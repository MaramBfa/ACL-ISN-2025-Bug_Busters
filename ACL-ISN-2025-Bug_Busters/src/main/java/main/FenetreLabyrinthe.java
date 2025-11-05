package main;

import entity.*;
import simple.Position;
import simple.Difficulty;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class FenetreLabyrinthe extends JPanel {
    private char[][] grille;
    private Heros hero;
    private ArrayList<Monstre> monstres;
    private ArrayList<Weapon> armes;
    private Cle cle;
    private Tresor tresor;
    private Ghost fantome;
    private Zombie zombie;
    private final int TAILLE_CASE = 40;
    private boolean partieTerminee = false;
    private boolean messageTresorAffiche = false;
    private long startTime;
    private Timer chronoTimer;
    private Timer timerMonstres;
    private JLabel chronoLabel;
    private Random rand = new Random();

    private Color wallColor, floorColor;
    private Image heroImg, monsterImg, ghostImg, zombieImg, keyImg, treasureImg, swordImg, bowImg;

    public FenetreLabyrinthe(char[][] grille, Heros hero,
                             ArrayList<Monstre> monstres,
                             Cle cle, Tresor tresor,
                             ArrayList<Weapon> armes,
                             Ghost fantome, Zombie zombie,
                             Difficulty difficulty) {
        this.grille = grille;
        this.hero = hero;
        this.monstres = monstres;
        this.cle = cle;
        this.tresor = tresor;
        this.armes = armes;
        this.fantome = fantome;
        this.zombie = zombie;

        heroImg = loadImage("/images/hero.png");
        monsterImg = loadImage("/images/monster.png");
        ghostImg = loadImage("/images/ghost.png");
        zombieImg = loadImage("/images/zombie.png");
        keyImg = loadImage("/images/key.png");
        treasureImg = loadImage("/images/treasure.png");
        swordImg = loadImage("/images/sword.png");
        bowImg = loadImage("/images/bow.png");

        switch (difficulty) {
            case EASY -> {
                wallColor = new Color(180, 220, 180);
                floorColor = new Color(240, 250, 220);
            }
            case MEDIUM -> {
                wallColor = new Color(150, 150, 150);
                floorColor = new Color(200, 200, 200);
            }
            case HARD -> {
                wallColor = new Color(60, 60, 60);
                floorColor = new Color(100, 100, 100);
            }
        }

        setPreferredSize(new Dimension(grille[0].length * TAILLE_CASE, grille.length * TAILLE_CASE + 60));
        setFocusable(true);
        setLayout(new BorderLayout());

        chronoLabel = new JLabel("Temps : 0 sec");
        chronoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        chronoLabel.setForeground(Color.BLUE);
        add(chronoLabel, BorderLayout.NORTH);

        startTime = System.currentTimeMillis();
        chronoTimer = new Timer(1000, e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            chronoLabel.setText("Temps : " + elapsed + " sec");
        });
        chronoTimer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                hero.deplacer(e.getKeyCode(), grille[0].length, grille.length, grille);
                verifierCollisions();
                repaint();
            }
        });

        timerMonstres = new Timer(500, e -> {
            if (!partieTerminee) {
                deplacerMonstres();
                fantome.move(grille[0].length, grille.length);
                zombie.moveTowards(new Position(hero.getX(), hero.getY()), grille);
                verifierCollisions();
                repaint();
            }
        });
        timerMonstres.start();
    }

    private Image loadImage(String path) {
        java.net.URL location = getClass().getResource(path);
        if (location == null) {
            BufferedImage img = new BufferedImage(TAILLE_CASE, TAILLE_CASE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, TAILLE_CASE, TAILLE_CASE);
            g.dispose();
            return img;
        }
        return new ImageIcon(location).getImage().getScaledInstance(TAILLE_CASE, TAILLE_CASE, Image.SCALE_SMOOTH);
    }

    private void deplacerMonstres() {
        for (Monstre m : monstres) {
            m.move(grille[0].length, grille.length);
        }
    }

    private void verifierCollisions() {
        if (partieTerminee) return;

        for (Monstre m : monstres) {
            if (!m.estVivant()) continue;
            Point pos = m.getPosition();
            if (hero.getX() == pos.x && hero.getY() == pos.y) {
                if (hero.aUneArme()) {
                    m.tuer();
                    hero.ajouterScore(50);
                } else {
                    hero.perdreVie();
                    if (hero.getPointsDeVie() <= 0) {
                        finDePartie("💀 Game Over !\nVoulez-vous rejouer ?", "Défaite");
                        return;
                    }
                }
            }
        }

        if (hero.getX() == fantome.getPos().x && hero.getY() == fantome.getPos().y) {
            hero.perdreVie();
            if (hero.getPointsDeVie() <= 0) {
                finDePartie("👻 Le fantôme vous a eu !\nVoulez-vous rejouer ?", "Défaite");
                return;
            }
        }

        if (hero.getX() == zombie.getPos().x && hero.getY() == zombie.getPos().y) {
            hero.perdreVie();
            if (hero.getPointsDeVie() <= 0) {
                finDePartie("🧟 Le zombie vous a attrapé !\nVoulez-vous rejouer ?", "Défaite");
                return;
            }
        }

        if (!cle.estRamassee() && hero.getX() == cle.getPos().x && hero.getY() == cle.getPos().y) {
            cle.ramasser();
            hero.pickKey();
            hero.ajouterScore(10);
        }

        for (Weapon w : armes) {
            if (!w.estRamassee() && hero.getX() == w.getPos().x && hero.getY() == w.getPos().y) {
                w.ramasser();
                hero.setWeapon(w.getType());
                hero.ajouterScore(20);
            }
        }

        if (hero.getX() == tresor.getPos().x && hero.getY() == tresor.getPos().y) {
            if (!hero.hasKey()) {
                if (!messageTresorAffiche) {
                    JOptionPane.showMessageDialog(this, "🔒 Le trésor est verrouillé ! Trouvez d'abord la clé.");
                    messageTresorAffiche = true;
                }
                return;
            }

            chronoTimer.stop();
            timerMonstres.stop();
            partieTerminee = true;

            long finalTime = (System.currentTimeMillis() - startTime) / 1000;
            int choix = JOptionPane.showConfirmDialog(this,
                "🎉 Vous avez gagné en " + finalTime + " secondes !\nScore final : " + hero.getScore() + "\n\nVoulez-vous rejouer ?",
                "Victoire", JOptionPane.YES_NO_OPTION);

            if (choix == JOptionPane.YES_OPTION) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                topFrame.dispose();
                MenuPrincipal.lancerNouvellePartie();
            } else {
                System.exit(0);
            }
        } else {
            messageTresorAffiche = false;
        }
    }

    private void finDePartie(String message, String titre) {
        chronoTimer.stop();
        timerMonstres.stop();
        partieTerminee = true;

        int choix = JOptionPane.showConfirmDialog(this, message, titre, JOptionPane.YES_NO_OPTION);
        if (choix == JOptionPane.YES_OPTION) {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            MenuPrincipal.lancerNouvellePartie();
        } else {
            System.exit(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < grille.length; x++) {
            for (int y = 0; y < grille[0].length; y++) {
                g.setColor(grille[x][y] == '#' ? wallColor : floorColor);
                g.fillRect(y * TAILLE_CASE, x * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
            }
        }

        if (!cle.estRamassee())
            g.drawImage(keyImg, cle.getPos().y * TAILLE

 package main;

import entity.*;
import simple.Position;
import simple.Difficulty; // ✅ Import ajouté

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class FenetreLabyrinthe extends JPanel {
    private static final long serialVersionUID = 1L;

    private char[][] grille;
    private Heros hero;
    private ArrayList<Position> monstres; 
    private ArrayList<Weapon> armes;
    private Cle cle;
    private Tresor tresor;
    private Ghost fantome;
    private Zombie zombie;
    private final int TAILLE_CASE = 40;
    private Random rand = new Random();

    // 🎨 Couleurs de thème (un seul mur et sol par niveau)
    private Color wallColor;
    private Color floorColor;

    // Autres images
    private Image heroImg, monsterImg, ghostImg, zombieImg, keyImg, treasureImg, swordImg, bowImg;

    private boolean messageTresorAffiche = false;
    private long startTime;
    private Timer chronoTimer;
    private JLabel chronoLabel;


    public FenetreLabyrinthe(char[][] grille, Heros hero,
                             ArrayList<Position> monstres,
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

        // === Chargement des images des entités ===
        heroImg = loadImage("/images/hero.png");
        monsterImg = loadImage("/images/monster.png");
        ghostImg = loadImage("/images/ghost.png");
        zombieImg = loadImage("/images/zombie.png");
        keyImg = loadImage("/images/key.png");
        treasureImg = loadImage("/images/treasure.png");
        swordImg = loadImage("/images/sword.png");
        bowImg = loadImage("/images/bow.png");

        // === Couleurs selon la difficulté ===
        switch (difficulty) {
            case EASY -> {
                wallColor = new Color(180, 220, 180); // vert clair
                floorColor = new Color(240, 250, 220); // beige
            }
            case MEDIUM -> {
                wallColor = new Color(150, 150, 150); // gris
                floorColor = new Color(200, 200, 200); // gris clair
            }
            case HARD -> {
                wallColor = new Color(60, 60, 60); // gris foncé
                floorColor = new Color(100, 100, 100); // anthracite
            }
        }

        // === Préparation de la fenêtre ===
        setPreferredSize(new Dimension(grille[0].length * TAILLE_CASE, grille.length * TAILLE_CASE + 60));
        setFocusable(true);
        startTime = System.currentTimeMillis();
        chronoLabel = new JLabel("Temps : 0 sec");
        chronoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        chronoLabel.setForeground(Color.BLUE);
        setLayout(new BorderLayout()); // nécessaire pour placer le chrono en haut
        add(chronoLabel, BorderLayout.NORTH);
        
        chronoTimer = new Timer(1000, e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            chronoLabel.setText("Temps : " + elapsed + " sec");
        });
        chronoTimer.start();


        // === Déplacement du héros via clavier ===
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                hero.deplacer(e.getKeyCode(), grille[0].length, grille.length, grille);
                verifierCollisions();
                repaint();
            }
        });

        // === Timer pour mouvement des monstres ===
        Timer timer = new Timer(500, e -> {
            deplacerMonstres();
            fantome.move(grille[0].length, grille.length);
            zombie.moveTowards(new Position(hero.getX(), hero.getY()), grille);
            verifierCollisions();
            repaint();
        });
        timer.start();
    }

    private Image loadImage(String path) {
        java.net.URL location = getClass().getResource(path);
        if (location == null) {
            // 🟡 Si l'image est manquante, on renvoie un carré de couleur de secours
            BufferedImage img = new BufferedImage(TAILLE_CASE, TAILLE_CASE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, TAILLE_CASE, TAILLE_CASE);
            g.dispose();
            System.err.println("⚠️ Image introuvable : " + path);
            return img;
        }
        Image img = new ImageIcon(location).getImage();
        return img.getScaledInstance(TAILLE_CASE, TAILLE_CASE, Image.SCALE_SMOOTH);
    }

    private void deplacerMonstres() {
        for (Position m : monstres) {
            int dir = rand.nextInt(4);
            int newX = m.x;
            int newY = m.y;

            switch (dir) {
                case 0 -> { if (m.x > 0 && grille[m.x - 1][m.y] != '#') newX--; }
                case 1 -> { if (m.x < grille.length - 1 && grille[m.x + 1][m.y] != '#') newX++; }
                case 2 -> { if (m.y > 0 && grille[m.x][m.y - 1] != '#') newY--; }
                case 3 -> { if (m.y < grille[0].length - 1 && grille[m.x][m.y + 1] != '#') newY++; }
            }
            m.x = newX;
            m.y = newY;
        }
    }

    private void verifierCollisions() {
        for (Position m : monstres) {
        if (hero.getX() == tresor.getPos().x && hero.getY() == tresor.getPos().y) {
            if (!hero.hasKey()) {
                if (!messageTresorAffiche) {
                    JOptionPane.showMessageDialog(this, "🔒 Le trésor est verrouillé ! Trouvez d'abord la clé.");
                    messageTresorAffiche = true;
                }
                return;
            }
        
            chronoTimer.stop(); // ⏱️ Arrête le chrono
            long finalTime = (System.currentTimeMillis() - startTime) / 1000;
        
            hero.ajouterScore(100);
            JOptionPane.showMessageDialog(this,
                "🎉 Vous avez gagné en " + finalTime + " secondes !\nScore final : " + hero.getScore(),
                "Victoire", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

                }
            }
        }

        if (hero.getX() == fantome.getPos().x && hero.getY() == fantome.getPos().y) {
            hero.perdreVie();
            if (hero.getPointsDeVie() <= 0) {
                JOptionPane.showMessageDialog(this, "👻 Le fantôme vous a eu !");
                System.exit(0);
            }
        }

        if (hero.getX() == zombie.getPos().x && hero.getY() == zombie.getPos().y) {
            hero.perdreVie();
            if (hero.getPointsDeVie() <= 0) {
                JOptionPane.showMessageDialog(this, "🧟 Le zombie vous a attrapé !");
                System.exit(0);
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

            hero.ajouterScore(100);
            JOptionPane.showMessageDialog(this, "🎉 Vous avez gagné ! Score final : " + hero.getScore());
            System.exit(0);
        } else {
            messageTresorAffiche = false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // === Dessin du labyrinthe avec couleurs différentes selon le niveau ===
        for (int x = 0; x < grille.length; x++) {
            for (int y = 0; y < grille[0].length; y++) {
                g.setColor(grille[x][y] == '#' ? wallColor : floorColor);
                g.fillRect(y * TAILLE_CASE, x * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
            }
        }

        // === Objets et entités ===
        if (!cle.estRamassee())
            g.drawImage(keyImg, cle.getPos().y * TAILLE_CASE, cle.getPos().x * TAILLE_CASE, this);

        for (Weapon w : armes)
            if (!w.estRamassee())
                g.drawImage((w.getType() == WeaponType.EPEE ? swordImg : bowImg),
                        w.getPos().y * TAILLE_CASE, w.getPos().x * TAILLE_CASE, this);

        g.drawImage(treasureImg, tresor.getPos().y * TAILLE_CASE, tresor.getPos().x * TAILLE_CASE, this);

        for (Position m : monstres)
            if (m.x >= 0 && m.y >= 0)
                g.drawImage(monsterImg, m.y * TAILLE_CASE, m.x * TAILLE_CASE, this);

        g.drawImage(ghostImg, fantome.getPos().y * TAILLE_CASE, fantome.getPos().x * TAILLE_CASE, this);
        g.drawImage(zombieImg, zombie.getPos().y * TAILLE_CASE, zombie.getPos().x * TAILLE_CASE, this);
        g.drawImage(heroImg, hero.getY() * TAILLE_CASE, hero.getX() * TAILLE_CASE, this);

        // === HUD ===
        int hudY = grille.length * TAILLE_CASE + 25;
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Vies :", 10, hudY);

        for (int i = 0; i < hero.getPointsDeVie(); i++) {
            g.setColor(Color.GREEN);
            g.fillRect(70 + i * 30, hudY - 15, 20, 20);
            g.setColor(Color.BLACK);
            g.drawRect(70 + i * 30, hudY - 15, 20, 20);
        }

        g.drawString("Score : " + hero.getScore(), 10, hudY + 30);
        if (hero.aUneArme()) g.drawString("Arme : " + hero.getWeapon(), 180, hudY + 30);
        if (hero.hasKey()) g.drawString("Clé : ✅", 320, hudY + 30);
    }
}

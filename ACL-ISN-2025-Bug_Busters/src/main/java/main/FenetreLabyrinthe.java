package main;

import entity.Heros;
import entity.Tresor;
import simple.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class FenetreLabyrinthe extends JPanel {
    private char[][] grille;
    private Heros hero;
    private Position monstre;
    private Tresor tresor;
    private final int TAILLE_CASE = 40;
    private Random rand = new Random();

    public FenetreLabyrinthe(char[][] grille, Heros hero, Position monstre, Tresor tresor) {
        this.grille = grille;
        this.hero = hero;
        this.monstre = monstre;
        this.tresor = tresor;

        // On ajoute un peu plus de hauteur pour la zone HUD (barre de vie + score)
        setPreferredSize(new Dimension(grille[0].length * TAILLE_CASE, grille.length * TAILLE_CASE + 60));
        setFocusable(true);

        // Déplacement du héros via clavier
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                hero.deplacer(e.getKeyCode(), grille[0].length, grille.length, grille);
                verifierCollision();
                repaint();
            }
        });

        // Timer pour déplacement automatique du monstre
        Timer timer = new Timer(500, e -> {
            deplacerMonstre();
            verifierCollision();
            repaint();
        });
        timer.start();
    }

    /** Déplacement aléatoire du monstre */
    private void deplacerMonstre() {
        int dir = rand.nextInt(4);
        int newX = monstre.x;
        int newY = monstre.y;

        switch (dir) {
            case 0: if (monstre.x > 0 && grille[monstre.x - 1][monstre.y] != '#') newX--; break;
            case 1: if (monstre.x < grille.length - 1 && grille[monstre.x + 1][monstre.y] != '#') newX++; break;
            case 2: if (monstre.y > 0 && grille[monstre.x][monstre.y - 1] != '#') newY--; break;
            case 3: if (monstre.y < grille[0].length - 1 && grille[monstre.x][monstre.y + 1] != '#') newY++; break;
        }

        monstre.x = newX;
        monstre.y = newY;
    }

    /** Vérification des collisions héros-monstre et héros-trésor */
    private void verifierCollision() {
        // Collision héros-monstre
        if (hero.getX() == monstre.x && hero.getY() == monstre.y) {
            hero.perdreVie();
            if (hero.getPointsDeVie() <= 0) {
                JOptionPane.showMessageDialog(this, "💀 Game Over !");
                System.exit(0);
            }
        }

        // Collision héros-trésor
        if (hero.getX() == tresor.getPos().x && hero.getY() == tresor.getPos().y) {
            hero.ajouterScore(100);
            JOptionPane.showMessageDialog(this, "🎉 Bravo ! Vous avez trouvé le trésor 🎉\nScore final : " + hero.getScore());
            System.exit(0);
        }
    }

    /** Dessin du labyrinthe, héros, monstre, trésor, barre de vie et score */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // === 1. Labyrinthe ===
        for (int x = 0; x < grille.length; x++) {
            for (int y = 0; y < grille[0].length; y++) {
                g.setColor(grille[x][y] == '#' ? Color.BLACK : Color.WHITE);
                g.fillRect(y * TAILLE_CASE, x * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
                g.setColor(Color.GRAY);
                g.drawRect(y * TAILLE_CASE, x * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
            }
        }

        // === 2. Héros ===
        g.setColor(Color.BLUE);
        g.fillOval(hero.getY() * TAILLE_CASE + 5, hero.getX() * TAILLE_CASE + 5, TAILLE_CASE - 10, TAILLE_CASE - 10);

        // === 3. Monstre ===
        g.setColor(Color.RED);
        g.fillOval(monstre.y * TAILLE_CASE + 5, monstre.x * TAILLE_CASE + 5, TAILLE_CASE - 10, TAILLE_CASE - 10);

        // === 4. Trésor ===
        g.setColor(Color.YELLOW);
        g.fillRect(tresor.getPos().y * TAILLE_CASE + 10, tresor.getPos().x * TAILLE_CASE + 10, TAILLE_CASE - 20, TAILLE_CASE - 20);

        // === 5. HUD (barre de vie + score) ===
        int hudY = grille.length * TAILLE_CASE + 25;

        // Label "Vies"
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Vies :", 10, hudY);

        // Dessiner des cœurs ou barres pour représenter les vies
        for (int i = 0; i < hero.getPointsDeVie(); i++) {
            g.setColor(Color.GREEN);
            g.fillRect(70 + i * 30, hudY - 15, 20, 20);
            g.setColor(Color.BLACK);
            g.drawRect(70 + i * 30, hudY - 15, 20, 20);
        }

        // Afficher le score
        g.setColor(Color.BLACK);
        g.drawString("Score : " + hero.getScore(), 10, hudY + 30);
    }
}

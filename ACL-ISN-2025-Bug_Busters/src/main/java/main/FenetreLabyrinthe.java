package main;

import entity.Heros;
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
    private final int TAILLE_CASE = 40;
    private Random rand = new Random();

    public FenetreLabyrinthe(char[][] grille, Heros hero, Position monstre) {
        this.grille = grille;
        this.hero = hero;
        this.monstre = monstre;

        setPreferredSize(new Dimension(grille[0].length * TAILLE_CASE, grille.length * TAILLE_CASE));
        setFocusable(true);

        // Déplacement du héros
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                hero.deplacer(e.getKeyCode(), grille[0].length, grille.length, grille);
                verifierCollision();
                repaint();
            }
        });

        // Timer pour déplacement du monstre
        Timer timer = new Timer(500, e -> {
            deplacerMonstre();
            verifierCollision();
            repaint();
        });
        timer.start();
    }

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

    private void verifierCollision() {
        if (hero.getX() == monstre.x && hero.getY() == monstre.y) {
            hero.perdreVie();
            if (hero.getPointsDeVie() <= 0) {
                JOptionPane.showMessageDialog(this, "Game Over !");
                System.exit(0);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessin labyrinthe
        for (int x = 0; x < grille.length; x++) {
            for (int y = 0; y < grille[0].length; y++) {
                g.setColor(grille[x][y] == '#' ? Color.BLACK : Color.WHITE);
                g.fillRect(y * TAILLE_CASE, x * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
                g.setColor(Color.GRAY);
                g.drawRect(y * TAILLE_CASE, x * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
            }
        }

        // Dessin héros
        g.setColor(Color.BLUE);
        g.fillOval(hero.getY() * TAILLE_CASE + 5, hero.getX() * TAILLE_CASE + 5, TAILLE_CASE - 10, TAILLE_CASE - 10);

        // Dessin monstre
        g.setColor(Color.RED);
        g.fillOval(monstre.y * TAILLE_CASE + 5, monstre.x * TAILLE_CASE + 5, TAILLE_CASE - 10, TAILLE_CASE - 10);

        // Affichage points de vie
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Vies : " + hero.getPointsDeVie(), 10, 20);
    }
}

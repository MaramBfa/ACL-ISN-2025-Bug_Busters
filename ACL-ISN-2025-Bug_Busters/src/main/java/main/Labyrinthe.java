package main;

import simple.Position;
import java.util.Random;

public class Labyrinthe {

    private char[][] grille;
    private int width, height;
    private Random rand = new Random();

    public Labyrinthe(int width, int height) {
        this.width = width;
        this.height = height;
        this.grille = new char[height][width];
        genererGrille();
    }

    /** Génération aléatoire de la grille */
    private void genererGrille() {
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                // Bordures = murs
                if (x == 0 || y == 0 || x == height - 1 || y == width - 1) {
                    grille[x][y] = '#';
                } else {
                    grille[x][y] = (rand.nextDouble() < 0.25) ? '#' : ' '; // 25% de murs
                }
            }
        }
    }

    public char[][] getGrille() { return grille; }

    /** Placer un élément aléatoirement sur une case vide */
    public Position placerAleatoirement() {
        Position pos;
        do {
            int x = rand.nextInt(height);
            int y = rand.nextInt(width);
            pos = new Position(x, y);
        } while (grille[pos.x][pos.y] != ' ');
        return pos;
    }
}

package entity;

import java.awt.Point;
import java.util.Random;

public class Monstre {
    private Point position;
    private boolean estVivant;
    private Random rand;

    public Monstre(int gridWidth, int gridHeight, int heroX, int heroY) {
        rand = new Random();
        estVivant = true;
        int x, y;
        do {
            x = rand.nextInt(gridHeight);
            y = rand.nextInt(gridWidth);
        } while (x == heroX && y == heroY);
        position = new Point(x, y);
    }

    // === Position ===
    public Point getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        position.x = x;
        position.y = y;
    }

    // === État de vie ===
    public boolean estVivant() {
        return estVivant;
    }

    public void tuer() {
        estVivant = false;
        position.x = -1;
        position.y = -1;
    }

    // === Déplacement aléatoire ===
    public void move(int gridWidth, int gridHeight) {
        if (!estVivant) return;

        int dir = rand.nextInt(4);
        switch (dir) {
            case 0 -> { if (position.y > 0) position.y--; } // gauche
            case 1 -> { if (position.y < gridWidth - 1) position.y++; } // droite
            case 2 -> { if (position.x > 0) position.x--; } // haut
            case 3 -> { if (position.x < gridHeight - 1) position.x++; } // bas
        }
    }
}

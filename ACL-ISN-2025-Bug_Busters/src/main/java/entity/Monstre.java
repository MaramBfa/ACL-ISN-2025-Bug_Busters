package entity;

import java.awt.Point;
import java.util.Random;

public class Monstre {
    private Point position;
    private boolean estVivant = true;
    private Random rand = new Random();

    public Monstre(int gridWidth, int gridHeight, int heroX, int heroY) {
        int x, y;
        do {
            x = rand.nextInt(gridWidth);
            y = rand.nextInt(gridHeight);
        } while (x == heroX && y == heroY);
        position = new Point(x, y);
    }

    public Point getPosition() {
        return position;
    }

    public boolean estVivant() {
        return estVivant;
    }

    public void tuer() {
        estVivant = false;
        position.x = -1;
        position.y = -1;
    }

    public void move(int gridWidth, int gridHeight) {
        if (!estVivant) return;
        int dir = rand.nextInt(4);
        switch (dir) {
            case 0: if (position.y > 0) position.y--; break;
            case 1: if (position.y < gridHeight - 1) position.y++; break;
            case 2: if (position.x > 0) position.x--; break;
            case 3: if (position.x < gridWidth - 1) position.x++; break;
        }
    }
}

package entity;

import java.awt.Point;
import java.util.Random;

public class Monstre {
    private Point position;
    private Random rand = new Random();

    public Monstre(int gridWidth, int gridHeight, int heroX, int heroY) {
        int x, y;
        do {
            x = rand.nextInt(gridWidth);
            y = rand.nextInt(gridHeight);
        } while (x == heroX && y == heroY);
        position = new Point(x, y);
    }

    public Point getPosition() { return position; }

    public void move(int gridWidth, int gridHeight) {
        int dir = rand.nextInt(4);
        switch (dir) {
            case 0: if (position.y > 0) position.y--; break;
            case 1: if (position.y < gridHeight - 1) position.y++; break;
            case 2: if (position.x > 0) position.x--; break;
            case 3: if (position.x < gridWidth - 1) position.x++; break;
        }
    }
}

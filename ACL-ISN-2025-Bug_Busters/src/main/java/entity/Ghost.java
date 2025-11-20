package entity;

import simple.Position;
import java.util.Random;

public class Ghost {
    private Position pos;
    private Random rand = new Random();

    public Ghost(Position pos) { this.pos = pos; }

    public Position getPos() { return pos; }

    public void move(int width, int height, char[][] grille) {
        int dir = rand.nextInt(4);
        int newX = pos.x;
        int newY = pos.y;

        switch (dir) {
            case 0 -> newX--; // Haut
            case 1 -> newX++; // Bas
            case 2 -> newY--; // Gauche
            case 3 -> newY++; // Droite
        }

        // Le fantôme peut traverser les murs : on ne teste QUE les limites
        if (newX >= 0 && newX < height && newY >= 0 && newY < width) {
            pos.x = newX;
            pos.y = newY;
        }
    }
}

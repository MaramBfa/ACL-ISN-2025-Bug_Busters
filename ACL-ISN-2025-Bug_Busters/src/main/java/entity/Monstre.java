package entity;

import simple.Position;
import java.util.Random;

public class Monstre {
    private Position position;
    private Random rand = new Random();

    public Monstre(Position pos) {
        this.position = pos;
    }
    
    public Monstre(int gridWidth, int gridHeight, int heroX, int heroY) {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(gridHeight);
            y = rand.nextInt(gridWidth);
        } while (x == heroX && y == heroY);
        position = new Position(x, y);
    }

    public Position getPosition() { return position; }

    public void move(int gridWidth, int gridHeight) {
        // La logique de déplacement est gérée par FenetreLabyrinthe.deplacerMonstres()
    }
}
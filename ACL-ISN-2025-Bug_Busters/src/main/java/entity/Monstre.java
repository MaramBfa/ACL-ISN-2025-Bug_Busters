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
        int x, y;
        do {
            x = rand.nextInt(gridHeight);
            y = rand.nextInt(gridWidth);
        } while (x == heroX && y == heroY);
        position = new Position(x, y);
    }

    // ⭐️ OBLIGATOIRE : getter
    public Position getPos() {
        return position;
    }

    // ⭐️ OBLIGATOIRE : setter
    public void setPos(Position pos) {
        this.position = pos;
    }

    public Position getPosition() {
        return position;
    }

    public void move(int gridWidth, int gridHeight) {
        // la logique est gérée ailleurs
    }
}

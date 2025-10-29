package entity;

import simple.Position;
import java.util.Random;

public class Ghost {
    private Position pos;
    private Random rand = new Random();

    public Ghost(Position pos) { this.pos = pos; }

    public Position getPos() { return pos; }

    public void move(int width, int height) {
        int dir = rand.nextInt(4);
        switch (dir) {
            case 0 -> pos.x = (pos.x - 1 + height) % height;
            case 1 -> pos.x = (pos.x + 1) % height;
            case 2 -> pos.y = (pos.y - 1 + width) % width;
            case 3 -> pos.y = (pos.y + 1) % width;
        }
    }
}

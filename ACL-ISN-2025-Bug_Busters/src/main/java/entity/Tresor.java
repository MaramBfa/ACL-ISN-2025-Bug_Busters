package entity;

import simple.Position;

public class Tresor {
    private Position pos;

    public Tresor(Position pos) {
        this.pos = pos;
    }

    public Position getPos() { return pos; }
    public void setPos(Position pos) { this.pos = pos; }
}

package entity;

import simple.Position;

public class Door {
    private Position pos;
    private boolean ouverte = false;

    public Door(Position pos) {
        this.pos = pos;
    }

    public Position getPos() { return pos; }
    public boolean estOuverte() { return ouverte; }
    public void ouvrir() { ouverte = true; }
}
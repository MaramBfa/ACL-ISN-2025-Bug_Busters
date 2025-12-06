package entity;

import simple.Position;

public class Cle {
    private Position pos;
    private boolean ramassee = false;

    public Cle(Position pos) {
        this.pos = pos;
    }

    public Position getPos() { return pos; }
    public boolean estRamassee() { return ramassee; }
    public void ramasser() { ramassee = true; }
}
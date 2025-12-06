package entity;

import simple.Position;

public class Heart {
    private Position pos;
    private boolean ramassee = false;

    public Heart(Position pos) {
        this.pos = pos;
    }

    public Position getPos() { return pos; }
    public boolean estRamassee() { return ramassee; }
    public void ramasser() { ramassee = true; }
}
package entity;

import simple.Position;

public class Tresor {
    private Position pos;

    public Tresor(Position pos) {
        this.pos = pos;
    }
    
    //retourne la position du trésor
    public Position getPos() { return pos; }
    //setter de la position du tresor
    public void setPos(Position pos) { this.pos = pos; }
}
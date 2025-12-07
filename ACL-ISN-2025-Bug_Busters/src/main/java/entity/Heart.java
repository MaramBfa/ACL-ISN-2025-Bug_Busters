package entity;

import simple.Position;

public class Heart {
    private Position pos;
    private boolean ramassee = false;
    
    //un coeur est placé aléatoirement dans la labyrithe c'est pour ça il a une position(points de vie)
    public Heart(Position pos) {
        this.pos = pos;
    }
    //retourenr la position du coeur(point de vie)
    public Position getPos() { return pos; }
    //VERIFIE SI LE HERO A PRIS LE COEUR
    public boolean estRamassee() { return ramassee; }
    public void ramasser() { ramassee = true; }
}
package entity;

import simple.Position;

public class Weapon {
    private Position pos;
    private WeaponType type;
    private boolean ramassee = false;

    //constructeur qui crée une arme à une position donnée avec un type précis que se soit arc ou épée
    public Weapon(Position pos, WeaponType type) {
        this.pos = pos;
        this.type = type;
    }
    //retourne la position actuelle de l'arme
    public Position getPos() { return pos; }
    //retourne le type de l'arme
    public WeaponType getType() { return type; }
    //indiquer si l'arme a été ramassée ou pas par l'héro
    public boolean estRamassee() { return ramassee; }
    //marquer l'arme ramassee
    public void ramasser() { ramassee = true; }
}
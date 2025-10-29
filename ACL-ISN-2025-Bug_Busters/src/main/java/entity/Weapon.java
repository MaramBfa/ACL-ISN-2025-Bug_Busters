package entity;

import simple.Position;

public class Weapon {
    private Position pos;
    private WeaponType type;
    private boolean ramassee = false;

    public Weapon(Position pos, WeaponType type) {
        this.pos = pos;
        this.type = type;
    }

    public Position getPos() { return pos; }
    public WeaponType getType() { return type; }
    public boolean estRamassee() { return ramassee; }
    public void ramasser() { ramassee = true; }
}

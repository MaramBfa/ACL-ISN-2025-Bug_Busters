package entity;

import simple.Position;

/**
 * Clone du boss.
 * - Moins puissant, généralement 1 PV
 * - Se déplace comme un petit boss
 */
public class BossClone {

    private Position pos;
    private int hp;
    private boolean alive = true;

    public BossClone(Position pos, int hp) {
        this.pos = pos;
        this.hp = hp;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public int getHp() {
        return hp;
    }

    public boolean isAlive() {
        return alive;
    }

    public void takeDamage(int amount) {
        if (!alive) return;
        hp -= amount;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
    }
}

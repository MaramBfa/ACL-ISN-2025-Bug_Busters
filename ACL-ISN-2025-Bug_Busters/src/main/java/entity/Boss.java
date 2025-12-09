package entity;

import simple.Position;

/**
 * Boss final du niveau 10.
 * - A des points de vie (hp)
 * - Se déplace dans le labyrinthe
 * - Ne génère PAS directement les clones (c’est FenetreLabyrinthe qui le fait)
 */
public class Boss {

    private Position pos;
    private int hp;
    private boolean alive = true;

    public Boss(Position pos, int hp) {
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

    /** Inflige des dégâts au boss. */
    public void takeDamage(int amount) {
        if (!alive) return;
        hp -= amount;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
    }
}

package entity;

import simple.Position;

public class Zombie {
    // Position actuelle du zombie dans le labyrinthe
    private Position pos;

    // Constructeur
    public Zombie(Position pos) {
        this.pos = pos;
    }

    // Getter
    public Position getPos() {
        return pos;
    }

    // Cette fonction permet au zombie de suivre le héros à une distance de 3 cases
    public void moveTowards(Position hero, char[][] grille) {
        if (distance(hero) < 3) { // si héros très proche
            int dx = Integer.compare(hero.x, pos.x); // direction X vers héros
            int dy = Integer.compare(hero.y, pos.y); // direction Y vers héros
            int newX = pos.x + dx; // nouvelle position x
            int newY = pos.y + dy; // nouvelle position y

            // Vérifie que la nouvelle position est dans les limites
            if (newX >= 0 && newX < grille.length && newY >= 0 && newY < grille[0].length) {
                // Vérifie que ce n'est pas un mur ET pas la case du héros
                if (grille[newX][newY] != '#' && !(newX == hero.x && newY == hero.y)) {
                    pos.x = newX;
                    pos.y = newY;
                }
            }
        }
    }

    // Calcule la distance entre le zombie et une position donnée
    private int distance(Position p) {
        return Math.abs(p.x - pos.x) + Math.abs(p.y - pos.y);
    }
}

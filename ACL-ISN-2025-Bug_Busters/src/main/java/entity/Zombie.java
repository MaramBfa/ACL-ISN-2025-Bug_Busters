package entity;

import simple.Position;

public class Zombie {
	//position actuelle du zombie dans la lapyrithe
    private Position pos;
    //constructeur qui place un zombie à une position donnee
    public Zombie(Position pos) { this.pos = pos; }
    //retourne la position du zombie(getter)
    public Position getPos() { return pos; }
    
    //cette fonction permet au zombie de suivre le héro à une distance de 3 cases:
    public void moveTowards(Position hero, char[][] grille) {
        if (distance(hero) < 3) { //si hero très proche
            int dx = Integer.compare(hero.x, pos.x); // direction X vers hero
            int dy = Integer.compare(hero.y, pos.y); //direction Y vers hero
            int newX = pos.x + dx;//nouvelle position x
            int newY = pos.y + dy;//nouvelle position y
            
            //vérification des limites (vérifie que la nouvelle case ne sort pas de la labyrinthe)
            if (newX >= 0 && newX < grille.length && newY >= 0 && newY < grille[0].length) {
                if (grille[newX][newY] != '#') {
                    pos.x = newX;
                    pos.y = newY;
                }
            }
        }
    }
    //calcule la distance entre le zombie et une position  donnee
    private int distance(Position p) {
        return Math.abs(p.x - pos.x) + Math.abs(p.y - pos.y);
    }
}
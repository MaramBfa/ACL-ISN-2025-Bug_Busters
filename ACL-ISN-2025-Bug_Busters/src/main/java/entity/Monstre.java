package entity;

import simple.Position;
import java.util.Random;

public class Monstre {

    private Position position;
    private Random rand = new Random();

    //constructeur qui place un monstre à une position donnée
    public Monstre(Position pos) {
        this.position = pos;
    }
    
    //constructeur qui place un monstre aléatoirement dans la grille en évitant la position du héros
    public Monstre(int gridWidth, int gridHeight, int heroX, int heroY) {
        int x, y;
        do {
            x = rand.nextInt(gridHeight);
            y = rand.nextInt(gridWidth);
        } while (x == heroX && y == heroY);
        position = new Position(x, y);
    }
    
    //retourne la position actuelle du monstre
    public Position getPos() {
        return position;
    }
    
    //changer la position actuelle du monstre
    public void setPos(Position pos) {
        this.position = pos;
    }

}

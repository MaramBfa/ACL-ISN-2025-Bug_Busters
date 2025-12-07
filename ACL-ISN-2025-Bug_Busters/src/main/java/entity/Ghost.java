package entity;

import simple.Position;
import java.util.Random;

public class Ghost {
    private Position pos;
    private Random rand = new Random();

    public Ghost(Position pos) { this.pos = pos;}

    public Position getPos() { return pos; }

    public void move(int width, int height, char[][] grille) {
        int dir = rand.nextInt(4);
        int newX = pos.x;
        int newY = pos.y;
        
        //deplacement 
        switch (dir) {
            case 0 -> newX--; //dépacement en haut
            case 1 -> newX++; //déplacement en bas
            case 2 -> newY--; //déplacement à gauche
            case 3 -> newY++; //déplacement à droite
        }

        //le fontome c'est le seul qui peut traverser les murs donc il faut juste verifier les bords
        if (newX >= 0 && newX < height && newY >= 0 && newY < width) {
            pos.x= newX;
            pos.y= newY;
        }
    }
}

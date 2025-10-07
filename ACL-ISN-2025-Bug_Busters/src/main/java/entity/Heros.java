package entity;

import java.awt.event.KeyEvent;

public class Heros {
    private int x, y;
    private String direction;
    private boolean collisionOn;
    private int score = 0;
    private int pointsDeVie = 3; // ajout des points de vie

    public Heros(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.direction = "down";
        this.collisionOn = false;
    }

    public void deplacer(int keyCode, int gridWidth, int gridHeight, char[][] grille) {
        if (!collisionOn) {
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    if (x > 0 && grille[x - 1][y] != '#') x--; direction = "up"; break;
                case KeyEvent.VK_DOWN:
                    if (x < gridHeight - 1 && grille[x + 1][y] != '#') x++; direction = "down"; break;
                case KeyEvent.VK_LEFT:
                    if (y > 0 && grille[x][y - 1] != '#') y--; direction = "left"; break;
                case KeyEvent.VK_RIGHT:
                    if (y < gridWidth - 1 && grille[x][y + 1] != '#') y++; direction = "right"; break;
            }
        }
    }

    // getters et setters
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public String getDirection() { return direction; }
    public void setCollisionOn(boolean collision) { this.collisionOn = collision; }

    public int getPointsDeVie() { return pointsDeVie; }
    public void perdreVie() {
        pointsDeVie--;
        if (pointsDeVie < 0) pointsDeVie = 0;
        System.out.println("Héros touché ! Vies restantes : " + pointsDeVie);
    }

    public void ajouterScore(int points) { score += points; }
    public void enleverScore(int points) { score = Math.max(0, score - points); }
    public void afficherScore() { System.out.println("Score : " + score); }
}

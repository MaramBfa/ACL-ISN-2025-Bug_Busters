package entity;

import java.awt.event.KeyEvent;
import java.util.List;

public class Heros {
    private int x, y;
    private String direction;
    private boolean collisionOn;
    private int score = 0;
    private int pointsDeVie = 3;
    private WeaponType weapon = null;
    private boolean hasKey = false;

    public Heros(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.direction = "down";
        this.collisionOn = false;
    }

    /** Déplacement du héros selon les touches du clavier */
    public void deplacer(int keyCode, int gridWidth, int gridHeight, char[][] grille) {
        if (!collisionOn) {
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    if (x > 0 && grille[x - 1][y] != '#') x--;
                    direction = "up";
                    break;
                case KeyEvent.VK_DOWN:
                    if (x < gridHeight - 1 && grille[x + 1][y] != '#') x++;
                    direction = "down";
                    break;
                case KeyEvent.VK_LEFT:
                    if (y > 0 && grille[x][y - 1] != '#') y--;
                    direction = "left";
                    break;
                case KeyEvent.VK_RIGHT:
                    if (y < gridWidth - 1 && grille[x][y + 1] != '#') y++;
                    direction = "right";
                    break;
            }
        }
    }

    // === Getters & setters ===
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public String getDirection() { return direction; }

    // === Vie ===
    public int getPointsDeVie() { return pointsDeVie; }
    public void perdreVie() { if (pointsDeVie > 0) pointsDeVie--; }

    // === Score ===
    public void ajouterScore(int points) { score += points; }
    public void enleverScore(int points) { score = Math.max(0, score - points); }
    public int getScore() { return score; }

    // === Arme ===
    public boolean aUneArme() { return weapon != null; }
    public void setWeapon(WeaponType w) { weapon = w; }
    public WeaponType getWeapon() { return weapon; }

    // === Clé ===
    public boolean hasKey() { return hasKey; }
    public void pickKey() { hasKey = true; }

    // === Attaque ===
    public boolean attaquer(List<Position> monstres) {
        if (!aUneArme()) return false; // Pas d'arme → pas d’attaque

        int targetX = x;
        int targetY = y;

        switch (direction) {
            case "up" -> targetX--;
            case "down" -> targetX++;
            case "left" -> targetY--;
            case "right" -> targetY++;
        }

        Position monstreTouche = null;

        for (Position m : monstres) {
            if (weapon == WeaponType.EPEE) {
                if (m.x == targetX && m.y == targetY) {
                    monstreTouche = m;
                    break;
                }
            } else if (weapon == WeaponType.ARC) {
                if (m.x == x || m.y == y) {
                    monstreTouche = m;
                    break;
                }
            }
        }

        if (monstreTouche != null) {
            monstres.remove(monstreTouche);
            ajouterScore(50);
            System.out.println(" Monstre éliminé !");
            return true;
        }

        return false;
    }
}

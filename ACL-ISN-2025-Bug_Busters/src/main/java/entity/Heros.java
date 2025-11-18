package entity;

import java.awt.event.KeyEvent;
import main.FenetreLabyrinthe;

public class Heros {
    private int x, y;
    private String direction;
    private boolean collisionOn;
    private int score = 0;
    private int pointsDeVie = 3; // Augmenté à 5 au lieu de 3
    private final int MAX_POINTS_DE_VIE = 5;
    private WeaponType weapon = WeaponType.AUCUNE;
    private boolean hasKey = false;
    private FenetreLabyrinthe fenetreActuelle;

    public Heros(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.direction = "down";
        this.collisionOn = false;
    }

    public void setFenetreActuelle(FenetreLabyrinthe fenetre) {
        this.fenetreActuelle = fenetre;
    }

    public FenetreLabyrinthe getFenetreActuelle() {
        return fenetreActuelle;
    }

    /** Déplacement du héros selon les touches du clavier */
    public void deplacer(int keyCode, int gridWidth, int gridHeight, char[][] grille) {
        int HEIGHT = grille.length;
        int WIDTH = grille[0].length;
        
        if (!collisionOn) {
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    if (x > 0 && grille[x - 1][y] != '#') x--;
                    direction = "up";
                    break;
                case KeyEvent.VK_DOWN:
                    if (x < HEIGHT - 1 && grille[x + 1][y] != '#') x++; 
                    direction = "down";
                    break;
                case KeyEvent.VK_LEFT:
                    if (y > 0 && grille[x][y - 1] != '#') y--;
                    direction = "left";
                    break;
                case KeyEvent.VK_RIGHT:
                    if (y < WIDTH - 1 && grille[x][y + 1] != '#') y++; 
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
    public void setDirection(String direction) { this.direction = direction; }
    
    public boolean isCollisionOn() { return collisionOn; }
    public void setCollisionOn(boolean collisionOn) { this.collisionOn = collisionOn; }

    // === Vie ===
    public int getPointsDeVie() { return pointsDeVie; }
    public void setPointsDeVie(int pointsDeVie) { 
        this.pointsDeVie = Math.min(pointsDeVie, MAX_POINTS_DE_VIE); 
    }
    
    public void perdreVie() { 
        if (pointsDeVie > 0) pointsDeVie--; 
    }
    
    public void perdreVie(int degats) { 
        pointsDeVie = Math.max(0, pointsDeVie - degats); 
    }
    
    /** Ajoute 1 point de vie, plafonné à MAX_POINTS_DE_VIE */
    public void ajouterVie() {
        if (pointsDeVie < MAX_POINTS_DE_VIE) pointsDeVie++;
    }
    
    /** Ajoute un nombre spécifique de points de vie */
    public void ajouterVie(int quantite) {
        pointsDeVie = Math.min(MAX_POINTS_DE_VIE, pointsDeVie + quantite);
    }
    
    public boolean estVivant() {
        return pointsDeVie > 0;
    }
    
    // === Score ===
    public void ajouterScore(int points) { score += points; }
    public void enleverScore(int points) { score = Math.max(0, score - points); }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    // === Arme ===
    public boolean aUneArme() { 
        return weapon != null && weapon != WeaponType.AUCUNE; 
    }
    
    public void setWeapon(WeaponType w) { weapon = w; }
    public WeaponType getWeapon() { return weapon; }
    
    public void retirerArme() { 
        weapon = WeaponType.AUCUNE; 
    }

    // === Clé ===
    public boolean hasKey() { return hasKey; }
    public void pickKey() { hasKey = true; }
    public void useKey() { hasKey = false; }
    public void setHasKey(boolean hasKey) { this.hasKey = hasKey; }

    // === Méthodes utilitaires ===
    public void resetPosition(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.direction = "down";
    }
    
    public void reinitialiserStats() {
        this.pointsDeVie = 3;
        this.score = 0;
        this.weapon = WeaponType.AUCUNE;
        this.hasKey = false;
        this.collisionOn = false;
    }
    
    public void reinitialiserComplet(int startX, int startY) {
        resetPosition(startX, startY);
        reinitialiserStats();
    }
    
    /**
     * Vérifie si le héros est à une position spécifique
     */
    public boolean estAPosition(int posX, int posY) {
        return this.x == posX && this.y == posY;
    }
    
    /**
     * Retourne la position devant le héros selon sa direction
     */
    public int[] getPositionDevant() {
        int[] positionDevant = new int[]{x, y};
        
        switch (direction) {
            case "up" -> positionDevant[0] = x - 1;
            case "down" -> positionDevant[0] = x + 1;
            case "left" -> positionDevant[1] = y - 1;
            case "right" -> positionDevant[1] = y + 1;
        }
        
        return positionDevant;
    }
    
    /**
     * Retourne la distance de Manhattan entre le héros et une position donnée
     */
    public int distanceDe(int posX, int posY) {
        return Math.abs(x - posX) + Math.abs(y - posY);
    }
    
    @Override
    public String toString() {
        return String.format("Heros[Position=(%d,%d), Vie=%d/%d, Score=%d, Arme=%s, Clé=%s]", 
                           x, y, pointsDeVie, MAX_POINTS_DE_VIE, score, weapon, hasKey ? "OUI" : "NON");
    }
}
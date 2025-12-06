package entity;

import java.awt.event.KeyEvent;
import main.FenetreLabyrinthe;

/**
 * Classe Heros – gestion des vies, score, clé et armes (épée + arc).
 */
public class Heros {

    private int x, y;
    private String direction;
    private boolean collisionOn;
    private int score = 0;
    private int pointsDeVie = 3;
    private final int MAX_POINTS_DE_VIE = 5;

    // les armes sont indépendantes , le hero peut les avoir séparement
    private boolean aEpee = false;
    private boolean aArc = false;

    private int usagesEpeeRestants = 0;
    private int usagesArcRestants = 0;

    private boolean hasKey = false;
    private FenetreLabyrinthe fenetreActuelle;

    // Arme actuellement sélectionnée (pour l’attaque / HUD)
    private WeaponType weaponSelected = WeaponType.NONE;

    public Heros(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.direction = "down";
        this.collisionOn = false;
    }

    // Fenêtre actuelle (pour remonter jusqu'au JFrame)
    public void setFenetreActuelle(FenetreLabyrinthe fenetre) {
        this.fenetreActuelle = fenetre;
    }

    public FenetreLabyrinthe getFenetreActuelle() {
        return fenetreActuelle;
    }

    // Déplacement
    public void deplacer(int keyCode, int gridWidth, int gridHeight, char[][] grille) {
        int HEIGHT = grille.length;
        int WIDTH = grille[0].length;

        if (!collisionOn) {
            switch (keyCode) {

                // ZQSD
                case KeyEvent.VK_Z:
                    if (x > 0 && grille[x - 1][y] != '#') x--;
                    direction = "up";
                    break;
                case KeyEvent.VK_S:
                    if (x < HEIGHT - 1 && grille[x + 1][y] != '#') x++;
                    direction = "down";
                    break;
                case KeyEvent.VK_Q:
                    if (y > 0 && grille[x][y - 1] != '#') y--;
                    direction = "left";
                    break;
                case KeyEvent.VK_D:
                    if (y < WIDTH - 1 && grille[x][y + 1] != '#') y++;
                    direction = "right";
                    break;

                // Flèches
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

    // Getters & setters de base
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public boolean isCollisionOn() { return collisionOn; }
    public void setCollisionOn(boolean collisionOn) { this.collisionOn = collisionOn; }

    // Gestion de la vie
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

    public void ajouterVie() {
        if (pointsDeVie < MAX_POINTS_DE_VIE) pointsDeVie++;
    }

    public void ajouterVie(int quantite) {
        pointsDeVie = Math.min(MAX_POINTS_DE_VIE, pointsDeVie + quantite);
    }

    public boolean estVivant() {
        return pointsDeVie > 0;
    }

    // Score
    public void ajouterScore(int points) { score += points; }
    public void enleverScore(int points) { score = Math.max(0, score - points); }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    // Armes
    public void ramasserEpee() {
        aEpee = true;
        usagesEpeeRestants = 1;
        if (weaponSelected == WeaponType.NONE) {
            weaponSelected = WeaponType.EPEE;
        }
    }

    public void ramasserArc() {
        aArc = true;
        usagesArcRestants = 1;
        if (weaponSelected == WeaponType.NONE) {
            weaponSelected = WeaponType.ARC;
        }
    }

    public boolean aEpee() { return aEpee; }
    public boolean aArc() { return aArc; }

    public int getUsagesEpeeRestants() { return usagesEpeeRestants; }
    public int getUsagesArcRestants() { return usagesArcRestants; }

    public boolean peutUtiliserEpee() {
        return aEpee && usagesEpeeRestants > 0;
    }

    public boolean peutUtiliserArc() {
        return aArc && usagesArcRestants > 0;
    }

    public void consommerEpee() {
        if (usagesEpeeRestants > 0) usagesEpeeRestants--;
        if (usagesEpeeRestants == 0) aEpee = false;
    }

    public void consommerArc() {
        if (usagesArcRestants > 0) usagesArcRestants--;
        if (usagesArcRestants == 0) aArc = false;
    }

    // Arme sélectionnée (utilisée dans FenetreLabyrinthe)
    public void setWeapon(WeaponType w) {
        this.weaponSelected = w;
    }

    public WeaponType getWeapon() {
        return weaponSelected;
    }

    public boolean aUneArme() {
        return weaponSelected != WeaponType.NONE;
    }

    // Clé & reset
    public boolean hasKey() { return hasKey; }
    public void pickKey() { hasKey = true; }
    public void useKey() { hasKey = false; }
    public void setHasKey(boolean hasKey) { this.hasKey = hasKey; }

    public void resetPosition(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.direction = "down";
    }

    public void reinitialiserStats() {
        this.pointsDeVie = 3;
        this.score = 0;
        this.hasKey = false;
        this.collisionOn = false;

        this.aEpee = false;
        this.aArc = false;
        this.usagesEpeeRestants = 0;
        this.usagesArcRestants = 0;
        this.weaponSelected = WeaponType.NONE;
    }

    public void reinitialiserComplet(int startX, int startY) {
        resetPosition(startX, startY);
        reinitialiserStats();
    }

    // Utilitaires
    public boolean estAPosition(int posX, int posY) {
        return this.x == posX && this.y == posY;
    }

    public int[] getPositionDevant() {
        int[] p = {x, y};
        switch (direction) {
            case "up" -> p[0] = x - 1;
            case "down" -> p[0] = x + 1;
            case "left" -> p[1] = y - 1;
            case "right" -> p[1] = y + 1;
        }
        return p;
    }

    public int distanceDe(int posX, int posY) {
        return Math.abs(x - posX) + Math.abs(y - posY);
    }

    @Override
    public String toString() {
        return String.format(
                "Heros[(%d,%d), Vie=%d/%d, Score=%d, Epee=%d, Arc=%d, Clé=%s, ArmeActive=%s]",
                x, y, pointsDeVie, MAX_POINTS_DE_VIE, score,
                usagesEpeeRestants, usagesArcRestants,
                hasKey ? "OUI" : "NON",
                weaponSelected
        );
    }
 // ----------- RESET DES ARMES POUR CHANGER DE NIVEAU -----------
    public void resetArmes() {
        this.aEpee = false;
        this.usagesEpeeRestants = 0;

        this.aArc = false;
        this.usagesArcRestants = 0;

        this.weaponSelected = WeaponType.NONE;
    }

}



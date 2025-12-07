package entity;

import java.awt.event.KeyEvent;
import main.FenetreLabyrinthe;

// cette classe represente le hero du jeu , ici on gère sa position, ses vies, son score, ses armes
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

    // chaqune des armes ne peut être utilisée qu'une seule fois durant un niveau
    private int usagesEpeeRestants = 0;
    private int usagesArcRestants = 0;

    private boolean hasKey = false;
    private FenetreLabyrinthe fenetreActuelle;

    //arme actuellement sélectionnée (pour l’attaque / et pour l'affichage dans HUD)
    //au début du jeu, aucune arme n’est sélectionnée
    private WeaponType weaponSelected = WeaponType.NONE;

    public Heros(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.direction = "down";
        this.collisionOn = false;
    }

    //fenêtre actuelle (pour remonter jusqu'au JFrame)
    public void setFenetreActuelle(FenetreLabyrinthe fenetre) {
        this.fenetreActuelle = fenetre;
    }
    //permet d'appeler la fenetre
    public FenetreLabyrinthe getFenetreActuelle() {
        return fenetreActuelle;
    }

    // Déplacement
    public void deplacer(int keyCode, int gridWidth, int gridHeight, char[][] grille) {
        int HEIGHT = grille.length;
        int WIDTH = grille[0].length;

        if (!collisionOn) {
            switch (keyCode) {

                // déplacement avec ZQSD
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

                //dépalcement avec les flèches
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

    // getters et setters
    //renvoyer la position X du héros dans la grille
    public int getX() { return x; }
    //renvoyer la position Y du héros dans la grille
    public int getY() { return y; }
    //modifier la position X du héros
    public void setX(int x) { this.x = x; }
    //modifier la position X du héros
    public void setY(int y) { this.y = y; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public boolean isCollisionOn() { return collisionOn; }
    public void setCollisionOn(boolean collisionOn) { this.collisionOn = collisionOn; }

    //ici on gère les points de vie 
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
    //
    public void ajouterVie() {
        if (pointsDeVie < MAX_POINTS_DE_VIE) pointsDeVie++;
    }
    /*il faut verifier de ne pas depasser le nombre maximal de points de vies si par
     * exemple on va ajouter une potion qui ajoute plusieurs vies à la fois
     * cette fonction n'est pas encore utilisée on prevoit l'utilisé avec une potion
     */
    
    public void ajouterVie(int quantite) {
        pointsDeVie = Math.min(MAX_POINTS_DE_VIE, pointsDeVie + quantite);
    }

    public boolean estVivant() {
        return pointsDeVie > 0;
    }

    //calcul de score 
    
    //ajoute des points au score du héros
    public void ajouterScore(int points) { score += points; }
    //enlever des points au score du héros sans aller en dessous de 0
    public void enleverScore(int points) { score = Math.max(0, score - points); }
    //getter et setter du score
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    // Armes
    
    //quand le hero   ramasse une epee :
    //il peux l utiliser qu'une seule fois
    //si il avai pas d' arme on met l'epee comme arme active
    public void ramasserEpee() {
        aEpee = true;
        usagesEpeeRestants = 1;
        if (weaponSelected == WeaponType.NONE) {
            weaponSelected = WeaponType.EPEE;
        }
    }
    // meme truc que epee mais pour l arc :
    // le hero a un seul arc
    // encore 1 utilisation unique
    // si aucune arme avant alors on met l arc en arme selectionée
    public void ramasserArc() {
        aArc = true;
        usagesArcRestants = 1;
        if (weaponSelected == WeaponType.NONE) {
            weaponSelected = WeaponType.ARC;
        }
    }

    //renvoyer si hero possde épée
    public boolean aEpee() { return aEpee; }
    //renvoyer si hero possède arc
    public boolean aArc() { return aArc; }

    //renvoie le nombre d'utilisations restantes pour l'épée (soit 0 soit 1)
    public int getUsagesEpeeRestants() { return usagesEpeeRestants; }
    //renvoie le nombre d'utilisations restantes pour l'arc (soit 0 soit 1)
    public int getUsagesArcRestants() { return usagesArcRestants; }

    // verifie possibilité utilisation épée
    public boolean peutUtiliserEpee() {
        return aEpee && usagesEpeeRestants > 0;
    }
    // verifie possibilité utilisation arc
    public boolean peutUtiliserArc() {
        return aArc && usagesArcRestants > 0;
    }
    //utilisation épée
    public void consommerEpee() {
        if (usagesEpeeRestants > 0) usagesEpeeRestants--;
        if (usagesEpeeRestants == 0) aEpee = false;
    }
    
    //utilisation arc
    public void consommerArc() {
        if (usagesArcRestants > 0) usagesArcRestants--;
        if (usagesArcRestants == 0) aArc = false;
    }

    //change l'arme selectionnée
    public void setWeapon(WeaponType w) {
        this.weaponSelected = w;
    }
    
    //renvoie l'arme sélétionnée
    public WeaponType getWeapon() {
        return weaponSelected;
    }
    //renvoie true si une arme est selectionnée
    public boolean aUneArme() {
        return weaponSelected != WeaponType.NONE;
    }

    // Clé & reset
    //indique si le héros possède une clé
    public boolean hasKey() { return hasKey; }
    //le héros ramasse une clé
    public void pickKey() { hasKey = true; }
    //le héros utilise la clé (elle disparaît)
    public void useKey() { hasKey = false; }
    public void setHasKey(boolean hasKey) { this.hasKey = hasKey; }

    //replacer le héros au début d'un niveau
    public void resetPosition(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.direction = "down";
    }
    // Réinitialise toutes les statistiques du hhero au début de la nouvelle partie
    public void reinitialiserStats() {
        this.pointsDeVie = 3;
        this.score = 0;
        this.hasKey = false;
        this.collisionOn = false;
        //on enlève toutes les armes et leurs usages au début de chaque partie
        this.aEpee = false;
        this.aArc = false;
        this.usagesEpeeRestants = 0;
        this.usagesArcRestants = 0;
        this.weaponSelected = WeaponType.NONE;
    }
    // Vérifier position du hero est exacte
    public void reinitialiserComplet(int startX, int startY) {
        resetPosition(startX, startY);
        reinitialiserStats();
    }

    // Utilitaires
    public boolean estAPosition(int posX, int posY) {
        return this.x == posX && this.y == posY;
    }
    /*retourner la case juste devant le héros, en fonction de sa direction
     * C’est essentiel pour les attaques, les interactions, l’ouverture de portes, 
     * et toutes les actions qui doivent se faire dans la direction où il regarde
     */
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
    //retourne une description complète de l'état du héros
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
 // Réinitialiser les armes (utilisé lors d'un changement de niveau)
    public void resetArmes() {
        this.aEpee = false;
        this.usagesEpeeRestants = 0;

        this.aArc = false;
        this.usagesArcRestants = 0;

        this.weaponSelected = WeaponType.NONE;
    }

}



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Labyrinthe {

    private char[][] grille;
    private int lignes;
    private int colonnes;

    public static final char MUR = '#';
    public static final char VIDE = ' ';
    public static final char HERO = 'H';
    public static final char MONSTRE = 'M';
    public static final char TRESOR = 'T';

    
    public Labyrinthe(int lignes, int colonnes) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.grille = new char[lignes][colonnes];
        genererLabyrintheDefaut();
    }

    /**
     * Constructeur pour charger un labyrinthe à partir d’un fichier texte
     */
    public Labyrinthe(String nomFichier) throws IOException {
        chargerDepuisFichier(nomFichier);
    }

    /**
     * Génère un labyrinthe simple par défaut
     */
    private void genererLabyrintheDefaut() {
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if (i == 0 || j == 0 || i == lignes - 1 || j == colonnes - 1)
                    grille[i][j] = MUR;
                else
                    grille[i][j] = VIDE;
            }
        }
        // Placer le héros et le trésor
        grille[1][1] = HERO;
        grille[lignes - 2][colonnes - 2] = TRESOR;
    }

    /**
     * Charge un labyrinthe depuis un fichier texte
     */
    private void chargerDepuisFichier(String nomFichier) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(nomFichier));
        String ligne;
        int i = 0;

        // Lire toutes les lignes du fichier
        grille = new char[50][50]; // taille max temporaire
        while ((ligne = br.readLine()) != null) {
            colonnes = ligne.length();
            for (int j = 0; j < colonnes; j++) {
                grille[i][j] = ligne.charAt(j);
            }
            i++;
        }
        lignes = i;
        br.close();
    }

    /**
     * Affiche le labyrinthe dans la console
     */
    public void afficher() {
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                System.out.print(grille[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Vérifie si une case est libre (non mur)
     */
    public boolean estLibre(int x, int y) {
        if (x < 0 || y < 0 || x >= lignes || y >= colonnes) return false;
        return grille[x][y] != MUR;
    }

    /**
     * Déplace le héros dans une direction (haut, bas, gauche, droite)
     */
    public void deplacerHero(String direction) {
        int heroX = -1, heroY = -1;

        // Trouver le héros
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if (grille[i][j] == HERO) {
                    heroX = i;
                    heroY = j;
                    break;
                }
            }
        }

        if (heroX == -1) return; // pas de héros trouvé

        int nx = heroX, ny = heroY;
        switch (direction.toLowerCase()) {
            case "haut": nx--; break;
            case "bas": nx++; break;
            case "gauche": ny--; break;
            case "droite": ny++; break;
        }

        // Vérifie si la nouvelle position est libre
        if (estLibre(nx, ny)) {
            grille[heroX][heroY] = VIDE;
            grille[nx][ny] = HERO;
        }
    }

    // Getters
    public int getLignes() { return lignes; }
    public int getColonnes() { return colonnes; }
}

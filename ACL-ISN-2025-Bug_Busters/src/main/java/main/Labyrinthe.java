package main;

import simple.Position;
import simple.Level;
import java.util.*;

public class Labyrinthe {
    private final int width; //largeur labyrinthe
    private final int height;//hauteur labyrinthe
    private char[][] grille; //tableau qui contient les murs '#' et les chemins ' '
    private final Random rand = new Random();

    //constructeur qui permet de crée un labyrinthe à partir des infos du niveau (taille + densité des murs)
    public Labyrinthe(Level level) {
        this.width = level.width;
        this.height = level.height;
        this.grille = new char[height][width];
        genererLabyrintheGaranti(level.wallDensity);//on génère directement le  labyrinthe jouable
    }
    //renvoyer la grille finale pour l'utiliser dans fenertreLAbyrinthe 
    public char[][] getGrille() {
        return grille;
    }
//fonction qui génère le labyrinthe
    private void genererLabyrintheGaranti(double wallDensity) {
    	//au début on remplit tout en murs pour partir de zéro
        for (int i = 0; i < height; i++) Arrays.fill(grille[i], '#');
        //puis: on génère un labyrinthe "parfait" (une seule solution entre deux points)
        genererLabyrintheParfait();
        //puis on ajoute plus de murs selon la densité voulue
        ajouterMursSupplementaires(wallDensity);
        //au final on vérifie qu'on peut visiter presque tout le labyrinthe (pas de zones bloquées)
        garantirAccessibiliteComplete();
    }
    
    //algorithme pour générer un labyrinthe parfait
    private void genererLabyrintheParfait() {
        List<Position> murs = new ArrayList<>();//liste des murs à tester pour creuser
        Set<Position> cellules = new HashSet<>();//ensemble des cases déjà creusées

        //on commence au centre du labyrinthe
        Position start = new Position(height / 2, width / 2);
        cellules.add(start);
        grille[start.x][start.y] = ' ';//on commence par un chemin

        //on ajoute les murs autour de la cellule de départ
        ajouterMursAdjacents(start, murs);
        
        //on boucle tant que il reste des murs à traiter
        while (!murs.isEmpty()) {
        	//on prend un mur au hazard
            Position mur = murs.remove(rand.nextInt(murs.size()));
            //on regarde combien de cellules vides il touche
            List<Position> adj = getCellulesAdjacentes(mur);
            //règle du labyrinthe parfait : on ne casse un mur que s'il relie deux zones distinctes
            if (adj.size() == 1) {
            	//on creuse le mur
                grille[mur.x][mur.y] = ' ';
                //on creuse la cellule opposée pour créer un passage complet
                Position nouvelle = getCelluleOpposee(mur, adj.get(0));
                //on vérifie qu'elle est dans les limites

                if (nouvelle.x > 0 && nouvelle.x < height - 1 &&
                        nouvelle.y > 0 && nouvelle.y < width - 1) {

                    grille[nouvelle.x][nouvelle.y] = ' ';
                    cellules.add(nouvelle);
                    //on ajoute les murs autour de cette nouvelle cellule
                    ajouterMursAdjacents(nouvelle, murs);
                }
            }
        }
    }
    //ajoute les murs voisins d'une cellule dans la liste des murs à tester
    private void ajouterMursAdjacents(Position pos, List<Position> murs) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int nx = pos.x + dx[i];
            int ny = pos.y + dy[i];
            //on vérifie que c'est un mur valide dans la grille
            if (nx > 0 && nx < height - 1 &&
                ny > 0 && ny < width - 1 &&
                grille[nx][ny] == '#') {

                Position mur = new Position(nx, ny);
                if (!murs.contains(mur)) murs.add(mur);
            }
        }
    }
    //on retourne toutes les cellules (chemins) adjacentes à un mur
    private List<Position> getCellulesAdjacentes(Position mur) {
        List<Position> list = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int nx = mur.x + dx[i];
            int ny = mur.y + dy[i];
            
            //si c'est un chemin on le garde
            if (nx >= 0 && nx < height &&
                ny >= 0 && ny < width &&
                grille[nx][ny] == ' ') {
                list.add(new Position(nx, ny));
            }
        }
        return list;
    }
    //renvoyer la cellule qui se trouve "derrière" le mur pour former un passage
    private Position getCelluleOpposee(Position mur, Position cellule) {
        int dx = mur.x - cellule.x;
        int dy = mur.y - cellule.y;
        return new Position(mur.x + dx, mur.y + dy);
    }

    //ajoute des murs aléatoires tout en vérifiant qu'on ne bloque pas le labyrinthe
    private void ajouterMursSupplementaires(double wallDensity) {
        int totalCells = (height - 2) * (width - 2);
        int currentWalls = compterMurs() - (2 * height + 2 * width - 4);
        int targetWalls = (int) (totalCells * wallDensity);

        List<Position> candidats = new ArrayList<>();
        //on cherche des cases libres où on peut ajouter des murs sans bloquer le chemin
        for (int i = 2; i < height - 2; i++) {
            for (int j = 2; j < width - 2; j++) {
                if (grille[i][j] == ' ' && peutAjouterMurSansBloquer(i, j))
                    candidats.add(new Position(i, j));
            }
        }

        Collections.shuffle(candidats, rand);

        int index = 0;
        while (currentWalls < targetWalls && index < candidats.size()) {
            Position p = candidats.get(index);
            grille[p.x][p.y] = '#';
            currentWalls++;
            index++;
        }
    }
    //on verifie si ajouter un mur ici ne coupe pas un passage essentiel
    private boolean peutAjouterMurSansBloquer(int x, int y) {
        int libres = 0;
        if (grille[x-1][y] == ' ') libres++;
        if (grille[x+1][y] == ' ') libres++;
        if (grille[x][y-1] == ' ') libres++;
        if (grille[x][y+1] == ' ') libres++;
        //si la case a au moins 3 voisins libres, on peut la bloquer
        return libres >= 3;
    }
    //s'assure que le joueur peut accéder à presque toutes les zones du labyrinthe
    private void garantirAccessibiliteComplete() {
        Position centre = new Position(height / 2, width / 2);
        Set<Position> acc = trouverZonesAccessibles(centre);

        int total = compterSols();
        //si moins de 90% du labyrinthe est accessible, on corrige
        if (acc.size() < total * 0.9)
            corrigerAccessibilite(centre, acc);
    }
    //répare les zones inaccessibles en creusant des chemins
    private void corrigerAccessibilite(Position centre, Set<Position> acc) {
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                if (grille[i][j] == ' ' && !acc.contains(new Position(i, j)))
                    creerCheminVersZonePrincipale(centre, new Position(i, j));
            }
        }
    }
    //creuse un chemin direct (ligne brisée) entre une zone isolée et la zone principale
    private void creerCheminVersZonePrincipale(Position from, Position to) {
        int x = from.x;
        int y = from.y;
        //on va avancer vers la zone isolée case par case
        while (x != to.x || y != to.y) {
            if (Math.abs(to.x - x) > Math.abs(to.y - y))
                x += Integer.compare(to.x, x);
            else
                y += Integer.compare(to.y, y);

            if (x > 0 && x < height - 1 && y > 0 && y < width - 1)
                grille[x][y] = ' ';
        }
    }
    //compte le nombre de murs dans toute la grille
    private int compterMurs() {
        int c = 0;
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                if (grille[i][j] == '#') c++;
        return c;
    }
    //compte les cases sol dans l'intérieur du labyrinthe
    private int compterSols() {
        int c = 0;
        for (int i = 1; i < height - 1; i++)
            for (int j = 1; j < width - 1; j++)
                if (grille[i][j] == ' ') c++;
        return c;
    }

    // placer un objet dans une zone libre, accessible,
    // et suffisamment loin du joueur
    public Position placerLoinDeAccessible(Position refPos,
                                           int minDistance,
                                           Set<Position> occupees,
                                           Set<Position> accessibles) {

        List<Position> candidats = new ArrayList<>();

        for (Position p : accessibles) {

            if (occupees.contains(p)) continue;

            int dist = Math.abs(refPos.x - p.x) + Math.abs(refPos.y - p.y);

            if (dist >= minDistance) {
                candidats.add(p);
            }
        }

        if (candidats.isEmpty() && minDistance > 0)
            return placerLoinDeAccessible(refPos, minDistance - 1, occupees, accessibles);

        if (candidats.isEmpty()) {
            for (Position p : accessibles)
                if (!occupees.contains(p)) return p;

            return new Position(height - 2, width - 2);
        }

        return candidats.get(rand.nextInt(candidats.size()));
    }

 
    //bfs pour savoir quelles zones sont accessibles depuis le centre du labyrinthe
    public Set<Position> trouverZonesAccessibles(Position startPos) {
        Set<Position> acc = new HashSet<>();
        Queue<Position> q = new LinkedList<>();
        //si le départ est dans un mur alors aucune zone accessible
        if (grille[startPos.x][startPos.y] == '#') return acc;

        q.add(startPos);
        acc.add(startPos);

        int[] dr = {-1,1,0,0};
        int[] dc = {0,0,-1,1};

        while (!q.isEmpty()) {
            Position p = q.poll();

            for (int i = 0; i < 4; i++) {
                int nr = p.x + dr[i];
                int nc = p.y + dc[i];

                if (nr >= 0 && nr < height &&
                    nc >= 0 && nc < width &&
                    grille[nr][nc] != '#') {

                    Position next = new Position(nr, nc);

                    if (!acc.contains(next)) {
                        acc.add(next);
                        q.add(next);
                    }
                }
            }
        }
        return acc;
    }
}

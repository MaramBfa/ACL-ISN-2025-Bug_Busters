package main;

import simple.Position;
import simple.Level;
import java.util.*;

public class Labyrinthe {
    private final int width;
    private final int height;
    private char[][] grille;
    private final Random rand = new Random();

    public Labyrinthe(Level level) {
        this.width = level.width;
        this.height = level.height;
        this.grille = new char[height][width];
        genererLabyrintheGaranti(level.wallDensity);
    }

    public char[][] getGrille() {
        return grille;
    }

    private void genererLabyrintheGaranti(double wallDensity) {
        // Initialiser avec tous les murs
        for (int i = 0; i < height; i++) {
            Arrays.fill(grille[i], '#');
        }

        // Générer un labyrinthe parfait avec l'algorithme de Prim
        genererLabyrintheParfait();

        // Ajouter des murs supplémentaires selon la densité
        ajouterMursSupplementaires(wallDensity);

        // GARANTIR que toutes les zones sont accessibles depuis le centre
        garantirAccessibiliteComplete();
    }

    private void genererLabyrintheParfait() {
        List<Position> murs = new ArrayList<>();
        Set<Position> cellules = new HashSet<>();
        
        // Commencer avec la cellule de départ au centre
        Position start = new Position(height / 2, width / 2);
        cellules.add(start);
        grille[start.x][start.y] = ' ';
        
        // Ajouter les murs adjacents à la cellule de départ
        ajouterMursAdjacents(start, murs);
        
        while (!murs.isEmpty()) {
            // Choisir un mur aléatoire
            Position mur = murs.remove(rand.nextInt(murs.size()));
            
            // Trouver les cellules adjacentes à ce mur
            List<Position> cellulesAdjacentes = getCellulesAdjacentes(mur);
            
            // Si exactement une cellule est déjà dans le labyrinthe
            if (cellulesAdjacentes.size() == 1) {
                // Casser le mur
                grille[mur.x][mur.y] = ' ';
                
                // Trouver la cellule de l'autre côté du mur
                Position nouvelleCellule = getCelluleOpposee(mur, cellulesAdjacentes.get(0));
                
                // Ajouter la nouvelle cellule au labyrinthe
                if (nouvelleCellule.x > 0 && nouvelleCellule.x < height - 1 && 
                    nouvelleCellule.y > 0 && nouvelleCellule.y < width - 1) {
                    grille[nouvelleCellule.x][nouvelleCellule.y] = ' ';
                    cellules.add(nouvelleCellule);
                    
                    // Ajouter les murs adjacents à la nouvelle cellule
                    ajouterMursAdjacents(nouvelleCellule, murs);
                }
            }
        }
    }

    private void ajouterMursAdjacents(Position pos, List<Position> murs) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        
        for (int i = 0; i < 4; i++) {
            int newX = pos.x + dx[i];
            int newY = pos.y + dy[i];
            
            if (newX > 0 && newX < height - 1 && newY > 0 && newY < width - 1 && 
                grille[newX][newY] == '#') {
                Position mur = new Position(newX, newY);
                if (!murs.contains(mur)) {
                    murs.add(mur);
                }
            }
        }
    }

    private List<Position> getCellulesAdjacentes(Position mur) {
        List<Position> cellules = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        
        for (int i = 0; i < 4; i++) {
            int newX = mur.x + dx[i];
            int newY = mur.y + dy[i];
            
            if (newX >= 0 && newX < height && newY >= 0 && newY < width && 
                grille[newX][newY] == ' ') {
                cellules.add(new Position(newX, newY));
            }
        }
        return cellules;
    }

    private Position getCelluleOpposee(Position mur, Position cellule) {
        int dx = mur.x - cellule.x;
        int dy = mur.y - cellule.y;
        return new Position(mur.x + dx, mur.y + dy);
    }

    private void ajouterMursSupplementaires(double wallDensity) {
        int totalCells = (height - 2) * (width - 2);
        int currentWalls = compterMurs() - (2 * height + 2 * width - 4); // Exclure les bords
        int targetWalls = (int) (totalCells * wallDensity);
        
        // Liste des positions où on peut potentiellement ajouter des murs
        List<Position> positionsPotentielles = new ArrayList<>();
        
        // Prioriser les positions qui ne créent pas d'impasses
        for (int i = 2; i < height - 2; i++) {
            for (int j = 2; j < width - 2; j++) {
                if (grille[i][j] == ' ' && peutAjouterMurSansBloquer(i, j)) {
                    positionsPotentielles.add(new Position(i, j));
                }
            }
        }
        
        // Mélanger les positions
        Collections.shuffle(positionsPotentielles, rand);
        
        // Ajouter des murs jusqu'à atteindre la densité souhaitée
        int index = 0;
        while (currentWalls < targetWalls && index < positionsPotentielles.size()) {
            Position pos = positionsPotentielles.get(index);
            grille[pos.x][pos.y] = '#';
            currentWalls++;
            index++;
        }
        
        // Si on n'a pas atteint la densité, ajouter des murs moins optimaux
        if (currentWalls < targetWalls) {
            for (int i = 2; i < height - 2; i++) {
                for (int j = 2; j < width - 2; j++) {
                    if (grille[i][j] == ' ' && currentWalls < targetWalls) {
                        grille[i][j] = '#';
                        currentWalls++;
                    }
                }
            }
        }
    }

    private boolean peutAjouterMurSansBloquer(int x, int y) {
        // Vérifier qu'il y a au moins 3 directions libres autour
        int passagesLibres = 0;
        if (grille[x-1][y] == ' ') passagesLibres++;
        if (grille[x+1][y] == ' ') passagesLibres++;
        if (grille[x][y-1] == ' ') passagesLibres++;
        if (grille[x][y+1] == ' ') passagesLibres++;
        
        return passagesLibres >= 3; // Au moins 3 passages pour ne pas créer d'impasse
    }

    private void garantirAccessibiliteComplete() {
        Position centre = new Position(height / 2, width / 2);
        Set<Position> accessibles = trouverZonesAccessibles(centre);
        
        // Vérifier si au moins 90% des cases sol sont accessibles
        int totalSols = compterSols();
        int accessiblesCount = accessibles.size();
        
        System.out.println("🔍 Accessibilité : " + accessiblesCount + "/" + totalSols + " cases accessibles");
        
        if (accessiblesCount < totalSols * 0.9) {
            System.out.println("🔄 Correction de l'accessibilité...");
            corrigerAccessibilite(centre, accessibles);
        }
    }

    private void corrigerAccessibilite(Position centre, Set<Position> accessibles) {
        // Créer des chemins vers les zones isolées
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                if (grille[i][j] == ' ') {
                    Position pos = new Position(i, j);
                    if (!accessibles.contains(pos)) {
                        // Cette case est isolée, créer un chemin
                        creerCheminVersZonePrincipale(centre, pos);
                    }
                }
            }
        }
    }

    private void creerCheminVersZonePrincipale(Position from, Position to) {
        // Créer un chemin en ligne droite avec quelques variations
        int currentX = from.x;
        int currentY = from.y;
        
        // Chemin en deux étapes : d'abord horizontal, puis vertical
        while (currentX != to.x || currentY != to.y) {
            // Prioriser la direction qui réduit le plus la distance
            int distX = Math.abs(to.x - currentX);
            int distY = Math.abs(to.y - currentY);
            
            if (distX > distY) {
                // Se déplacer horizontalement
                if (currentX < to.x) currentX++;
                else currentX--;
            } else {
                // Se déplacer verticalement
                if (currentY < to.y) currentY++;
                else currentY--;
            }
            
            // S'assurer que la position est dans les limites
            if (currentX > 0 && currentX < height - 1 && currentY > 0 && currentY < width - 1) {
                grille[currentX][currentY] = ' ';
                
                // Élargir légèrement le chemin pour éviter les couloirs trop étroits
                if (rand.nextDouble() < 0.3) { // 30% de chance d'élargir
                    if (currentX > 1) grille[currentX-1][currentY] = ' ';
                    if (currentX < height - 2) grille[currentX+1][currentY] = ' ';
                    if (currentY > 1) grille[currentX][currentY-1] = ' ';
                    if (currentY < width - 2) grille[currentX][currentY+1] = ' ';
                }
            }
            
            // Éviter les boucles infinies
            if (Math.abs(currentX - from.x) > height || Math.abs(currentY - from.y) > width) {
                break;
            }
        }
    }

    private int compterMurs() {
        int count = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (grille[i][j] == '#') count++;
            }
        }
        return count;
    }

    private int compterSols() {
        int count = 0;
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                if (grille[i][j] == ' ') count++;
            }
        }
        return count;
    }

    public Position placerAccessible(Set<Position> occupees, Set<Position> accessibles) {
        List<Position> candidates = new ArrayList<>();
        
        for (Position p : accessibles) {
            if (!occupees.contains(p)) { 
                candidates.add(p);
            }
        }

        if (candidates.isEmpty()) {
            // Fallback : chercher n'importe quelle position libre
            for (int r = 1; r < height - 1; r++) {
                for (int c = 1; c < width - 1; c++) {
                    if (grille[r][c] == ' ') {
                        Position pos = new Position(r, c);
                        if (!occupees.contains(pos)) {
                            occupees.add(pos);
                            return pos;
                        }
                    }
                }
            }
            // Dernier recours : position aléatoire
            int x, y;
            do {
                x = rand.nextInt(height - 2) + 1;
                y = rand.nextInt(width - 2) + 1;
            } while (grille[x][y] == '#' || occupees.contains(new Position(x, y)));
            Position pos = new Position(x, y);
            occupees.add(pos);
            return pos;
        }

        Position selectedPos = candidates.get(rand.nextInt(candidates.size()));
        occupees.add(selectedPos);
        return selectedPos;
    }
    
    public Position placerLoinDe(Position refPos) {
        List<Position> candidates = new ArrayList<>();
        int maxDistance = -1;

        for (int r = 1; r < height - 1; r++) {
            for (int c = 1; c < width - 1; c++) {
                if (grille[r][c] == ' ') {
                    Position currentPos = new Position(r, c);
                    int distance = Math.abs(r - refPos.x) + Math.abs(c - refPos.y);
                    
                    if (distance > maxDistance) {
                        maxDistance = distance;
                        candidates.clear();
                        candidates.add(currentPos);
                    } else if (distance == maxDistance) {
                        candidates.add(currentPos);
                    }
                }
            }
        }
        
        if (candidates.isEmpty()) {
            return new Position(height - 2, width - 2);
        }
        
        return candidates.get(rand.nextInt(candidates.size()));
    }
    
    public Set<Position> trouverZonesAccessibles(Position startPos) {
        Set<Position> accessible = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();

        if (startPos.x < 0 || startPos.x >= height || startPos.y < 0 || startPos.y >= width || grille[startPos.x][startPos.y] == '#') {
            return accessible;
        }

        queue.add(startPos);
        accessible.add(startPos);

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            for (int i = 0; i < 4; i++) {
                int newR = current.x + dr[i];
                int newC = current.y + dc[i];

                if (newR >= 0 && newR < height && newC >= 0 && newC < width) {
                    Position neighbor = new Position(newR, newC);
                    
                    if (grille[newR][newC] != '#' && !accessible.contains(neighbor)) {
                        accessible.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }
        return accessible;
    }
}
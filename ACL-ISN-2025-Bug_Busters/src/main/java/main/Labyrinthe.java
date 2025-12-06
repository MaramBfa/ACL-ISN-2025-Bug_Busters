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
        for (int i = 0; i < height; i++) Arrays.fill(grille[i], '#');
        genererLabyrintheParfait();
        ajouterMursSupplementaires(wallDensity);
        garantirAccessibiliteComplete();
    }

    private void genererLabyrintheParfait() {
        List<Position> murs = new ArrayList<>();
        Set<Position> cellules = new HashSet<>();

        Position start = new Position(height / 2, width / 2);
        cellules.add(start);
        grille[start.x][start.y] = ' ';

        ajouterMursAdjacents(start, murs);

        while (!murs.isEmpty()) {
            Position mur = murs.remove(rand.nextInt(murs.size()));
            List<Position> adj = getCellulesAdjacentes(mur);

            if (adj.size() == 1) {
                grille[mur.x][mur.y] = ' ';
                Position nouvelle = getCelluleOpposee(mur, adj.get(0));

                if (nouvelle.x > 0 && nouvelle.x < height - 1 &&
                        nouvelle.y > 0 && nouvelle.y < width - 1) {

                    grille[nouvelle.x][nouvelle.y] = ' ';
                    cellules.add(nouvelle);
                    ajouterMursAdjacents(nouvelle, murs);
                }
            }
        }
    }

    private void ajouterMursAdjacents(Position pos, List<Position> murs) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int nx = pos.x + dx[i];
            int ny = pos.y + dy[i];

            if (nx > 0 && nx < height - 1 &&
                ny > 0 && ny < width - 1 &&
                grille[nx][ny] == '#') {

                Position mur = new Position(nx, ny);
                if (!murs.contains(mur)) murs.add(mur);
            }
        }
    }

    private List<Position> getCellulesAdjacentes(Position mur) {
        List<Position> list = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int nx = mur.x + dx[i];
            int ny = mur.y + dy[i];

            if (nx >= 0 && nx < height &&
                ny >= 0 && ny < width &&
                grille[nx][ny] == ' ') {
                list.add(new Position(nx, ny));
            }
        }
        return list;
    }

    private Position getCelluleOpposee(Position mur, Position cellule) {
        int dx = mur.x - cellule.x;
        int dy = mur.y - cellule.y;
        return new Position(mur.x + dx, mur.y + dy);
    }

    private void ajouterMursSupplementaires(double wallDensity) {
        int totalCells = (height - 2) * (width - 2);
        int currentWalls = compterMurs() - (2 * height + 2 * width - 4);
        int targetWalls = (int) (totalCells * wallDensity);

        List<Position> candidats = new ArrayList<>();

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

    private boolean peutAjouterMurSansBloquer(int x, int y) {
        int libres = 0;
        if (grille[x-1][y] == ' ') libres++;
        if (grille[x+1][y] == ' ') libres++;
        if (grille[x][y-1] == ' ') libres++;
        if (grille[x][y+1] == ' ') libres++;
        return libres >= 3;
    }

    private void garantirAccessibiliteComplete() {
        Position centre = new Position(height / 2, width / 2);
        Set<Position> acc = trouverZonesAccessibles(centre);

        int total = compterSols();
        if (acc.size() < total * 0.9)
            corrigerAccessibilite(centre, acc);
    }

    private void corrigerAccessibilite(Position centre, Set<Position> acc) {
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                if (grille[i][j] == ' ' && !acc.contains(new Position(i, j)))
                    creerCheminVersZonePrincipale(centre, new Position(i, j));
            }
        }
    }

    private void creerCheminVersZonePrincipale(Position from, Position to) {
        int x = from.x;
        int y = from.y;

        while (x != to.x || y != to.y) {
            if (Math.abs(to.x - x) > Math.abs(to.y - y))
                x += Integer.compare(to.x, x);
            else
                y += Integer.compare(to.y, y);

            if (x > 0 && x < height - 1 && y > 0 && y < width - 1)
                grille[x][y] = ' ';
        }
    }

    private int compterMurs() {
        int c = 0;
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                if (grille[i][j] == '#') c++;
        return c;
    }

    private int compterSols() {
        int c = 0;
        for (int i = 1; i < height - 1; i++)
            for (int j = 1; j < width - 1; j++)
                if (grille[i][j] == ' ') c++;
        return c;
    }

    // -------------------------------------------------------------------------
    // 🎯 NOUVELLE FONCTION PRINCIPALE : ACCESSIBLE + ANTI-COLLISION + DISTANCE
    // -------------------------------------------------------------------------
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

    // ancienne fonction (maximum distance)
    public Position placerLoinDe(Position refPos) {
        List<Position> candidats = new ArrayList<>();
        int maxDist = -1;

        for (int r = 1; r < height - 1; r++) {
            for (int c = 1; c < width - 1; c++) {
                if (grille[r][c] == ' ') {
                    int d = Math.abs(r - refPos.x) + Math.abs(c - refPos.y);
                    if (d > maxDist) {
                        maxDist = d;
                        candidats.clear();
                        candidats.add(new Position(r, c));
                    } else if (d == maxDist) {
                        candidats.add(new Position(r, c));
                    }
                }
            }
        }

        if (candidats.isEmpty())
            return new Position(height - 2, width - 2);

        return candidats.get(rand.nextInt(candidats.size()));
    }

    // BFS pour accessibilité
    public Set<Position> trouverZonesAccessibles(Position startPos) {
        Set<Position> acc = new HashSet<>();
        Queue<Position> q = new LinkedList<>();

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

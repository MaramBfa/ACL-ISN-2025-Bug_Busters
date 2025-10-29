package main;

import simple.Position;
import java.util.*;

public class Labyrinthe {
    private char[][] grille;
    private int width, height;
    private Random rand = new Random();

    public Labyrinthe(int width, int height) {
        this.width = width;
        this.height = height;
        genererGrilleJouable();
    }

    /** Génère un labyrinthe aléatoire mais jouable */
    private void genererGrilleJouable() {
        boolean ok = false;
        while (!ok) {
            genererGrilleAleatoire();
            Position start = new Position(height / 2, width / 2);
            Set<Position> accessibles = trouverZonesAccessibles(start);
            ok = accessibles.size() > (width * height) / 3; // labyrinthe assez ouvert
        }
    }

    /** Génère une grille brute */
    private void genererGrilleAleatoire() {
        grille = new char[height][width];
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                if (x == 0 || y == 0 || x == height - 1 || y == width - 1)
                    grille[x][y] = '#';
                else
                    grille[x][y] = (rand.nextDouble() < 0.25) ? '#' : ' ';
            }
        }
    }

    /** BFS : retourne toutes les cases accessibles depuis un point donné */
    public Set<Position> trouverZonesAccessibles(Position start) {
        Set<Position> visited = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();
        if (grille[start.x][start.y] == '#') return visited;

        queue.add(start);
        visited.add(start);

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            Position p = queue.poll();
            for (int i = 0; i < 4; i++) {
                int nx = p.x + dx[i];
                int ny = p.y + dy[i];
                Position np = new Position(nx, ny);
                if (nx >= 0 && nx < height && ny >= 0 && ny < width &&
                        grille[nx][ny] != '#' && !visited.contains(np)) {
                    visited.add(np);
                    queue.add(np);
                }
            }
        }
        return visited;
    }

    /** Place un élément sur une case libre et accessible */
    public Position placerAccessible(Set<Position> occupees, Set<Position> accessibles) {
        List<Position> libres = new ArrayList<>();
        for (Position p : accessibles) {
            if (grille[p.x][p.y] != '#' && !occupees.contains(p)) {
                libres.add(p);
            }
        }
        if (libres.isEmpty()) throw new RuntimeException("Pas de case accessible !");
        Position pos = libres.get(rand.nextInt(libres.size()));
        occupees.add(pos);
        return pos;
    }

    public char[][] getGrille() {
        return grille;
    }
}

package simple;

import java.util.*;

public class Labyrinthe {
    private char[][] grille;

    public Labyrinthe(char[][] grille) {
        this.grille = grille;
    }

    public Position placerAleatoirement() {
        List<Position> casesLibres = new ArrayList<>();
        for (int x = 0; x < grille.length; x++) {
            for (int y = 0; y < grille[0].length; y++) {
                if (grille[x][y] == ' ') {
                    casesLibres.add(new Position(x, y));
                }
            }
        }
        if (casesLibres.isEmpty()) return null;
        return casesLibres.get(new Random().nextInt(casesLibres.size()));
    }

}

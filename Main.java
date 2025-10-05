package simple;

public class Main {
    public static void main(String[] args) {
        char[][] grille = {
            {'#', '#', '#'},
            {'#', ' ', '#'},
            {'#', ' ', '#'}
        };

        Labyrinthe lab = new Labyrinthe(grille);
        Position pos = lab.placerAleatoirement();
        System.out.println("Position libre : (" + pos.x + ", " + pos.y + ")");
    }
}

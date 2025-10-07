public class Main {
    public static void main(String[] args) {
        Labyrinthe laby = new Labyrinthe(8, 10);
        laby.afficher();

        System.out.println("\n➡️ Le héros se déplace à droite :");
        laby.deplacerHero("droite");
        laby.afficher();
    }
}

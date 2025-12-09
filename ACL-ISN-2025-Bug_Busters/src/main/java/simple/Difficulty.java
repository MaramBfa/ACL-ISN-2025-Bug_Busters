package simple;

public enum Difficulty {
    //on garde EASY/MEDIUM/HARD comme before mais on ne l'utilise plus pour niveaux fixes
    EASY(10, 10, 2),
    MEDIUM(10, 15, 4),
    HARD(15,15, 6);

    public final int width;
    public final int height;
    public final int nbMonstres;

    Difficulty(int width, int height, int nbMonstres) {
        this.width = width;
        this.height = height;
        this.nbMonstres = nbMonstres;
    }
}
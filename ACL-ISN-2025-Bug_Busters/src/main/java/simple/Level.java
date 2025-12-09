package simple;

public enum Level {
    //densité des murs réduite pour les niveaux avancés
    LEVEL_1(15, 15, 0.20, 2, 4, true, 1),
    LEVEL_2(15, 15, 0.25, 3, 5, true, 1),
    LEVEL_3(15, 15, 0.30, 4, 6, true, 2),
    LEVEL_4(15, 15, 0.35, 5, 7, true, 2),
    LEVEL_5(15, 15, 0.38, 6, 8, true, 3),
    LEVEL_6(15, 15, 0.40, 7, 9, true, 3),
    LEVEL_7(15, 15, 0.42, 8, 10, true, 4),
    LEVEL_8(15, 15, 0.38, 9, 11, true, 4),  //densité REDUITE (0.38 au lieu de 0.45)
    LEVEL_9(15, 15, 0.40, 10, 12, true, 5), //densité REDUITE (0.40 au lieu de 0.48)
    LEVEL_10(15, 15, 0.42, 12, 15, false, 5); //densité REDUITE (0.42 au lieu de 0.50)

    public final int width;
    public final int height;
    public final double wallDensity;
    public final int minMonsters;
    public final int maxMonsters;
    public final boolean hasWeapons;
    public final int nbCoeurs;

    Level(int height, int width, double wallDensity, int minMonsters, int maxMonsters, boolean hasWeapons, int nbCoeurs) {
        this.height = height;
        this.width = width;
        this.wallDensity = wallDensity;
        this.minMonsters = minMonsters;
        this.maxMonsters = maxMonsters;
        this.hasWeapons = hasWeapons;
        this.nbCoeurs = nbCoeurs;
    }
}
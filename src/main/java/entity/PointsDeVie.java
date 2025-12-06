package entity;

public class PointsDeVie {
    private int points;

    public PointsDeVie(int points) { this.points = points; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public void enlever(int p) { points = Math.max(0, points - p); }
    public void ajouter(int p) { points += p; }
}
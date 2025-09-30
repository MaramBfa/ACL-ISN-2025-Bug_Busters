package entity;

public class Heros {

    public int worldX, worldY;
    public int speed;
    public String direction;
    public boolean collisionOn;

    public Heros() {
        setDefaultValues();
    }

    public void setDefaultValues() {
        worldX = 0; // Position de départ X
        worldY = 0; // Position de départ Y
        speed = 2;
        direction = "down";
    }

    public void deplacer() {
        // Déplacer en fonction de la direction
        if (!collisionOn) {
            switch (direction) {
                case "up":
                    worldY -= speed;
                    break;
                case "down":
                    worldY += speed;
                    break;
                case "left":
                    worldX -= speed;
                    break;
                case "right":
                    worldX += speed;
                    break;
            }
        }
    }
}

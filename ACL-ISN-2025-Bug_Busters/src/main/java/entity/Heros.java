package entity;

import java.awt.event.KeyEvent;

public class Heros {
    private int x, y;   // position du héros en cases
    private String direction;
    private boolean collisionOn;

    public Heros(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.direction = "down";
        this.collisionOn = false;
    }

    // Signature attendue par Maze: deplacer(int keyCode, int gridWidth, int gridHeight)
    public void deplacer(int keyCode, int gridWidth, int gridHeight) {
        if (!collisionOn) {
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    if (y > 0) y--;
                    direction = "up";
                    break;
                case KeyEvent.VK_DOWN:
                    if (y < gridHeight - 1) y++;
                    direction = "down";
                    break;
                case KeyEvent.VK_LEFT:
                    if (x > 0) x--;
                    direction = "left";
                    break;
                case KeyEvent.VK_RIGHT:
                    if (x < gridWidth - 1) x++;
                    direction = "right";
                    break;
            }
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getDirection() { return direction; }

    public void setCollisionOn(boolean collision) {
        this.collisionOn = collision;
    }
}

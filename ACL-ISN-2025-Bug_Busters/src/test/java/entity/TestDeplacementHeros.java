package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.event.KeyEvent;
import static org.junit.jupiter.api.Assertions.*;

class TestDeplacementHeros {
    private Heros heros;
    private char[][] grille;
    
    @BeforeEach
    void setUp() {
        heros = new Heros(5, 5);
        
        // Créer une grille simple 10x10 avec un mur au centre
        grille = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grille[i][j] = ' ';
            }
        }
        // Ajouter quelques murs
        grille[4][5] = '#';
        grille[6][5] = '#';
        grille[5][4] = '#';
        grille[5][6] = '#';
    }
    
    @Test
    void testDeplacementHaut() {
        heros.deplacer(KeyEvent.VK_UP, 10, 10, grille);
        assertEquals(4, heros.getX());
        assertEquals(5, heros.getY());
        assertEquals("up", heros.getDirection());
    }
    
    @Test
    void testDeplacementHautMur() {
        // Positionner devant un mur
        heros.setX(5);
        heros.setY(5);
        grille[4][5] = '#';
        
        heros.deplacer(KeyEvent.VK_UP, 10, 10, grille);
        assertEquals(5, heros.getX()); // Ne bouge pas
        assertEquals(5, heros.getY());
    }
    
    @Test
    void testDeplacementBas() {
        heros.deplacer(KeyEvent.VK_DOWN, 10, 10, grille);
        assertEquals(6, heros.getX());
        assertEquals(5, heros.getY());
        assertEquals("down", heros.getDirection());
    }
    
    @Test
    void testDeplacementGauche() {
        heros.deplacer(KeyEvent.VK_LEFT, 10, 10, grille);
        assertEquals(5, heros.getX());
        assertEquals(4, heros.getY());
        assertEquals("left", heros.getDirection());
    }
    
    @Test
    void testDeplacementDroite() {
        heros.deplacer(KeyEvent.VK_RIGHT, 10, 10, grille);
        assertEquals(5, heros.getX());
        assertEquals(6, heros.getY());
        assertEquals("right", heros.getDirection());
    }
    
    @Test
    void testDeplacementLimitesGrille() {
        // Positionner en bordure
        heros.setX(0);
        heros.setY(5);
        
        heros.deplacer(KeyEvent.VK_UP, 10, 10, grille);
        assertEquals(0, heros.getX()); // Ne peut pas sortir
        assertEquals(5, heros.getY());
    }
}
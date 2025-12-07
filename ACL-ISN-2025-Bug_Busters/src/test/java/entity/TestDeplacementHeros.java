package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.event.KeyEvent;
import static org.junit.jupiter.api.Assertions.*;

class TestDeplacementHeros {
    private Heros heros;
    private char[][] grille;
    
    // Les dimensions de la grille
    private final int GRID_SIZE = 10;
    
    @BeforeEach
    void setUp() {
        
        heros = new Heros(5, 5); 
        
        //  une grille simple 10x10 avec des murs autour de (5,5)
        grille = new char[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grille[i][j] = ' '; // Case vide par défaut
            }
        }
        
        // Ajouter des murs autour de la position (5, 5) pour les tests de collision
        // (4, 5) : Mur au-dessus
        grille[4][5] = '#'; 
        // (6, 5) : Mur en dessous
        grille[6][5] = '#'; 
        // (5, 4) : Mur à gauche
        grille[5][4] = '#'; 
        // (5, 6) : Mur à droite
        grille[5][6] = '#'; 
    }
    
 

    @Test
    void testDeplacementHaut() {
       
        heros.setX(2);
        heros.setY(5);
        
        heros.deplacer(KeyEvent.VK_UP, GRID_SIZE, GRID_SIZE, grille);
     
        assertEquals(1, heros.getX(), "Le héros doit monter d'une case (X: 2 -> 1).");
        assertEquals(5, heros.getY());
        assertEquals("up", heros.getDirection());
    }
    
    @Test
    void testDeplacementBas() {
        heros.deplacer(KeyEvent.VK_DOWN, GRID_SIZE, GRID_SIZE, grille);
        
        grille[6][5] = ' '; 
        
        heros.deplacer(KeyEvent.VK_DOWN, GRID_SIZE, GRID_SIZE, grille);
       
        assertEquals(6, heros.getX(), "Le héros doit descendre d'une case (X: 5 -> 6).");
        assertEquals(5, heros.getY());
        assertEquals("down", heros.getDirection());
    }
    
    @Test
    void testDeplacementGauche() {
        grille[5][4] = ' ';
        
        heros.deplacer(KeyEvent.VK_LEFT, GRID_SIZE, GRID_SIZE, grille);
      
        assertEquals(5, heros.getX());
        assertEquals(4, heros.getY(), "Le héros doit aller à gauche d'une case (Y: 5 -> 4).");
        assertEquals("left", heros.getDirection());
    }
    
    @Test
    void testDeplacementDroite() {
        grille[5][6] = ' '; 
        
        heros.deplacer(KeyEvent.VK_RIGHT, GRID_SIZE, GRID_SIZE, grille);
     
        assertEquals(5, heros.getX());
        assertEquals(6, heros.getY(), "Le héros doit aller à droite d'une case (Y: 5 -> 6).");
        assertEquals("right", heros.getDirection());
    }
    
    // --- Tests de Blocage -

    @Test
    void testDeplacementHautMur() {
       
        heros.deplacer(KeyEvent.VK_UP, GRID_SIZE, GRID_SIZE, grille);
        
       
        assertEquals(5, heros.getX(), "Le héros ne doit pas traverser le mur (X doit rester 5)."); 
        assertEquals(5, heros.getY());
    }
    
    @Test
    void testDeplacementLimitesGrille() {
        heros.setX(0); 
        heros.setY(5);
        heros.deplacer(KeyEvent.VK_UP, GRID_SIZE, GRID_SIZE, grille);
        assertEquals(0, heros.getX(), "Le héros ne doit pas sortir de la grille (X doit rester 0)."); 
        assertEquals(5, heros.getY());
    }
}
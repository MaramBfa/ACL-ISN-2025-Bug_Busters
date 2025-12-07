package entity;

import simple.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestZombie {
    
    @Test
    void testZombieCreation() {
        Position pos = new Position(5, 5);
        Zombie zombie = new Zombie(pos);
        
        assertEquals(pos, zombie.getPos());
    }
    
    @Test
    void testMoveTowardsHeroProche() {
        Zombie zombie = new Zombie(new Position(5, 5));
        Position hero = new Position(5, 3); // Distance = 2 (< 3)
        
        char[][] grille = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grille[i][j] = ' ';
            }
        }
        
        zombie.moveTowards(hero, grille);
        
        // Doit se déplacer vers le héros (de 5,5 à 5,4)
        assertEquals(5, zombie.getPos().x);
        assertEquals(4, zombie.getPos().y);
    }
    
    @Test
    void testMoveTowardsHeroLoin() {
        Zombie zombie = new Zombie(new Position(5, 5));
        Position hero = new Position(10, 10); // Distance > 3
        
        char[][] grille = new char[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                grille[i][j] = ' ';
            }
        }
        
        zombie.moveTowards(hero, grille);
        
        // Ne doit pas bouger (trop loin)
        assertEquals(5, zombie.getPos().x);
        assertEquals(5, zombie.getPos().y);
    }
    
    @Test
    void testMoveTowardsMur() {
        Zombie zombie = new Zombie(new Position(5, 5));
        Position hero = new Position(5, 4);
        
        char[][] grille = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grille[i][j] = ' ';
            }
        }
        grille[5][4] = '#'; // Mur devant
        
        zombie.moveTowards(hero, grille);
        
        // Ne peut pas traverser le mur
        assertEquals(5, zombie.getPos().x);
        assertEquals(5, zombie.getPos().y);
    }
    
    // ============ TESTS DE DÉTECTION DE BUGS ============
    
    @Test
    void testBugZombieSurCaseHero() {
        System.out.println("=== DÉTECTION BUG: Zombie sur case héros ===");
        
        Zombie zombie = new Zombie(new Position(5, 5));
        Position hero = new Position(4, 5); // Juste au-dessus
        
        char[][] grille = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grille[i][j] = ' ';
            }
        }
        
        // Le zombie devrait se déplacer vers le héros
        zombie.moveTowards(hero, grille);
        
        // BUG POTENTIEL: Le zombie ne devrait PAS se mettre sur la case du héros
        // Ce test vérifie qu'il s'arrête à la case adjacente
        assertNotEquals(hero.x, zombie.getPos().x, 
            "BUG: Zombie ne devrait pas se mettre sur la case du héros");
        assertNotEquals(hero.y, zombie.getPos().y,
            "BUG: Zombie ne devrait pas se mettre sur la case du héros");
    }
    
    @Test
    void testBugZombieDeplacementHorsLimites() {
        System.out.println("=== DÉTECTION BUG: Zombie hors limites ===");
        
        // Zombie en bordure
        Zombie zombie = new Zombie(new Position(0, 5));
        Position hero = new Position(2, 5); // En dessous
        
        char[][] grille = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grille[i][j] = ' ';
            }
        }
        
        zombie.moveTowards(hero, grille);
        
        // Le zombie ne devrait pas sortir de la grille
        assertTrue(zombie.getPos().x >= 0, 
            "BUG: Zombie ne devrait pas sortir de la grille (X négatif)");
        assertTrue(zombie.getPos().x < 10,
            "BUG: Zombie ne devrait pas sortir de la grille (X trop grand)");
    }
    
    @Test
    void testBugZombieDistanceCalculation() {
        System.out.println("=== DÉTECTION BUG: Calcul distance zombie ===");
        
        Zombie zombie = new Zombie(new Position(0, 0));
        Position hero1 = new Position(0, 2); // Distance 2
        Position hero2 = new Position(2, 2); // Distance 4
        
        char[][] grille = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grille[i][j] = ' ';
            }
        }
        
        // Avec héros à distance 2 (<3) -> devrait bouger
        zombie.moveTowards(hero1, grille);
        assertNotEquals(0, zombie.getPos().x + zombie.getPos().y,
            "BUG: Zombie devrait bouger vers héros proche (distance < 3)");
        
        // Reset zombie
        zombie = new Zombie(new Position(0, 0));
        
        // Avec héros à distance 4 (>=3) -> ne devrait pas bouger
        zombie.moveTowards(hero2, grille);
        assertEquals(0, zombie.getPos().x,
            "BUG: Zombie ne devrait pas bouger vers héros loin (distance >= 3)");
        assertEquals(0, zombie.getPos().y,
            "BUG: Zombie ne devrait pas bouger vers héros loin (distance >= 3)");
    }
}
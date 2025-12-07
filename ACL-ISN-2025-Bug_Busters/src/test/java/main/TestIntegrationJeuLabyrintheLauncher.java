package main;

import entity.Heros;
import simple.Level;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestIntegrationJeuLabyrintheLauncher {
    
    @Test
    void testGetLevelEnum() {
        Level level1 = JeuLabyrintheLauncher.getLevelEnum(1);
        Level level5 = JeuLabyrintheLauncher.getLevelEnum(5);
        Level level10 = JeuLabyrintheLauncher.getLevelEnum(10);
        Level levelInvalid = JeuLabyrintheLauncher.getLevelEnum(11);
        Level levelZero = JeuLabyrintheLauncher.getLevelEnum(0);
        
        assertEquals(Level.LEVEL_1, level1);
        assertEquals(Level.LEVEL_5, level5);
        assertEquals(Level.LEVEL_10, level10);
        assertNull(levelInvalid);
        assertNull(levelZero);
    }
    
    @Test
    void testDernierNiveauAtteint() {
        int dernierNiveau = JeuLabyrintheLauncher.getDernierNiveauAtteint();
        assertTrue(dernierNiveau >= 1 && dernierNiveau <= 10);
    }
    
    // ============ TESTS DE DÉTECTION DE BUGS ============
    
    @Test
    void testBugNiveauxInvalides() {
        System.out.println("=== DÉTECTION BUG: Niveaux invalides ===");
        
        // Test avec des valeurs extrêmes
        assertNull(JeuLabyrintheLauncher.getLevelEnum(-1),
            "BUG: Niveau négatif devrait retourner null");
        
        assertNull(JeuLabyrintheLauncher.getLevelEnum(0),
            "BUG: Niveau 0 devrait retourner null");
        
        assertNull(JeuLabyrintheLauncher.getLevelEnum(100),
            "BUG: Niveau hors limites devrait retourner null");
    }
    
    @Test
    void testBugProgressionNiveaux() {
        System.out.println("=== DÉTECTION BUG: Progression des niveaux ===");
        
        // Tous les niveaux devraient exister de 1 à 10
        for (int i = 1; i <= 10; i++) {
            Level level = JeuLabyrintheLauncher.getLevelEnum(i);
            assertNotNull(level, 
                "BUG: Le niveau " + i + " devrait exister");
            
            // Vérifier quelques propriétés de base
            assertTrue(level.width > 0,
                "BUG: Niveau " + i + " - largeur doit être > 0");
            assertTrue(level.height > 0,
                "BUG: Niveau " + i + " - hauteur doit être > 0");
            assertTrue(level.wallDensity >= 0 && level.wallDensity <= 1,
                "BUG: Niveau " + i + " - densité murs doit être entre 0 et 1");
        }
    }
}
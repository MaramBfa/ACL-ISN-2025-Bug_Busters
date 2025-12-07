package simple;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestLevelEnum {
    
    @Test
    void testLevelValues() {
        Level[] levels = Level.values();
        
        assertEquals(10, levels.length);
        assertEquals(Level.LEVEL_1, levels[0]);
        assertEquals(Level.LEVEL_10, levels[9]);
    }
    
    @Test
    void testLevelProperties() {
        Level level1 = Level.LEVEL_1;
        Level level10 = Level.LEVEL_10;
        
        assertEquals(15, level1.width);
        assertEquals(15, level1.height);
        assertEquals(0.20, level1.wallDensity, 0.01);
        assertEquals(2, level1.minMonsters);
        assertEquals(4, level1.maxMonsters);
        assertTrue(level1.hasWeapons);
        assertEquals(1, level1.nbCoeurs);
        
        assertEquals(15, level10.width);
        assertEquals(15, level10.height);
        assertEquals(0.42, level10.wallDensity, 0.01);
        assertEquals(12, level10.minMonsters);
        assertEquals(15, level10.maxMonsters);
        assertFalse(level10.hasWeapons);
        assertEquals(5, level10.nbCoeurs);
    }
}
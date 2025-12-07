package simple;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestPosition {
    
    @Test
    void testEquals() {
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(5, 5);
        Position pos3 = new Position(5, 6);
        
        assertEquals(pos1, pos2);
        assertNotEquals(pos1, pos3);
        assertEquals(pos1.hashCode(), pos2.hashCode());
    }
    
    @Test
    void testHashCodeConsistency() {
        Position pos = new Position(10, 15);
        int hash1 = pos.hashCode();
        int hash2 = pos.hashCode();
        assertEquals(hash1, hash2);
    }
}
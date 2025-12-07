package entity;

import simple.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestMonstre {
    
    @Test
    void testCreationMonstre() {
        Position pos = new Position(3, 3);
        Monstre monstre = new Monstre(pos);
        
        assertEquals(pos, monstre.getPos());
        assertEquals(3, monstre.getPos().x);
        assertEquals(3, monstre.getPos().y);
    }
    
    @Test
    void testSetPosition() {
        Monstre monstre = new Monstre(new Position(1, 1));
        Position nouvellePos = new Position(2, 2);
        
        monstre.setPos(nouvellePos);
        assertEquals(nouvellePos, monstre.getPos());
    }
}
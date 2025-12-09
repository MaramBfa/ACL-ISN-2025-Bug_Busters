package entity;

import simple.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestZombie {

    @Test
    void testZombieCreation() {
        // juste vérifier que le zombie garde bien la position donnée
        Position pos = new Position(5, 5);
        Zombie zombie = new Zombie(pos);

        assertEquals(pos, zombie.getPos());
    }

    @Test
    void testMoveTowardsHeroProche() {
        // quand le héros est à distance < 3, le zombie doit avancer vers lui
        Zombie zombie = new Zombie(new Position(5, 5));
        Position hero = new Position(5, 3); // distance = 2

        char[][] grille = new char[10][10];
        for (char[] row : grille) java.util.Arrays.fill(row, ' ');

        zombie.moveTowards(hero, grille);

        // il avance d’une case vers le héros
        assertEquals(5, zombie.getPos().x);
        assertEquals(4, zombie.getPos().y);
    }

    @Test
    void testMoveTowardsHeroLoin() {
        // si le héros est trop loin, le zombie ne bouge pas
        Zombie zombie = new Zombie(new Position(5, 5));
        Position hero = new Position(10, 10); // distance > 3

        char[][] grille = new char[15][15];
        for (char[] row : grille) java.util.Arrays.fill(row, ' ');

        zombie.moveTowards(hero, grille);

        // il reste immobile
        assertEquals(5, zombie.getPos().x);
        assertEquals(5, zombie.getPos().y);
    }

    @Test
    void testMoveTowardsMur() {
        // le zombie ne doit pas passer à travers un mur
        Zombie zombie = new Zombie(new Position(5, 5));
        Position hero = new Position(5, 4);

        char[][] grille = new char[10][10];
        for (char[] row : grille) java.util.Arrays.fill(row, ' ');

        grille[5][4] = '#'; // mur juste devant

        zombie.moveTowards(hero, grille);

        // il ne bouge pas car bloqué par un mur
        assertEquals(5, zombie.getPos().x);
        assertEquals(5, zombie.getPos().y);
    }

    @Test
    void testBugZombieDeplacementHorsLimites() {
        // le zombie ne doit pas sortir de la grille
        Zombie zombie = new Zombie(new Position(0, 5));
        Position hero = new Position(2, 5);

        char[][] grille = new char[10][10];
        for (char[] row : grille) java.util.Arrays.fill(row, ' ');

        zombie.moveTowards(hero, grille);

        assertTrue(zombie.getPos().x >= 0);
        assertTrue(zombie.getPos().x < 10);
    }

    @Test
    void testBugZombieDistanceCalculation() {
        // juste vérifier que la logique distance < 3 fonctionne bien
        Zombie zombie = new Zombie(new Position(0, 0));
        Position heroProche = new Position(0, 2); // distance 2 → doit bouger
        Position heroLoin = new Position(2, 2);   // distance 4 → ne bouge pas

        char[][] grille = new char[10][10];
        for (char[] row : grille) java.util.Arrays.fill(row, ' ');

        // cas héros proche
        zombie.moveTowards(heroProche, grille);
        boolean aBouge = !(zombie.getPos().x == 0 && zombie.getPos().y == 0);
        assertTrue(aBouge, "le zombie doit bouger si la distance < 3");

        // reset
        zombie = new Zombie(new Position(0, 0));

        // cas héros loin
        zombie.moveTowards(heroLoin, grille);
        assertEquals(0, zombie.getPos().x);
        assertEquals(0, zombie.getPos().y);
    }
}

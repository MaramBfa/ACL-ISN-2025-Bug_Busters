package main;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import entity.*;
import simple.Level;
import simple.Position;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Tests JUnit – Exemple pédagogique :
 * Ici nous démontrons comment un test peut détecter un vrai bug,
 * puis comment ce bug est corrigé.
 *
 * >>> BUG DÉTECTÉ :
 * Dans verifierCollisions(), le héros perdait 2 vies au lieu d’une.
 */
public class FenetreLabyrintheTest {

    @Test
    void testCollisionRetireUneSeuleVie() throws Exception {

        FenetreLabyrinthe f = createFakeFenetre();
        Heros hero = f.getHero();

        hero.setPointsDeVie(5);

        Field ghostField = f.getClass().getDeclaredField("fantome");
        ghostField.setAccessible(true);
        Ghost ghost = (Ghost) ghostField.get(f);

        Field posField = ghost.getClass().getDeclaredField("pos");
        posField.setAccessible(true);

        posField.set(ghost, new Position(hero.getX(), hero.getY()));

        Method m = f.getClass().getDeclaredMethod("verifierCollisions");
        m.setAccessible(true);
        m.invoke(f);

        assertEquals(4, hero.getPointsDeVie(),
                "Le héros doit perdre EXACTEMENT une seule vie lors d’une collision !");
    }


    @Test
    void testCreationFenetre_OK() {

        FenetreLabyrinthe f = createFakeFenetre();

        assertNotNull(f, "La fenêtre devrait être créée correctement.");
        assertNotNull(f.getHero());
    }


    @Test
    void testPaintComponent_OK() {
        FenetreLabyrinthe f = createFakeFenetre();

        assertDoesNotThrow(() -> {
            f.paintComponent(new java.awt.image.BufferedImage(50, 50, 1).getGraphics());
        });
    }

    private FenetreLabyrinthe createFakeFenetre() {

        char[][] grille = new char[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                grille[i][j] = ' ';

        Heros hero = new Heros(1, 1);

        return new FenetreLabyrinthe(
                grille,
                hero,
                new ArrayList<>(),                       // monstres
                new Cle(new Position(0, 0)),
                new Door(new Position(2, 2)),
                null,                                     // trésor
                new ArrayList<>(),                        // armes
                new Ghost(new Position(0, 2)),
                new Zombie(new Position(2, 0)),
                Level.LEVEL_1,
                new ArrayList<>(),                        // cœurs
                null,                                     // boss absent dans le test
                new ArrayList<>()                         // clones vides
        );

    }
}

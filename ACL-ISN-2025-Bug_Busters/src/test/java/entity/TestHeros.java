package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.event.KeyEvent;
import static org.junit.jupiter.api.Assertions.*;

class TestHeros {
    private Heros heros;
    private char[][] grille;

    @BeforeEach
    void setUp() {
        heros = new Heros(5, 5);
        
        // Initialiser une grille pour les tests de déplacement
        grille = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grille[i][j] = ' ';
            }
        }
    }

    @Test
    void testInitialisationHeros() {
        assertEquals(5, heros.getX());
        assertEquals(5, heros.getY());
        assertEquals(3, heros.getPointsDeVie());
        assertEquals(0, heros.getScore());
        assertFalse(heros.hasKey());
        assertFalse(heros.aEpee());
        assertFalse(heros.aArc());
    }

    @Test
    void testPerdreVie() {
        heros.perdreVie();
        assertEquals(2, heros.getPointsDeVie());
        
        heros.perdreVie(2);
        assertEquals(0, heros.getPointsDeVie());
        assertFalse(heros.estVivant());
    }

    @Test
    void testAjouterVie() {
        heros.ajouterVie();
        assertEquals(4, heros.getPointsDeVie());
        
        heros.ajouterVie(2);
        assertEquals(5, heros.getPointsDeVie()); // Max 5
    }

    @Test
    void testRamasserEpee() {
        heros.ramasserEpee();
        assertTrue(heros.aEpee());
        assertEquals(1, heros.getUsagesEpeeRestants());
        assertTrue(heros.peutUtiliserEpee());
    }

    @Test
    void testConsommerEpee() {
        heros.ramasserEpee();
        heros.consommerEpee();
        assertEquals(0, heros.getUsagesEpeeRestants());
        assertFalse(heros.peutUtiliserEpee());
        assertFalse(heros.aEpee());
    }

    @Test
    void testRamasserArc() {
        heros.ramasserArc();
        assertTrue(heros.aArc());
        assertEquals(1, heros.getUsagesArcRestants());
        assertTrue(heros.peutUtiliserArc());
    }

    @Test
    void testCle() {
        assertFalse(heros.hasKey());
        heros.pickKey();
        assertTrue(heros.hasKey());
        heros.useKey();
        assertFalse(heros.hasKey());
    }

    @Test
    void testScore() {
        heros.ajouterScore(50);
        assertEquals(50, heros.getScore());
        
        heros.enleverScore(20);
        assertEquals(30, heros.getScore());
        
        heros.enleverScore(40);
        assertEquals(0, heros.getScore()); // Ne peut pas être négatif
    }

    @Test
    void testEstAPosition() {
        assertTrue(heros.estAPosition(5, 5));
        assertFalse(heros.estAPosition(5, 4));
    }

    @Test
    void testPositionDevant() {
        heros.setDirection("up");
        int[] pos = heros.getPositionDevant();
        assertEquals(4, pos[0]); // x - 1
        assertEquals(5, pos[1]);
        
        heros.setDirection("right");
        pos = heros.getPositionDevant();
        assertEquals(5, pos[0]);
        assertEquals(6, pos[1]); // y + 1
    }

    @Test
    void testReinitialiserStats() {
        heros.ajouterScore(100);
        heros.ramasserEpee();
        heros.pickKey();
        heros.perdreVie();
        
        heros.reinitialiserStats();
        
        assertEquals(3, heros.getPointsDeVie());
        assertEquals(0, heros.getScore());
        assertFalse(heros.hasKey());
        assertFalse(heros.aEpee());
        assertFalse(heros.aArc());
    }

    // ============ TESTS DE DÉTECTION DE BUGS ============
    
    @Test
    void testBugPointsDeVieNegatifs() {
        System.out.println("=== DÉTECTION BUG: Points de vie négatifs ===");
        
        // Essayer de rendre les points de vie négatifs
        heros.perdreVie(10);
        assertEquals(0, heros.getPointsDeVie(), 
            "BUG: Les points de vie devraient rester à 0 minimum");
        assertFalse(heros.estVivant());
        
        // Tester avec setPointsDeVie négatif
        heros.setPointsDeVie(-5);
        assertEquals(0, heros.getPointsDeVie(),
            "BUG: setPointsDeVie ne devrait pas accepter de valeurs négatives");
    }

    @Test
    void testBugLimitePointsDeVie() {
        System.out.println("=== DÉTECTION BUG: Limite points de vie ===");
        
        // Essayer de dépasser la limite maximale
        heros.ajouterVie(10);
        assertEquals(5, heros.getPointsDeVie(), 
            "BUG: Les points de vie ne doivent pas dépasser 5");
        
        // Tester setPointsDeVie avec valeur > max
        heros.setPointsDeVie(10);
        assertEquals(5, heros.getPointsDeVie(),
            "BUG: setPointsDeVie doit respecter la limite maximale de 5");
    }

    @Test
    void testBugScoreNegatif() {
        System.out.println("=== DÉTECTION BUG: Score négatif ===");
        
        // Enlever plus de points qu'on n'en a
        heros.ajouterScore(10);
        heros.enleverScore(15);
        assertEquals(0, heros.getScore(), 
            "BUG: Le score ne doit jamais être négatif");
        
        // Enlever des points sans avoir de score
        heros.setScore(0);
        heros.enleverScore(10);
        assertEquals(0, heros.getScore(),
            "BUG: EnleverScore doit garder le score à 0 minimum");
    }

    @Test
    void testBugDeplacementHorsLimites() {
        System.out.println("=== DÉTECTION BUG: Déplacement hors limites ===");
        
        // Test bord supérieur
        heros.setX(0);
        heros.setY(5);
        heros.deplacer(KeyEvent.VK_UP, 10, 10, grille);
        assertEquals(0, heros.getX(), 
            "BUG: Ne peut pas sortir de la grille par le haut");
        
        // Test bord inférieur
        heros.setX(9);
        heros.setY(5);
        heros.deplacer(KeyEvent.VK_DOWN, 10, 10, grille);
        assertEquals(9, heros.getX(), 
            "BUG: Ne peut pas sortir de la grille par le bas");
        
        // Test bord gauche
        heros.setX(5);
        heros.setY(0);
        heros.deplacer(KeyEvent.VK_LEFT, 10, 10, grille);
        assertEquals(0, heros.getY(), 
            "BUG: Ne peut pas sortir de la grille par la gauche");
        
        // Test bord droit
        heros.setX(5);
        heros.setY(9);
        heros.deplacer(KeyEvent.VK_RIGHT, 10, 10, grille);
        assertEquals(9, heros.getY(), 
            "BUG: Ne peut pas sortir de la grille par la droite");
    }

    @Test
    void testBugDeplacementAvecMur() {
        System.out.println("=== DÉTECTION BUG: Déplacement avec mur ===");
        
        // Ajouter un mur devant le héros
        grille[4][5] = '#';
        
        heros.deplacer(KeyEvent.VK_UP, 10, 10, grille);
        assertEquals(5, heros.getX(), 
            "BUG: Ne peut pas traverser un mur");
        assertEquals(5, heros.getY());
    }

    @Test
    void testBugArmesMultiplesEtConsommation() {
        System.out.println("=== DÉTECTION BUG: Gestion des armes multiples ===");
        
        // Le héros devrait pouvoir avoir les deux armes
        heros.ramasserEpee();
        heros.ramasserArc();
        
        assertTrue(heros.aEpee() && heros.aArc(), 
            "BUG: Devrait pouvoir avoir les deux armes");
        
        // Consommer une arme ne devrait pas affecter l'autre
        heros.consommerEpee();
        assertFalse(heros.aEpee(), "Épée consommée");
        assertTrue(heros.aArc(), "BUG: Arc devrait toujours être disponible");
        
        // Consommer une arme qu'on n'a pas ne devrait pas planter
        heros.consommerEpee(); // Déjà consommée
        assertEquals(0, heros.getUsagesEpeeRestants(),
            "BUG: Consommer une épée inexistante ne devrait pas causer d'erreur");
    }

    @Test
    void testBugDeplacementZQSD() {
        System.out.println("=== DÉTECTION BUG: Déplacements ZQSD ===");
        
        // Vérifier que ZQSD fonctionne comme les flèches
        heros.setX(5);
        heros.setY(5);
        
        // Test Z (haut)
        heros.deplacer(KeyEvent.VK_Z, 10, 10, grille);
        assertEquals(4, heros.getX(), "Z devrait déplacer vers le haut");
        assertEquals("up", heros.getDirection());
        
        // Reset
        heros.setX(5);
        heros.setY(5);
        
        // Test Q (gauche)
        heros.deplacer(KeyEvent.VK_Q, 10, 10, grille);
        assertEquals(4, heros.getY(), "Q devrait déplacer vers la gauche");
        assertEquals("left", heros.getDirection());
        
        // Reset
        heros.setX(5);
        heros.setY(5);
        
        // Test S (bas)
        heros.deplacer(KeyEvent.VK_S, 10, 10, grille);
        assertEquals(6, heros.getX(), "S devrait déplacer vers le bas");
        assertEquals("down", heros.getDirection());
        
        // Reset
        heros.setX(5);
        heros.setY(5);
        
        // Test D (droite)
        heros.deplacer(KeyEvent.VK_D, 10, 10, grille);
        assertEquals(6, heros.getY(), "D devrait déplacer vers la droite");
        assertEquals("right", heros.getDirection());
    }
}
package main;

import entity.*;
import simple.Position;
import simple.Level;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class JeuLabyrintheLauncher {

    public static int niveauActuel = 1;
    public static Heros herosActuel = null;
    public static int dernierNiveauAtteint = 1;

    public static void lancerJeu() {
        niveauActuel = 1;
        herosActuel = null;
        dernierNiveauAtteint = 1;
        lancerNouveauNiveau(null);
    }

    public static void lancerNouveauNiveau(Heros herosStats) {
        Level currentLevelEnum = getLevelEnum(niveauActuel);

        if (currentLevelEnum == null) {
            JOptionPane.showMessageDialog(null, "FIN DU JEU ! Tous les niveaux sont terminés.", "Victoire Totale", JOptionPane.INFORMATION_MESSAGE);
            MenuPrincipal.lancerNouvellePartie();
            return;
        }

        Heros heros;
        if (herosStats != null) {
            heros = herosStats; 
            heros.resetPosition(currentLevelEnum.height / 2, currentLevelEnum.width / 2);
        } else {
            heros = new Heros(currentLevelEnum.height / 2, currentLevelEnum.width / 2);
        }

        Labyrinthe laby = new Labyrinthe(currentLevelEnum);
        char[][] grille = laby.getGrille();
        Position centre = new Position(heros.getX(), heros.getY());

        ArrayList<Position> occupees = new ArrayList<>();
        occupees.add(centre);
        Set<Position> accessibles = laby.trouverZonesAccessibles(centre);

        // Placement des éléments...
        Cle cle = new Cle(laby.placerAccessible(new HashSet<>(occupees), accessibles));
        occupees.add(cle.getPos());

        Door porte = new Door(laby.placerLoinDe(centre));
        occupees.add(porte.getPos());

        ArrayList<Weapon> armes = new ArrayList<>();
        if (currentLevelEnum.hasWeapons) {
            Position posEpee = laby.placerAccessible(new HashSet<>(occupees), accessibles);
            armes.add(new Weapon(posEpee, WeaponType.EPEE));
            occupees.add(posEpee);
            
            Position posArc = laby.placerAccessible(new HashSet<>(occupees), accessibles);
            armes.add(new Weapon(posArc, WeaponType.ARC));
            occupees.add(posArc);
        }

        ArrayList<Heart> coeurs = new ArrayList<>();
        for (int i = 0; i < currentLevelEnum.nbCoeurs; i++) {
            Position posCoeur = laby.placerAccessible(new HashSet<>(occupees), accessibles);
            coeurs.add(new Heart(posCoeur));
            occupees.add(posCoeur);
        }

        Tresor tresor = null;
        if (niveauActuel == 10) {
            Position tresorPos = laby.placerLoinDe(centre);
            tresor = new Tresor(tresorPos);
            occupees.add(tresorPos);
        }

        ArrayList<Position> monstres = new ArrayList<>();
        Random rand = new Random();
        
        int range = currentLevelEnum.maxMonsters - currentLevelEnum.minMonsters;
        int numMonstres = currentLevelEnum.minMonsters;
        if (range > 0) {
            numMonstres += rand.nextInt(range);
        }

        for (int i = 0; i < numMonstres; i++) {
            Position monstrePos = laby.placerAccessible(new HashSet<>(occupees), accessibles);
            monstres.add(monstrePos);
            occupees.add(monstrePos);
        }

        Ghost fantome = new Ghost(laby.placerAccessible(new HashSet<>(occupees), accessibles));
        occupees.add(fantome.getPos());

        Zombie zombie = new Zombie(laby.placerAccessible(new HashSet<>(occupees), accessibles));
        occupees.add(zombie.getPos());
        
        JFrame frame = new JFrame("Labyrinthe - Niveau " + niveauActuel + " (" + currentLevelEnum.nbCoeurs + " ❤️)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        FenetreLabyrinthe panel = new FenetreLabyrinthe(
            grille, 
            heros, 
            monstres, 
            cle, 
            porte,
            tresor,
            armes, 
            fantome, 
            zombie, 
            currentLevelEnum, 
            coeurs
        );

        heros.setFenetreActuelle(panel);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // CORRECTION : Mettre à jour le dernier niveau atteint dès le début du niveau
        if (niveauActuel > dernierNiveauAtteint) {
            dernierNiveauAtteint = niveauActuel;
            System.out.println("🎯 Mise à jour dernier niveau atteint: " + dernierNiveauAtteint);
        }

        String messageNiveau = "Niveau " + niveauActuel + " - " + currentLevelEnum.nbCoeurs + " ❤️ disponibles";
        if (niveauActuel == 10) {
            messageNiveau += "\n🏆 NIVEAU FINAL - Trouvez le trésor!";
        }
        panel.setMessageHUD("🎮 " + messageNiveau);
    }

    public static void niveauTermine(Heros hero, long finalTime) {
        JFrame oldFrame = (JFrame) SwingUtilities.getWindowAncestor(hero.getFenetreActuelle());
        if (oldFrame != null) {
            oldFrame.dispose();
        }
        
        if (hero.getPointsDeVie() <= 0) {
            // CORRECTION DÉFINITIVE : niveauActuel contient EXACTEMENT le niveau où on a perdu
            int niveauPerdu = niveauActuel;
            
            JOptionPane.showMessageDialog(null, 
                "GAME OVER au niveau " + niveauPerdu + " !\n"
                + "Score final : " + hero.getScore() + "\n"
                + "Vous recommencez au niveau " + niveauPerdu, 
                "Défaite", JOptionPane.ERROR_MESSAGE);
            
            // CORRECTION : Relancer EXACTEMENT le même niveau
            // niveauActuel reste inchangé, on le réutilise directement
            herosActuel = null; // Nouveau héros
            lancerNouveauNiveau(null);
            return;
        }

        // Si victoire, on met à jour le dernier niveau atteint AVANT d'incrémenter
        dernierNiveauAtteint = niveauActuel; // CORRECTION : Mettre à jour ici
        
        // Sauvegarder le niveau qu'on vient de terminer
        int niveauTermine = niveauActuel;
        
        // CORRECTION : Incrémenter seulement pour la victoire
        niveauActuel++;
        
        herosActuel = hero; 
        
        Level niveauPrecedent = getLevelEnum(niveauTermine);
        String messageCoeurs = "";
        if (niveauPrecedent != null && niveauPrecedent.nbCoeurs > 1) {
            messageCoeurs = "\n💖 Niveau avec " + niveauPrecedent.nbCoeurs + " cœurs de vie !";
        }
        
        String messageVictoire = "Félicitations ! Niveau " + niveauTermine + " terminé en " + finalTime + " secondes.\n"
            + "Score actuel : " + hero.getScore() + messageCoeurs;
        
        if (niveauTermine == 10) {
            messageVictoire += "\n\n🎉 VICTOIRE FINALE ! Vous avez conquis tous les labyrinthes !";
            niveauActuel = 1; // Réinitialiser pour une nouvelle partie
            dernierNiveauAtteint = 1;
        }
        
        JOptionPane.showMessageDialog(null, messageVictoire, "Victoire !", JOptionPane.INFORMATION_MESSAGE);

        // DEBUG
        System.out.println("🎯 Après victoire - Niveau actuel: " + niveauActuel + ", Dernier niveau: " + dernierNiveauAtteint);

        if (niveauActuel > Level.values().length) {
            MenuPrincipal.lancerNouvellePartie(); 
        } else {
            lancerNouveauNiveau(herosActuel);
        }
    }
    
    public static void recommencerAuNiveau(int niveau) {
        if (niveau >= 1 && niveau <= Level.values().length) {
            niveauActuel = niveau;
            herosActuel = null;
            System.out.println("🔄 Recommencement au niveau " + niveau);
            lancerNouveauNiveau(null);
        }
    }
    
    public static void continuerPartie() {
        if (dernierNiveauAtteint > 1) {
            int choix = JOptionPane.showConfirmDialog(null, 
                "Voulez-vous continuer au niveau " + dernierNiveauAtteint + "?\n"
                + "Ou recommencer depuis le niveau 1?",
                "Continuer la partie", 
                JOptionPane.YES_NO_OPTION);
            
            if (choix == JOptionPane.YES_OPTION) {
                niveauActuel = dernierNiveauAtteint;
                herosActuel = null;
                lancerNouveauNiveau(null);
            } else {
                lancerJeu();
            }
        } else {
            lancerJeu();
        }
    }

    private static Level getLevelEnum(int num) {
        if (num < 1 || num > Level.values().length) {
            return null;
        }
        return Level.values()[num - 1];
    }

    // Getter pour le dernier niveau atteint
    public static int getDernierNiveauAtteint() {
        return dernierNiveauAtteint;
    }

    // Méthode pour déboguer l'état actuel
    public static void afficherEtat() {
        System.out.println("🔍 État actuel - Niveau: " + niveauActuel + ", Dernier niveau atteint: " + dernierNiveauAtteint);
    }
}
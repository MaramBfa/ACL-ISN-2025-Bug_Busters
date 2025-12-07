package main;

import entity.Heros;
import entity.Cle;
import entity.Door;
import entity.Tresor;
import entity.Heart;
import entity.Weapon;
import entity.WeaponType;
import entity.Ghost;
import entity.Zombie;
import entity.Monstre;
import simple.Position;
import simple.Level;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class JeuLabyrintheLauncher {

    public static int niveauActuel = 1; //nuemro niveau en cours , initialement le premier
    public static Heros herosActuel = null; //héro qu'on garde entre les niveaux
    public static int dernierNiveauAtteint = 1; //pour pouvoir continuer une partie

    //on lance une nouvelle partie depuis le niveau 1
    public static void lancerJeu() {
        niveauActuel = 1;
        herosActuel = null;
        dernierNiveauAtteint = 1;
        lancerNouveauNiveau(null); //on n'a pas encore de héro donc null
    }

    //on lance un niveau 
    public static void lancerNouveauNiveau(Heros herosStats) {

        Level currentLevelEnum = getLevelEnum(niveauActuel); //on récupere les infos sur le niveau(taille , nombre de monstres, nombre ce coeurs,carte..)

        if (currentLevelEnum == null) {
        	//si on dépasse le nombre max de niveaux alors on annonce la fin du jeu
            JOptionPane.showMessageDialog(null,
                "FIN DU JEU ! Tous les niveaux sont terminés.",
                "Victoire Totale", JOptionPane.INFORMATION_MESSAGE);
            MenuPrincipal.lancerNouvellePartie();
            return;
        }

        //hero 
        Heros heros;
        if (herosStats != null) {
            heros = herosStats; //on garde les stats du héros
            heros.resetPosition(currentLevelEnum.height / 2, currentLevelEnum.width / 2);
        } else {
            heros = new Heros(currentLevelEnum.height / 2, currentLevelEnum.width / 2);
        }

        //génération du labyrinthe
        Labyrinthe laby = new Labyrinthe(currentLevelEnum);
        char[][] grille = laby.getGrille();
        Position centre = new Position(heros.getX(), heros.getY());
        
       //liste des cases déjà prises pour éviter les collisions au moment du placement
        ArrayList<Position> occupees = new ArrayList<>();
        occupees.add(centre);//on réserve la case du héros pour éviter de placer un objet dessus

        Set<Position> accessibles = laby.trouverZonesAccessibles(centre);

        //palcement clé du niveau
        Position posCle = laby.placerLoinDeAccessible(
                centre, 4,
                new HashSet<>(occupees),
                accessibles
        );
        Cle cle = new Cle(posCle);
        occupees.add(posCle);

     // ----- placement de la porte -----
     // règle : porte aux niveaux 1 → 9, aucune porte au niveau 10

     boolean niveauFinal = (niveauActuel == 10); //niveau final → pas de porte

     Door porte = null;
     Position posPorte = null;

     if (!niveauFinal) {   //on place une porte seulement si ce n'est pas le niveau 10

         final int MIN_DIST_CLE_PORTE = 6;  //distance minimale clé–porte
         final int MAX_ESSAIS = 40;         //sécurité anti-boucle infinie

         int essais = 0;

         do {
             posPorte = laby.placerLoinDeAccessible(
                     centre,
                     10,              //distance minimale depuis le héros
                     new HashSet<>(occupees),
                     accessibles
             );

             essais++;

             //si trop d’essais → on arrête la boucle, on accepte la position actuelle
             if (essais > MAX_ESSAIS) break;

         } while (
             posPorte != null &&
             //distance clé–porte doit être >= 6
             (Math.abs(posPorte.x - posCle.x) + Math.abs(posPorte.y - posCle.y)) < MIN_DIST_CLE_PORTE
         );

         if (posPorte != null) {
             porte = new Door(posPorte);
             occupees.add(posPorte); //on évite que quelque chose se place dessus
         }
     }


        //palcement des armes (épée + arc)
        ArrayList<Weapon> armes = new ArrayList<>();
        //on place des armes selon le niveau (si le niveau le possède) , possibilté alors de modifié quels armes apparaissent dans quel niveau , au début on avait que épée au niveau 1 par exemple mais après on l'a rajouter pour facilier le jeu
        //nous permet de changer les paramètres du jeu
        if (currentLevelEnum.hasWeapons) {

            Position posEpee = laby.placerLoinDeAccessible(
                    centre, 4,
                    new HashSet<>(occupees),
                    accessibles
            );
            armes.add(new Weapon(posEpee, WeaponType.EPEE));
            occupees.add(posEpee);

            Position posArc = laby.placerLoinDeAccessible(
                    centre, 4,
                    new HashSet<>(occupees),
                    accessibles
            );
            armes.add(new Weapon(posArc, WeaponType.ARC));
            occupees.add(posArc);
        }

        //placement des coeurs
        ArrayList<Heart> coeurs = new ArrayList<>();
        for (int i = 0; i < currentLevelEnum.nbCoeurs; i++) {

            Position posCoeur = laby.placerLoinDeAccessible(
                    centre, 3,
                    new HashSet<>(occupees),
                    accessibles
            );
            coeurs.add(new Heart(posCoeur));
            occupees.add(posCoeur);
        }

        //palcement du trésor(juste au niveau 10)
        Tresor tresor = null;
        if (niveauActuel == 10) {

            Position posTresor = laby.placerLoinDeAccessible(
                    centre, 12,
                    new HashSet<>(occupees),
                    accessibles
            );
            tresor = new Tresor(posTresor);
            occupees.add(posTresor);
        }

        //placement des monstres
        ArrayList<Monstre> monstres = new ArrayList<>();
        Random rand = new Random();

        //on choisit un nombre de monstres dans l’intervalle défini par Level 
        int range = currentLevelEnum.maxMonsters - currentLevelEnum.minMonsters;
        int numMonstres = currentLevelEnum.minMonsters;
        if (range > 0) numMonstres += rand.nextInt(range);

        for (int i = 0; i < numMonstres; i++) {

            Position posMonstre = laby.placerLoinDeAccessible(
                    centre, 5,
                    new HashSet<>(occupees),
                    accessibles
            );

            monstres.add(new Monstre(posMonstre));
            occupees.add(posMonstre);
        }

        //placement du fantôme qui passe à travers les murs
        Position posFantome = laby.placerLoinDeAccessible(
                centre, 8,
                new HashSet<>(occupees),
                accessibles
        );
        Ghost fantome = new Ghost(posFantome);
        occupees.add(posFantome);

        //placement du zombie
        Position posZombie = laby.placerLoinDeAccessible(
                centre, 7,
                new HashSet<>(occupees),
                accessibles
        );
        Zombie zombie = new Zombie(posZombie);
        occupees.add(posZombie);

        //creer la fenetre du niveau
        JFrame frame = new JFrame("Labyrinthe - Niveau " + niveauActuel +
                " (" + currentLevelEnum.nbCoeurs + " ❤️)");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        //on cree le panneau du jeu (qui gère tout le gameplay)
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

        heros.setFenetreActuelle(panel);  //permet au héro de savoir dans quelle fenêtre il est actuellement (utile pour fin de la partie)

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // mise à jour de la progression du jeu
        if (niveauActuel > dernierNiveauAtteint) {
            dernierNiveauAtteint = niveauActuel;
            System.out.println("🎯 Mise à jour dernier niveau atteint: " + dernierNiveauAtteint);
        }

        //messagae HUD
        String messageNiveau = "Niveau " + niveauActuel + " - " +
                currentLevelEnum.nbCoeurs + " ❤️ disponibles";

        if (niveauActuel == 10)
            messageNiveau += "\n🏆 NIVEAU FINAL - Trouvez le trésor!";

        panel.setMessageHUD("🎮 " + messageNiveau);
    }

    //fonction qu'on appelle quand le joueur termine un niveau
    public static void niveauTermine(Heros hero, long finalTime) {
    	
    	//on ferme la fenêtre du niveau terminé
        JFrame oldFrame = (JFrame) SwingUtilities.getWindowAncestor(hero.getFenetreActuelle());
        if (oldFrame != null) oldFrame.dispose();

        // Défaite alors pas d'image de victoire
        if (hero.getPointsDeVie() <= 0) {
            int niveauPerdu = niveauActuel;

            JOptionPane.showMessageDialog(null,
                "GAME OVER au niveau " + niveauPerdu + " !\n"
                + "Score final : " + hero.getScore() + "\n"
                + "Vous recommencez au niveau " + niveauPerdu,
                "Défaite", JOptionPane.ERROR_MESSAGE);

            herosActuel = null; // on va jetter les stats
            lancerNouveauNiveau(null);
            return;
        }

        //victoire alors on montre l'image de victoire
        int niveauTermine = niveauActuel;
        dernierNiveauAtteint = niveauActuel;
        niveauActuel++;//on passe au niveu suivant
        
        FenetreLabyrinthe fen = hero.getFenetreActuelle();
        int choix = fen.afficherImageVictoire(niveauTermine);
        
        //si le joeuur clique Quitter alors on va quitter tout le jeur
        if (choix == 1) System.exit(0);
        
        //on garde le meme hero avec ses stats pour les prochains niveaux
        herosActuel = hero;
        
        //au niveau final au reprend dès le début
        if (niveauTermine == 10) {
            JOptionPane.showMessageDialog(null,
                "🎉 VICTOIRE TOTALE ! Vous avez terminé tous les niveaux !");
            niveauActuel = 1;
            dernierNiveauAtteint = 1;
            MenuPrincipal.lancerNouvellePartie();
            return;
        }
        
        //sinon on lance le nouveau niveau avec le hero actuel
        lancerNouveauNiveau(herosActuel);
    }

    //permet de recommencer directement un niveau précis
    public static void recommencerAuNiveau(int niveau) {
        if (niveau >= 1 && niveau <= Level.values().length) {
            niveauActuel = niveau;
            herosActuel = null; //on recommence avec un hero reinitialisé (sans défaite)
            System.out.println("🔄 Recommencement au niveau " + niveau);
            lancerNouveauNiveau(null);
        }
    }
    
    //ici on reprend une ancienne progression
    public static void continuerPartie() {
        if (dernierNiveauAtteint > 1) {
            int choix = JOptionPane.showConfirmDialog(null,
                "Voulez-vous continuer au niveau " + dernierNiveauAtteint + "?\n"
                + "Ou recommencer depuis le niveau 1?",
                "Continuer la partie",
                JOptionPane.YES_NO_OPTION);
            
            //no : veut dire on recommence au niveau 1
            if (choix == 1) lancerJeu();
            else {
                niveauActuel = dernierNiveauAtteint;
                herosActuel = null;
                lancerNouveauNiveau(null);
            }
        } else {
            lancerJeu(); //si aucun niveau encore atteint
        }
    }
    
    
    private static Level getLevelEnum(int num) {
        if (num < 1 || num > Level.values().length) return null;
        return Level.values()[num - 1];
    }
    //renvoie simplement le dernier niveau atteint par le joueur dans la partie
    //ça sert surtout pour la fonction "continuer partie"
    public static int getDernierNiveauAtteint() {
        return dernierNiveauAtteint;
    }

    //affiche un petit résumé dans la console pour le debug
    //utile pour vérifier que les valeurs des niveaux sont correctes
    public static void afficherEtat() {
        System.out.println("🔍 État actuel - Niveau: " + niveauActuel
                + ", Dernier niveau atteint: " + dernierNiveauAtteint);
    }
}

package main;

import entity.*;
import simple.Position;
import simple.Difficulty; // ✅ Import ajouté

import javax.swing.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Difficulty diff = choisirDifficulte();

        Labyrinthe lab = new Labyrinthe(diff.width, diff.height);
        char[][] grille = lab.getGrille();

        Set<Position> occupees = new HashSet<>();

        Position posHero = new Position(diff.height / 2, diff.width / 2);
        Heros hero = new Heros(posHero.x, posHero.y);

        Set<Position> accessibles = lab.trouverZonesAccessibles(posHero);

        Position posCle = lab.placerAccessible(occupees, accessibles);
        Cle cle = new Cle(posCle);

        Position posTresor = lab.placerAccessible(occupees, accessibles);
        Tresor tresor = new Tresor(posTresor);

        ArrayList<Weapon> armes = new ArrayList<>();
        for (WeaponType type : WeaponType.values())
            armes.add(new Weapon(lab.placerAccessible(occupees, accessibles), type));

        ArrayList<Position> monstres = new ArrayList<>();
        for (int i = 0; i < diff.nbMonstres; i++)
            monstres.add(lab.placerAccessible(occupees, accessibles));

        Ghost fantome = new Ghost(lab.placerAccessible(occupees, accessibles));
        Zombie zombie = new Zombie(lab.placerAccessible(occupees, accessibles));

        JFrame frame = new JFrame("🧩 Labyrinthe - Niveau " + diff.name());
        FenetreLabyrinthe panel = new FenetreLabyrinthe(
                grille, hero, monstres, cle, tresor, armes, fantome, zombie, diff
        );

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static Difficulty choisirDifficulte() {
        Object[] options = {"Facile", "Moyen", "Difficile"};
        int choix = JOptionPane.showOptionDialog(
                null,
                "Choisissez la difficulté du labyrinthe :",
                "Sélection du niveau",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        return switch (choix) {
            case 0 -> Difficulty.EASY;
            case 1 -> Difficulty.MEDIUM;
            case 2 -> Difficulty.HARD;
            default -> Difficulty.EASY;
        };
    }
}

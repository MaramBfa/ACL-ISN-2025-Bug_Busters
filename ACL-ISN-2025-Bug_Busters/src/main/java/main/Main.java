package main;

import entity.Heros;
import entity.Tresor;
import simple.Position;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Taille du labyrinthe
        int largeur = 20;
        int hauteur = 15;

        // Création du labyrinthe aléatoire
        Labyrinthe lab = new Labyrinthe(largeur, hauteur);
        char[][] grille = lab.getGrille();

        // Placement aléatoire du héros
        Position posHero = lab.placerAleatoirement();
        Heros hero = new Heros(posHero.x, posHero.y);

        // Placement aléatoire du monstre (différent du héros)
        Position posMonstre;
        do {
            posMonstre = lab.placerAleatoirement();
        } while (posMonstre.equals(posHero));

        // Placement aléatoire du trésor (différent du héros et du monstre)
        Position posTresor;
        do {
            posTresor = lab.placerAleatoirement();
        } while (posTresor.equals(posHero) || posTresor.equals(posMonstre));
        Tresor tresor = new Tresor(posTresor);

        // Création de la fenêtre
        JFrame frame = new JFrame("Labyrinthe Aléatoire");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Ajout du panel
        FenetreLabyrinthe panel = new FenetreLabyrinthe(grille, hero, posMonstre, tresor);
        frame.add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

package main;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Menu du Labyrinthe 🧩");
        setSize(450, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        add(panel);

        JButton jouerButton = new JButton("🎮 Nouvelle Partie (Niveau 1)");
        JButton continuerButton = new JButton("➡️ Continuer la Partie");
        JButton instructionsButton = new JButton("📖 Instructions");
        JButton quitterButton = new JButton("❌ Quitter");

        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        jouerButton.setFont(buttonFont);
        continuerButton.setFont(buttonFont);
        instructionsButton.setFont(buttonFont);
        quitterButton.setFont(buttonFont);

        continuerButton.setEnabled(JeuLabyrintheLauncher.getDernierNiveauAtteint() > 1);

        jouerButton.addActionListener(e -> {
            dispose();
            JeuLabyrintheLauncher.lancerJeu();
        });

        continuerButton.addActionListener(e -> {
            dispose();
            JeuLabyrintheLauncher.continuerPartie();
        });

        instructionsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "🎯 OBJECTIF :\n"
                + "• Trouver la CLÉ 🗝️ puis la PORTE 🚪 pour passer au niveau suivant\n"
                + "• Niveau 10 : récupérer le TRÉSOR 🏆 pour gagner la partie\n\n"
                + "💖 SANTÉ :\n"
                + "• Départ avec 3 vies ❤️❤️❤️ (jusqu'à 5 maximum)\n"
                + "• Certains niveaux contiennent des cœurs supplémentaires\n\n"
                + "⚔️ ARMES :\n"
                + "• Tu peux avoir l'ÉPÉE et l'ARC en même temps\n"
                + "• Chaque arme = 1 seule utilisation\n"
                + "  - Épée : attaque autour du héros (corps-à-corps)\n"
                + "  - Arc : tir en ligne droite jusqu'à un mur ou une cible\n\n"
                + "🎮 CONTRÔLES :\n"
                + "• Flèches ⇧ ⇩ ⇦ ⇨ : déplacement\n"
                + "• Espace : attaquer avec l'ÉPÉE\n"
                + "• T puis Flèche : tirer avec l’ARC\n\n"
                + "👾 ENNEMIS :\n"
                + "• Monstres : tuables (épée ou arc)\n"
                + "• Zombie : poursuit le héros, tuable\n"
                + "• Fantôme : traverse les murs et est INVINCIBLE 👻\n"
                + "• Niveau 10 : BOSS + clones qui apparaissent avec le temps ⚠️",
                "Instructions du Jeu", JOptionPane.INFORMATION_MESSAGE);
        });


        quitterButton.addActionListener(e -> System.exit(0));

        panel.add(jouerButton);
        panel.add(continuerButton);
        panel.add(instructionsButton);
        panel.add(quitterButton);
    }

    public static void lancerNouvellePartie() {
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}

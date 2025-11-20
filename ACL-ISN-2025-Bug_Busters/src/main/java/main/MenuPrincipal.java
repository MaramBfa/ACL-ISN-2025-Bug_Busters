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
                + "• Trouve la CLÉ 🗝️ puis la PORTE 🚪 pour passer au niveau suivant\n"
                + "• Niveau 10 : Trouve le TRÉSOR 🏆\n\n"
                + "💖 SANTÉ :\n"
                + "• Départ avec 3 vies ❤️❤️❤️ (max 5 vies)\n"
                + "• Des cœurs supplémentaires apparaissent dans certains niveaux\n\n"
                + "⚔️ ARMES :\n"
                + "• Tu peux posséder l'ÉPÉE ET l'ARC en même temps\n"
                + "• Chaque arme peut être utilisée UNE SEULE FOIS\n"
                + "  - Épée : attaque au corps-à-corps dans une zone d'une case autour de toi\n"
                + "  - Arc : attaque à distance en ligne droite jusqu'au mur\n\n"
                + "🎮 CONTRÔLES :\n"
                + "• Z / Q / S / D ou les flèches : se déplacer\n"
                + "• Barre ESPACE : attaquer avec l'ÉPÉE\n"
                + "• T puis Z / Q / S / D : tirer une flèche avec l'ARC\n\n"
                + "👻 ENNEMIS :\n"
                + "• Monstres : peuvent être tués par l'épée ou l'arc\n"
                + "• Zombie : te poursuit s'il est proche, tuable aussi\n"
                + "• Fantôme : traverse les murs et est INVINCIBLE 😈",
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

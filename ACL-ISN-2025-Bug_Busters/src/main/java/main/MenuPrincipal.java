package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPrincipal extends JFrame {
    
    public MenuPrincipal() {
        setTitle("Menu du Labyrinthe 🧩");
        setSize(450, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 15, 15)); // 4 boutons maintenant
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

        // Désactiver le bouton continuer si pas de partie en cours
        continuerButton.setEnabled(JeuLabyrintheLauncher.getDernierNiveauAtteint() > 1);

        // Action Nouvelle Partie
        jouerButton.addActionListener(e -> {
            dispose(); 
            JeuLabyrintheLauncher.lancerJeu(); 
        });

        // Action Continuer
        continuerButton.addActionListener(e -> {
            dispose();
            JeuLabyrintheLauncher.continuerPartie();
        });

        // Action Instructions
        instructionsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "🎯 OBJECTIF :\n"
                + "• Trouve la CLÉ 🗝️ puis la PORTE 🚪 pour passer au niveau suivant\n"
                + "• Niveau 10 : Trouve le TRÉSOR 🏆\n\n"
                + "💖 SANTÉ :\n"
                + "• Départ avec 3 vies ❤️❤️❤️\n"
                + "• Maximum 5 vies possible\n"
                + "• Plus de cœurs dans les niveaux avancés !\n\n"
                + "⚔️ ARMES :\n"
                + "• Épée : Attaque proche\n"
                + "• Arc : Attaque à distance\n\n"
                + "🎮 CONTRÔLES :\n"
                + "• Flèches : Se déplacer\n"
                + "• Entrée : Attaquer\n\n"
                + "💡 ASTUCE : Si vous perdez, vous recommencez au même niveau !",
                "Instructions du Jeu", JOptionPane.INFORMATION_MESSAGE);
        });

        // Action Quitter
        quitterButton.addActionListener(e -> System.exit(0));

        panel.add(jouerButton);
        panel.add(continuerButton);
        panel.add(instructionsButton);
        panel.add(quitterButton);
    }
    
    /**
     * Méthode statique appelée pour afficher le menu principal (utilisé après la fin d'une partie).
     */
    public static void lancerNouvellePartie() {
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
        });
    }

    /**
     * Point d'entrée principal.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}
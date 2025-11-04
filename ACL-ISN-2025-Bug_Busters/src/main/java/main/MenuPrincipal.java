package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPrincipal extends JFrame {
    public MenuPrincipal() {
        setTitle("Menu du Labyrinthe");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JButton jouerButton = new JButton("Jouer");
        JButton instructionsButton = new JButton("Instructions");
        JButton quitterButton = new JButton("Quitter");

        jouerButton.addActionListener(e -> {
            dispose(); // ferme le menu
            new FenetreLabyrinthe(); // lance le jeu
        });

        instructionsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Déplacez-vous avec les flèches du clavier.\nTrouvez la clé et le trésor en évitant les monstres.",
                "Instructions", JOptionPane.INFORMATION_MESSAGE);
        });

        quitterButton.addActionListener(e -> System.exit(0));

        add(jouerButton);
        add(instructionsButton);
        add(quitterButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}

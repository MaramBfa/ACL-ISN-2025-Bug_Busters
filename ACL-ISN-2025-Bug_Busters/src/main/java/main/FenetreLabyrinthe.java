private ArrayList<Monstre> monstres;
public FenetreLabyrinthe(char[][] grille, Heros hero,
                         ArrayList<Monstre> monstres,
                         Cle cle, Tresor tresor,
                         ArrayList<Weapon> armes,
                         Ghost fantome, Zombie zombie,
                         Difficulty difficulty) {
    this.grille = grille;
    this.hero = hero;
    this.monstres = monstres;
    this.cle = cle;
    this.tresor = tresor;
    this.armes = armes;
    this.fantome = fantome;
    this.zombie = zombie;
    // ... le reste de ton constructeur
}
private void deplacerMonstres() {
    for (Monstre m : monstres) {
        m.move(grille[0].length, grille.length);
    }
}
private void verifierCollisions() {
    if (partieTerminee) return;

    for (Monstre m : monstres) {
        if (!m.estVivant()) continue;
        Point pos = m.getPosition();
        if (hero.getX() == pos.x && hero.getY() == pos.y) {
            if (hero.aUneArme()) {
                m.tuer();
                hero.ajouterScore(50);
            } else {
                hero.perdreVie();
                if (hero.getPointsDeVie() <= 0) {
                    finDePartie("💀 Game Over !\nVoulez-vous rejouer ?", "Défaite");
                    return;
                }
            }
        }
    }

    // Fantôme
    if (hero.getX() == fantome.getPos().x && hero.getY() == fantome.getPos().y) {
        hero.perdreVie();
        if (hero.getPointsDeVie() <= 0) {
            finDePartie("👻 Le fantôme vous a eu !\nVoulez-vous rejouer ?", "Défaite");
            return;
        }
    }

    // Zombie
    if (hero.getX() == zombie.getPos().x && hero.getY() == zombie.getPos().y) {
        hero.perdreVie();
        if (hero.getPointsDeVie() <= 0) {
            finDePartie("🧟 Le zombie vous a attrapé !\nVoulez-vous rejouer ?", "Défaite");
            return;
        }
    }

    // Clé
    if (!cle.estRamassee() && hero.getX() == cle.getPos().x && hero.getY() == cle.getPos().y) {
        cle.ramasser();
        hero.pickKey();
        hero.ajouterScore(10);
    }

    // Armes
    for (Weapon w : armes) {
        if (!w.estRamassee() && hero.getX() == w.getPos().x && hero.getY() == w.getPos().y) {
            w.ramasser();
            hero.setWeapon(w.getType());
            hero.ajouterScore(20);
        }
    }

    // Trésor
    if (hero.getX() == tresor.getPos().x && hero.getY() == tresor.getPos().y) {
        if (!hero.hasKey()) {
            if (!messageTresorAffiche) {
                JOptionPane.showMessageDialog(this, "🔒 Le trésor est verrouillé ! Trouvez d'abord la clé.");
                messageTresorAffiche = true;
            }
            return;
        }

        chronoTimer.stop();
        timerMonstres.stop();
        partieTerminee = true;

        long finalTime = (System.currentTimeMillis() - startTime) / 1000;
        int choix = JOptionPane.showConfirmDialog(this,
            "🎉 Vous avez gagné en " + finalTime + " secondes !\nScore final : " + hero.getScore() + "\n\nVoulez-vous rejouer ?",
            "Victoire", JOptionPane.YES_NO_OPTION);

        if (choix == JOptionPane.YES_OPTION) {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            MenuPrincipal.lancerNouvellePartie();
        } else {
            System.exit(0);
        }
    } else {
        messageTresorAffiche = false;
    }
}
for (Monstre m : monstres) {
    if (m.estVivant()) {
        Point pos = m.getPosition();
        g.drawImage(monsterImg, pos.y * TAILLE_CASE, pos.x * TAILLE_CASE, this);
    }
}

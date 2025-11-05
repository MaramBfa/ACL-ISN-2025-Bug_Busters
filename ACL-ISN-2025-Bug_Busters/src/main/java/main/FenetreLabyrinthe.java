// Remplace la déclaration de monstres :
private ArrayList<Monstre> monstres;

// Dans le constructeur :
public FenetreLabyrinthe(char[][] grille, Heros hero,
                         ArrayList<Monstre> monstres,
                         Cle cle, Tresor tresor,
                         ArrayList<Weapon> armes,
                         Ghost fantome, Zombie zombie,
                         Difficulty difficulty) {
    this.monstres = monstres;
    ...
}

// Méthode de déplacement des monstres :
private void deplacerMonstres() {
    for (Monstre m : monstres) {
        m.move(grille[0].length, grille.length);
    }
}

// Méthode de vérification des collisions :
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

    if (hero.getX() == fantome.getPos().x && hero.getY() == fantome.getPos().y) {
        hero.perdreVie();
        if (hero.getPointsDeVie() <= 0) {
            finDePartie("👻 Le fantôme vous a eu !\nVoulez-vous rejouer ?", "Défaite");
            return;
        }
    }

    if (hero.getX() == zombie.getPos().x && hero.getY() == zombie.getPos().y) {
        hero.perdreVie();
        if (hero.getPointsDeVie() <= 0) {
            finDePartie("🧟 Le zombie vous a attrapé !\nVoulez-vous rejouer ?", "Défaite");
            return;
        }
    }

    if (!cle.estRamassee() && hero.getX() == cle.getPos().x && hero.getY() == cle.getPos().y) {
        cle.ramasser();
        hero.pickKey();
        hero.ajouterScore(10);
    }

    for (Weapon w : armes) {
        if (!w.estRamassee() && hero.getX() == w.getPos().x && hero.getY() == w.getPos().y) {
            w.ramasser();
            hero.setWeapon(w.getType());
            hero.ajouterScore(20);
        }
    }

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

// Méthode paintComponent :
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    for (int x = 0; x < grille.length; x++) {
        for (int y = 0; y < grille[0].length; y++) {
            g.setColor(grille[x][y] == '#' ? wallColor : floorColor);
            g.fillRect(y * TAILLE_CASE, x * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
        }
    }

    if (!cle.estRamassee())
        g.drawImage(keyImg, cle.getPos().y * TAILLE_CASE, cle.getPos().x * TAILLE_CASE, this);

    for (Weapon w : armes)
        if (!w.estRamassee())
            g.drawImage((w.getType() == WeaponType.EPEE ? swordImg : bowImg),
                    w.getPos().y * TAILLE_CASE, w.getPos().x * TAILLE_CASE, this);

    g.drawImage(treasureImg, tresor.getPos().y * TAILLE_CASE, tresor.getPos().x * TAILLE_CASE, this);

    for (Monstre m : monstres)
        if (m.estVivant()) {
            Point pos = m.getPosition();
            g.drawImage(monsterImg, pos.y * TAILLE_CASE, pos.x * TAILLE_CASE, this);
        }

    g.drawImage(ghostImg, fantome.getPos().y * TAILLE_CASE, fantome.getPos().x * TAILLE_CASE, this);
    g.drawImage(zombieImg, zombie.getPos().y * TAILLE_CASE, zombie.getPos().x * TAILLE_CASE, this);
    g.drawImage(heroImg, hero.getY() * TAILLE_CASE, hero.getX() * TAILLE_CASE, this);

    int hudY = grille.length * TAILLE_CASE + 25;
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial", Font.BOLD, 16));
    g.drawString("Vies :", 10, hudY);

    for (int i = 0; i < hero.getPointsDeVie(); i++) {
        g.setColor(Color.GREEN);
        g.fillRect(70 + i * 30, hudY - 15, 20, 20);
        g.setColor(Color.BLACK);
        g.drawRect(70 + i * 30, hudY - 15, 20, 20);
    }

    g.drawString("Score : " + hero.getScore(), 10, hudY + 30);
    if (hero.aUneArme()) g.drawString("Arme : " + hero.getWeapon(), 180, hudY + 30);
    if (hero.hasKey()) g.drawString("Clé : ✅", 320, hudY + 30);
}

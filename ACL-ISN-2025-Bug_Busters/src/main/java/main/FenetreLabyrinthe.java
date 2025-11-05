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
}

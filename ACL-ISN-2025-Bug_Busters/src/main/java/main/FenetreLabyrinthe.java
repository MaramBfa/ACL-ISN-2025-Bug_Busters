package main;

import entity.Heros;
import entity.Cle;
import entity.Door;
import entity.Tresor;
import entity.Heart;
import entity.Ghost;
import entity.Zombie;
import entity.Weapon;
import entity.WeaponType;
import simple.Position;
import simple.Level;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;

public class FenetreLabyrinthe extends JPanel {

    private static final long serialVersionUID = 1L;

    private char[][] grille;
    private Heros hero;
    private ArrayList<Position> monstres;
    private ArrayList<Weapon> armes;
    private Cle cle;
    private Door door;
    private Tresor tresor;
    private ArrayList<Heart> coeurs;
    private Ghost fantome;
    private Zombie zombie;
    private boolean zombieVivant = true;

    private final int TAILLE_CASE = 40;
    private Level currentLevel;
    private boolean partieTerminee = false;

    private javax.swing.Timer timerMonstres;
    private javax.swing.Timer chronoTimer;

    private Color wallColor;
    private Color floorColor;

    private Image heroImg, monsterImg, ghostImg, zombieImg,
            keyImg, treasureImg, swordImg, bowImg, heartImg, doorImg;

    private long startTime;
    private JLabel chronoLabel;

    private long dernierDegatTime = 0;
    private static final long DELAI_ENTRE_DEGATS = 1000;
    private StringBuilder messageHUD = new StringBuilder();
    private long messageHUDTime = 0;
    private static final long DUREE_MESSAGE_HUD = 3000;

    // Tir à l’arc (T puis Z/Q/S/D)
    private boolean bowAiming = false;

    public FenetreLabyrinthe(char[][] grille, Heros hero,
                             ArrayList<Position> monstres,
                             Cle cle, Door door, Tresor tresor,
                             ArrayList<Weapon> armes,
                             Ghost fantome, Zombie zombie,
                             Level level, ArrayList<Heart> coeurs) {

        this.grille = grille;
        this.hero = hero;
        this.monstres = monstres;
        this.cle = cle;
        this.door = door;
        this.tresor = tresor;
        this.armes = armes;
        this.fantome = fantome;
        this.zombie = zombie;
        this.currentLevel = level;
        this.coeurs = coeurs;

        // Couleurs selon densité des murs
        if (level.wallDensity < 0.25) {
            wallColor = new Color(180, 220, 180);
            floorColor = new Color(240, 250, 220);
        } else if (level.wallDensity < 0.35) {
            wallColor = new Color(150, 150, 150);
            floorColor = new Color(200, 200, 200);
        } else {
            wallColor = new Color(60, 60, 60);
            floorColor = new Color(100, 100, 100);
        }

        // Chargement des images
        heroImg = loadImage("/images/hero.png");
        monsterImg = loadImage("/images/monster.png");
        ghostImg = loadImage("/images/ghost.png");
        zombieImg = loadImage("/images/zombie.png");
        keyImg = loadImage("/images/key.png");
        treasureImg = loadImage("/images/treasure.png");
        swordImg = loadImage("/images/sword.png");
        bowImg = loadImage("/images/bow.png");
        heartImg = loadImage("/images/Coeur.png");
        doorImg = loadImage("/images/door.png");

        setPreferredSize(new Dimension(
                grille[0].length * TAILLE_CASE,
                grille.length * TAILLE_CASE + 150));
        setFocusable(true);

        // Chronomètre
        startTime = System.currentTimeMillis();
        chronoLabel = new JLabel("Temps : 0 sec - Cœurs restants: "
                + getCoeursRestants() + "/" + coeurs.size());
        chronoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        chronoLabel.setForeground(Color.BLUE);
        setLayout(new BorderLayout());
        add(chronoLabel, BorderLayout.NORTH);

        chronoTimer = new Timer(1000, e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            chronoLabel.setText("Temps : " + elapsed
                    + " sec - Cœurs restants: "
                    + getCoeursRestants() + "/" + coeurs.size());
        });
        chronoTimer.start();

        // Gestion clavier : flèches/ZQSD pour bouger, T puis ZQSD pour arc, ESPACE pour épée
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (partieTerminee) return;

                int key = e.getKeyCode();

                // 1) Attaque ÉPÉE (ESPACE)
                if (key == KeyEvent.VK_SPACE) {
                    if (hero.peutUtiliserEpee()) {
                        attaquerEpee();
                        hero.consommerEpee();
                    } else if (hero.aEpee()) {
                        setMessageHUD("🗡️ Tu as déjà utilisé ton épée !");
                    } else {
                        setMessageHUD("❌ Pas d'épée (Espace)");
                    }
                    repaint();
                    return;
                }

                // 2) Préparation Arc (T)
                if (key == KeyEvent.VK_T) {
                    if (hero.peutUtiliserArc()) {
                        bowAiming = true;
                        setMessageHUD("🏹 Direction (Z/Q/S/D)");
                    } else if (hero.aArc()) {
                        setMessageHUD("🏹 Arc déjà utilisé !");
                    } else {
                        setMessageHUD("❌ Pas d'arc (T)");
                    }
                    repaint();
                    return;
                }

                // 3) Direction du tir de l’arc (Z/Q/S/D)
                if (bowAiming &&
                        (key == KeyEvent.VK_Z ||
                         key == KeyEvent.VK_Q ||
                         key == KeyEvent.VK_S ||
                         key == KeyEvent.VK_D)) {

                    int dx = 0, dy = 0;

                    if (key == KeyEvent.VK_Z) dx = -1;  // Haut
                    if (key == KeyEvent.VK_S) dx =  1;  // Bas
                    if (key == KeyEvent.VK_Q) dy = -1;  // Gauche
                    if (key == KeyEvent.VK_D) dy =  1;  // Droite

                    attaquerArc(dx, dy);
                    hero.consommerArc();
                    bowAiming = false;

                    repaint();
                    return;
                }

                // 4) DÉPLACEMENT : UNIQUEMENT AVEC LES FLÈCHES
                if (!bowAiming &&
                        (key == KeyEvent.VK_UP ||
                         key == KeyEvent.VK_DOWN ||
                         key == KeyEvent.VK_LEFT ||
                         key == KeyEvent.VK_RIGHT)) {

                    hero.deplacer(key, grille[0].length, grille.length, grille);
                    verifierCollisions();
                    verifierSiBloque();
                    repaint();
                }
            }
        });


        // Timer de déplacement des monstres + fantôme + zombie
        timerMonstres = new Timer(500, e -> {
            if (!partieTerminee) {
                deplacerMonstres();
                fantome.move(grille[0].length, grille.length, grille);
                if (zombieVivant) {
                    zombie.moveTowards(new Position(hero.getX(), hero.getY()), grille);
                }
                verifierCollisions();
                repaint();
            }
        });
        timerMonstres.start();
    }

    // Chargement d’une image avec placeholder violet si manquante
    private Image loadImage(String path) {
        java.net.URL location = getClass().getResource(path);
        if (location == null) {
            BufferedImage img = new BufferedImage(
                    TAILLE_CASE, TAILLE_CASE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, TAILLE_CASE, TAILLE_CASE);
            g.dispose();
            System.err.println("⚠️ Image introuvable : " + path);
            return img;
        }
        Image img = new ImageIcon(location).getImage();
        return img.getScaledInstance(
                TAILLE_CASE, TAILLE_CASE, Image.SCALE_SMOOTH);
    }

    private int getCoeursRestants() {
        int count = 0;
        if (coeurs == null) return 0;
        for (Heart coeur : coeurs) {
            if (!coeur.estRamassee()) {
                count++;
            }
        }
        return count;
    }

    private void deplacerMonstres() {
        Random rand = new Random();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        int gridHeight = grille.length;
        int gridWidth = grille[0].length;

        for (int i = 0; i < monstres.size(); i++) {
            Position m = monstres.get(i);
            int newX = m.x;
            int newY = m.y;

            int direction = rand.nextInt(4);
            newX += dx[direction];
            newY += dy[direction];

            if (newX >= 0 && newX < gridHeight
                    && newY >= 0 && newY < gridWidth
                    && grille[newX][newY] != '#') {
                monstres.set(i, new Position(newX, newY));
            }
        }
    }

    // --- ATTAQUE ÉPÉE : zone de 1 case autour (et même case) ---
    private void attaquerEpee() {
        int hx = hero.getX();
        int hy = hero.getY();
        boolean cibleTouchee = false;

        ArrayList<Position> aSupprimer = new ArrayList<>();
        for (Position m : monstres) {
            int dx = m.x - hx;
            int dy = m.y - hy;
            if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
                aSupprimer.add(m);
                hero.ajouterScore(50);
                cibleTouchee = true;
            }
        }
        monstres.removeAll(aSupprimer);

        if (zombieVivant) {
            int dxZ = zombie.getPos().x - hx;
            int dyZ = zombie.getPos().y - hy;
            if (Math.abs(dxZ) <= 1 && Math.abs(dyZ) <= 1) {
                zombieVivant = false;
                hero.ajouterScore(50);
                cibleTouchee = true;
            }
        }

        int dxF = fantome.getPos().x - hx;
        int dyF = fantome.getPos().y - hy;
        if (Math.abs(dxF) <= 1 && Math.abs(dyF) <= 1) {
            setMessageHUD("👻 Le fantôme est invincible !");
        }

        if (cibleTouchee) {
            setMessageHUD("🗡️ Coup d'épée réussi !");
        } else {
            setMessageHUD("🗡️ Coup d'épée dans le vide...");
        }
    }

    // --- ATTAQUE ARC : ligne droite jusqu’au mur ---
    private void attaquerArc(int dirX, int dirY) {
        if (dirX == 0 && dirY == 0) return;

        int x = hero.getX();
        int y = hero.getY();

        boolean toucheQuelqueChose = false;
        boolean aToucheMonstre = false;
        boolean aToucheZombie = false;
        boolean aToucheFantome = false;

        while (true) {
            x += dirX;
            y += dirY;

            if (x < 0 || x >= grille.length || y < 0 || y >= grille[0].length) break;
            if (grille[x][y] == '#') break;

            // Monstres
            for (int i = 0; i < monstres.size(); i++) {
                Position m = monstres.get(i);
                if (m.x == x && m.y == y) {
                    monstres.remove(i);
                    hero.ajouterScore(50);
                    toucheQuelqueChose = true;
                    aToucheMonstre = true;
                    i--;
                }
            }

            // Zombie
            if (zombieVivant &&
                zombie.getPos().x == x &&
                zombie.getPos().y == y) {
                zombieVivant = false;
                hero.ajouterScore(50);
                toucheQuelqueChose = true;
                aToucheZombie = true;
            }

            // Fantôme (traversé)
            if (fantome.getPos().x == x && fantome.getPos().y == y) {
                aToucheFantome = true;
            }
        }

        if (aToucheFantome && !toucheQuelqueChose) {
            setMessageHUD("👻 La flèche traverse le fantôme sans effet...");
        } else if (toucheQuelqueChose) {
            String msg = "🏹 Flèche touchée : ";
            if (aToucheMonstre) msg += "monstre ";
            if (aToucheZombie) msg += "zombie ";
            msg += "! +50 points";
            setMessageHUD(msg);
        } else {
            setMessageHUD("🏹 Flèche perdue dans le labyrinthe...");
        }
    }

    private void verifierCollisions() {
        if (partieTerminee) return;

        Position heroPos = new Position(hero.getX(), hero.getY());
        long currentTime = System.currentTimeMillis();
        boolean peutPrendreDegat =
                (currentTime - dernierDegatTime) > DELAI_ENTRE_DEGATS;

        boolean hit = false;
        String typeEnnemi = "";

        if (peutPrendreDegat) {
            if (fantome.getPos().equals(heroPos)) {
                hit = true;
                typeEnnemi = "fantôme";
            } else if (zombieVivant && zombie.getPos().equals(heroPos)) {
                hit = true;
                typeEnnemi = "zombie";
            } else {
                for (Position m : monstres) {
                    if (m.equals(heroPos)) {
                        hit = true;
                        typeEnnemi = "monstre";
                        break;
                    }
                }
            }

            if (hit) {
                hero.perdreVie();
                hero.enleverScore(10);
                dernierDegatTime = currentTime;
                setMessageHUD("💔 Touché par " + typeEnnemi
                        + " ! -1❤️  Vie: " + hero.getPointsDeVie());

                if (hero.getPointsDeVie() <= 0) {
                    finDePartie("GAME OVER ! Votre héros est à terre.", "Défaite");
                }
                return;
            }
        }

        // Cœurs
        for (Heart coeur : coeurs) {
            if (!coeur.estRamassee()
                    && hero.getX() == coeur.getPos().x
                    && hero.getY() == coeur.getPos().y) {
                coeur.ramasser();
                hero.ajouterVie();
                hero.ajouterScore(10);
                int restants = getCoeursRestants();
                setMessageHUD("❤️ Cœur ramassé ! +1 Vie ! +10 points (" + restants + " restant(s))");
                break;
            }
        }

        // Clé
        if (!cle.estRamassee()
                && hero.getX() == cle.getPos().x
                && hero.getY() == cle.getPos().y) {
            cle.ramasser();
            hero.pickKey();
            hero.ajouterScore(10);
            setMessageHUD("🔑 Clé ramassée ! +10 points");
        }

        // Armes : épée et arc peuvent être ramassés tous les deux
        for (Weapon w : armes) {
            if (!w.estRamassee()
                    && hero.getX() == w.getPos().x
                    && hero.getY() == w.getPos().y) {
                w.ramasser();
                if (w.getType() == WeaponType.EPEE) {
                    hero.ramasserEpee();
                    setMessageHUD("🗡️ Épée ramassée ! (1 utilisation)");
                } else if (w.getType() == WeaponType.ARC) {
                    hero.ramasserArc();
                    setMessageHUD("🏹 Arc ramassé ! (1 utilisation)");
                }
                hero.ajouterScore(20);
            }
        }

        // Porte
        if (door != null
                && hero.getX() == door.getPos().x
                && hero.getY() == door.getPos().y) {

            if (!hero.hasKey()) {
                setMessageHUD("🔒 Porte verrouillée ! Trouvez la clé d'abord.");
            } else {
                hero.useKey();
                chronoTimer.stop();
                timerMonstres.stop();
                partieTerminee = true;
                hero.ajouterScore(50);

                long finalTime = (System.currentTimeMillis() - startTime) / 1000;
                JeuLabyrintheLauncher.niveauTermine(hero, finalTime);
            }
        }

        // Trésor (niveau 10)
        if (tresor != null
                && hero.getX() == tresor.getPos().x
                && hero.getY() == tresor.getPos().y) {

            chronoTimer.stop();
            timerMonstres.stop();
            partieTerminee = true;
            hero.ajouterScore(200);

            long finalTime = (System.currentTimeMillis() - startTime) / 1000;
            JeuLabyrintheLauncher.niveauTermine(hero, finalTime);
        }
    }

    // Rendue publique pour le launcher
    public void setMessageHUD(String message) {
        messageHUD.setLength(0);
        messageHUD.append(message);
        messageHUDTime = System.currentTimeMillis();
    }

    private void verifierSiBloque() {
        Position heroPos = new Position(hero.getX(), hero.getY());
        Set<Position> accessibles = trouverZonesAccessibles(heroPos);

        if (accessibles.size() < 10) {
            setMessageHUD("💡 Zone restreinte - Explorez les passages !");
        }
    }

    private Set<Position> trouverZonesAccessibles(Position start) {
        Set<Position> accessible = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();
        queue.add(start);
        accessible.add(start);

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            for (int i = 0; i < 4; i++) {
                int newR = current.x + dr[i];
                int newC = current.y + dc[i];

                if (newR >= 0 && newR < grille.length &&
                        newC >= 0 && newC < grille[0].length) {

                    if (grille[newR][newC] != '#') {
                        Position neighbor = new Position(newR, newC);
                        if (!accessible.contains(neighbor)) {
                            accessible.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
        return accessible;
    }

    private void finDePartie(String message, String titre) {
        timerMonstres.stop();
        chronoTimer.stop();
        partieTerminee = true;

        JOptionPane.showMessageDialog(this, message, titre, JOptionPane.ERROR_MESSAGE);

        int choix = JOptionPane.showConfirmDialog(this,
                "Voulez-vous revenir au menu principal ?",
                "Fin du Jeu", JOptionPane.YES_NO_OPTION);
        if (choix == JOptionPane.YES_OPTION) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (parentFrame != null) parentFrame.dispose();
            MenuPrincipal.lancerNouvellePartie();
        } else {
            System.exit(0);
        }
    }

    // Image de victoire en taille originale + boutons en bas
    public int afficherImageVictoire(int niveau) {
        String path = "/images/" + niveau + ".jpg";
        ImageIcon icon = null;

        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {

                // Charger image origine
                Image img = new ImageIcon(url).getImage();

                // Taille écran
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                int maxW = (int) (screen.width * 0.6);   // 60% largeur écran
                int maxH = (int) (screen.height * 0.75); // 75% hauteur écran

                int imgW = img.getWidth(null);
                int imgH = img.getHeight(null);

                // Ratio d’origine
                double ratio = (double) imgW / imgH;

                // Nouveau ratio qui rentre dans l’écran
                int newW = imgW;
                int newH = imgH;

                if (newW > maxW) {
                    newW = maxW;
                    newH = (int) (newW / ratio);
                }
                if (newH > maxH) {
                    newH = maxH;
                    newW = (int) (newH * ratio);
                }

                Image resized = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                icon = new ImageIcon(resized);
            } else {
                System.err.println("❌ Image introuvable : " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Object[] options = (niveau == 10)
                ? new Object[]{"🏁 Fin du jeu", "❌ Quitter"}
                : new Object[]{"▶ Continuer", "❌ Quitter"};

        return JOptionPane.showOptionDialog(
                null,
                "",  // pas de texte au dessus de l’image
                "Niveau " + niveau + " terminé !",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                icon,
                options,
                options[0]
        );
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Sol + murs
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[0].length; j++) {
                g2.setColor(floorColor);
                g2.fillRect(j * TAILLE_CASE, i * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);

                if (grille[i][j] == '#') {
                    g2.setColor(wallColor);
                    g2.fillRect(j * TAILLE_CASE, i * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
                }
            }
        }

        // Clé
        if (!cle.estRamassee()) {
            g2.drawImage(keyImg, cle.getPos().y * TAILLE_CASE,
                    cle.getPos().x * TAILLE_CASE, null);
        }

        // Porte
        if (door != null) {
            g2.drawImage(doorImg, door.getPos().y * TAILLE_CASE,
                    door.getPos().x * TAILLE_CASE, null);
        }

        // Trésor
        if (tresor != null) {
            g2.drawImage(treasureImg, tresor.getPos().y * TAILLE_CASE,
                    tresor.getPos().x * TAILLE_CASE, null);
        }

        // Cœurs
        for (Heart coeur : coeurs) {
            if (!coeur.estRamassee()) {
                g2.drawImage(heartImg, coeur.getPos().y * TAILLE_CASE,
                        coeur.getPos().x * TAILLE_CASE, null);
            }
        }

        // Armes
        for (Weapon w : armes) {
            if (!w.estRamassee()) {
                Image img = (w.getType() == WeaponType.EPEE) ? swordImg : bowImg;
                g2.drawImage(img, w.getPos().y * TAILLE_CASE,
                        w.getPos().x * TAILLE_CASE, null);
            }
        }

        // Monstres
        for (Position m : monstres) {
            g2.drawImage(monsterImg, m.y * TAILLE_CASE, m.x * TAILLE_CASE, null);
        }

        // Fantôme
        g2.drawImage(ghostImg, fantome.getPos().y * TAILLE_CASE,
                fantome.getPos().x * TAILLE_CASE, null);

        // Zombie
        if (zombieVivant) {
            g2.drawImage(zombieImg, zombie.getPos().y * TAILLE_CASE,
                    zombie.getPos().x * TAILLE_CASE, null);
        }

        // Héros
        g2.drawImage(heroImg, hero.getY() * TAILLE_CASE,
                hero.getX() * TAILLE_CASE, null);

        int hudY = grille.length * TAILLE_CASE + 30;
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Score : " + hero.getScore(), 10, hudY);

        // Barre de vie
        g2.drawString("Vie : ", 150, hudY);
        for (int i = 0; i < 5; i++) {
            if (i < hero.getPointsDeVie()) {
                g2.setColor(Color.RED);
                g2.fillRect(190 + i * 15, hudY - 12, 12, 12);
            }
            g2.setColor(Color.BLACK);
            g2.drawRect(190 + i * 15, hudY - 12, 12, 12);
        }

        // Affichage des armes et usages restants
        String armeStr = "Épée: " + hero.getUsagesEpeeRestants()
                       + " | Arc: " + hero.getUsagesArcRestants();
        g2.drawString("Armes : " + armeStr, 300, hudY);

        g2.drawString("Clé : " + (hero.hasKey() ? "🔑" : "❌"), 520, hudY);
        g2.drawString("Cœurs: " + getCoeursRestants() + "/" + coeurs.size(), 600, hudY);

        // Message HUD
        if (messageHUD.length() > 0 &&
                (System.currentTimeMillis() - messageHUDTime) < DUREE_MESSAGE_HUD) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(10, hudY + 20, getWidth() - 20, 25);
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(messageHUD.toString(), 20, hudY + 37);
        }

        dessinerCarteGlobale(g, hudY + 50);
    }

    private void dessinerCarteGlobale(Graphics g, int mapY) {
        Level[] allLevels = Level.values();
        int currentLevelIndex = Arrays.asList(allLevels).indexOf(currentLevel);
        int circleSize = 18;
        int mapX = 10;

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Progression :", mapX, mapY - 5);

        for (int i = 0; i < allLevels.length; i++) {
            if (i < currentLevelIndex) {
                g.setColor(Color.LIGHT_GRAY);
            } else if (i == currentLevelIndex) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.DARK_GRAY);
            }

            g.fillOval(mapX + i * (circleSize + 10) + 70,
                    mapY, circleSize, circleSize);
            g.setColor(Color.BLACK);
            g.drawOval(mapX + i * (circleSize + 10) + 70,
                    mapY, circleSize, circleSize);

            if (i == allLevels.length - 1) {
                g.setColor(new Color(255, 215, 0));
                g.fillRect(mapX + i * (circleSize + 10) + 70 + 5,
                        mapY + 5, circleSize - 10, circleSize - 10);
            }

            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(i + 1),
                    mapX + i * (circleSize + 10) + 70 + 4,
                    mapY + 14);
        }
    }

    public Heros getHero() { return hero; }
    public ArrayList<Position> getMonstres() { return monstres; }
    public boolean isZombieVivant() { return zombieVivant; }
    public boolean isPartieTerminee() { return partieTerminee; }
    public ArrayList<Heart> getCoeurs() { return coeurs; }
    public Cle getCle() { return cle; }
    public Door getDoor() { return door; }
    public Tresor getTresor() { return tresor; }
}

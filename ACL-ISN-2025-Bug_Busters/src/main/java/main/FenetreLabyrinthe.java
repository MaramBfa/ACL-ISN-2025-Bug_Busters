package main;

import entity.*;
import simple.Position;
import simple.Level;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;

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
    private final int TAILLE_CASE = 40;
    private Level currentLevel; 
    private boolean partieTerminee = false;
    private Timer timerMonstres;

    private Color wallColor;
    private Color floorColor;

    private Image heroImg, monsterImg, ghostImg, zombieImg, keyImg, treasureImg, swordImg, bowImg, heartImg, doorImg; 

    private boolean messageTresorAffiche = false;
    private long startTime;
    private Timer chronoTimer;
    private JLabel chronoLabel;

    private boolean zombieVivant = true;
    
    // Variables pour gérer les messages et les dégâts
    private long dernierDegatTime = 0;
    private static final long DELAI_ENTRE_DEGATS = 1000;
    private StringBuilder messageHUD = new StringBuilder();
    private long messageHUDTime = 0;
    private static final long DUREE_MESSAGE_HUD = 3000;

    // CONSTRUCTEUR CORRIGÉ - AVEC LISTE DE COEURS
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

        // Chargement images
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

        // Couleurs
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

        setPreferredSize(new Dimension(grille[0].length * TAILLE_CASE, grille.length * TAILLE_CASE + 150));
        setFocusable(true);
        startTime = System.currentTimeMillis();
        chronoLabel = new JLabel("Temps : 0 sec - Cœurs restants: " + getCoeursRestants() + "/" + coeurs.size());
        chronoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        chronoLabel.setForeground(Color.BLUE);
        setLayout(new BorderLayout());
        add(chronoLabel, BorderLayout.NORTH);

        chronoTimer = new Timer(1000, e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            chronoLabel.setText("Temps : " + elapsed + " sec - Cœurs restants: " + getCoeursRestants() + "/" + coeurs.size());
        });
        chronoTimer.start();

        // Déplacement héros / Attaque
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (partieTerminee) return;

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attaquer(); 
                } else {
                    hero.deplacer(e.getKeyCode(), grille[0].length, grille.length, grille);
                    verifierCollisions();
                    verifierSiBloque();
                }
                repaint();
            }
        });

        // Timer monstres
        timerMonstres = new Timer(500, e -> {
            if (!partieTerminee) {
                deplacerMonstres(); 
                fantome.move(grille[0].length, grille.length, grille); 
                if (zombieVivant) zombie.moveTowards(new Position(hero.getX(), hero.getY()), grille);
                verifierCollisions();
                repaint();
            }
        });
        timerMonstres.start();
    }
    
    // CORRECTION : Rendre cette méthode PUBLIC
    public void setMessageHUD(String message) {
        messageHUD.setLength(0);
        messageHUD.append(message);
        messageHUDTime = System.currentTimeMillis();
    }

    // Méthode pour compter les cœurs restants
    private int getCoeursRestants() {
        int count = 0;
        for (Heart coeur : coeurs) {
            if (!coeur.estRamassee()) {
                count++;
            }
        }
        return count;
    }
    
    private Image loadImage(String path) {
        java.net.URL location = getClass().getResource(path);
        if (location == null) {
            BufferedImage img = new BufferedImage(TAILLE_CASE, TAILLE_CASE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, TAILLE_CASE, TAILLE_CASE);
            g.dispose();
            System.err.println("⚠️ Image introuvable : " + path);
            return img;
        }
        Image img = new ImageIcon(location).getImage();
        return img.getScaledInstance(TAILLE_CASE, TAILLE_CASE, Image.SCALE_SMOOTH);
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

            if (newX >= 0 && newX < gridHeight && newY >= 0 && newY < gridWidth && grille[newX][newY] != '#') {
                monstres.set(i, new Position(newX, newY));
            }
        }
    }

    private void attaquer() {
        if (!hero.aUneArme()) {
            setMessageHUD("❌ Pas d'arme ! (Touche Entrée)");
            return;
        }

        int range = (hero.getWeapon() == WeaponType.EPEE) ? 1 : 3; 
        int targetX;
        int targetY;

        boolean cibleTouchee = false;

        for (int i = 1; i <= range; i++) {
            targetX = hero.getX();
            targetY = hero.getY();

            switch (hero.getDirection()) {
                case "up" -> targetX -= i;
                case "down" -> targetX += i;
                case "left" -> targetY -= i;
                case "right" -> targetY += i;
            }

            if (targetX < 0 || targetX >= grille.length || targetY < 0 || targetY >= grille[0].length || grille[targetX][targetY] == '#') {
                break;
            }

            // 1. Monstre normal
            for (int j = 0; j < monstres.size(); j++) {
                Position m = monstres.get(j);
                if (targetX == m.x && targetY == m.y) {
                    monstres.remove(j);
                    hero.ajouterScore(50);
                    setMessageHUD("💥 Monstre éliminé ! +50 points");
                    cibleTouchee = true;
                    break;
                }
            }

            // 2. Zombie
            if (!cibleTouchee && zombieVivant && targetX == zombie.getPos().x && targetY == zombie.getPos().y) {
                zombieVivant = false;
                hero.ajouterScore(50);
                setMessageHUD("🧟 Zombie éliminé ! +50 points");
                cibleTouchee = true;
            }

            // 3. Fantôme
            if (!cibleTouchee && targetX == fantome.getPos().x && targetY == fantome.getPos().y) {
                setMessageHUD("👻 Le fantôme est invincible !");
                cibleTouchee = true;
            }

            if (cibleTouchee || hero.getWeapon() == WeaponType.EPEE) {
                break;
            }
        }
        
        if (!cibleTouchee) {
            setMessageHUD("⚔️ Attaque dans le vide...");
        }
    }

    private void verifierCollisions() {
        if (partieTerminee) return;
        
        Position heroPos = new Position(hero.getX(), hero.getY());
        
        // Vérifier le délai entre les dégâts
        long currentTime = System.currentTimeMillis();
        boolean peutPrendreDegat = (currentTime - dernierDegatTime) > DELAI_ENTRE_DEGATS;
        
        // Collisions avec les entités hostiles
        if (hero.getPointsDeVie() > 0 && peutPrendreDegat) {
            boolean hit = false;
            String typeEnnemi = "";
            
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
                
                String message = "💔 Touché par " + typeEnnemi + "! -1❤️  Vie: " + hero.getPointsDeVie();
                setMessageHUD(message);
                
                if (hero.getPointsDeVie() <= 0) {
                    finDePartie("GAME OVER ! Votre héros est à terre.", "Défaite");
                }
                return;
            }
        }

        // Ramassage des CŒURS (tous les cœurs de la liste)
        for (Heart coeur : coeurs) {
            if (!coeur.estRamassee() && hero.getX() == coeur.getPos().x && hero.getY() == coeur.getPos().y) {
                coeur.ramasser();
                hero.ajouterVie(); 
                hero.ajouterScore(10);
                int coeursRestants = getCoeursRestants();
                setMessageHUD("❤️ Cœur ramassé ! +1 Vie ! +10 points (" + coeursRestants + " restant(s))");
                break;
            }
        }
        
        if (!cle.estRamassee() && hero.getX() == cle.getPos().x && hero.getY() == cle.getPos().y) {
            cle.ramasser();
            hero.pickKey();
            hero.ajouterScore(10);
            setMessageHUD("🔑 Clé ramassée ! +10 points");
        }
        
        for (Weapon w : armes) {
            if (!w.estRamassee() && hero.getX() == w.getPos().x && hero.getY() == w.getPos().y) {
                w.ramasser();
                hero.setWeapon(w.getType());
                hero.ajouterScore(20);
                setMessageHUD("⚔️ " + w.getType() + " ramassé ! +20 points");
            }
        }

        // Interaction avec la PORTE
        if (door != null && hero.getX() == door.getPos().x && hero.getY() == door.getPos().y) {
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

        // Interaction avec le TRESOR (uniquement niveau 10)
        if (tresor != null && hero.getX() == tresor.getPos().x && hero.getY() == tresor.getPos().y) {
            chronoTimer.stop();
            timerMonstres.stop();
            partieTerminee = true;
            hero.ajouterScore(200);
            
            long finalTime = (System.currentTimeMillis() - startTime) / 1000;
            JeuLabyrintheLauncher.niveauTermine(hero, finalTime);
        }
    }

    private void verifierSiBloque() {
        Position heroPos = new Position(hero.getX(), hero.getY());
        Set<Position> accessibles = trouverZonesAccessibles(heroPos);
        
        // Si le héros est dans une petite zone isolée (moins de 10 cases accessibles)
        if (accessibles.size() < 10) {
            setMessageHUD("💡 Zone restreinte - Explorez les passages!");
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
                if (newR >= 0 && newR < grille.length && newC >= 0 && newC < grille[0].length) {
                    Position neighbor = new Position(newR, newC);
                    if (grille[newR][newC] != '#' && !accessible.contains(neighbor)) {
                        accessible.add(neighbor);
                        queue.add(neighbor);
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
        
        // DEBUG
        System.out.println("🔍 DEBUG - Avant Game Over:");
        System.out.println("Niveau actuel: " + JeuLabyrintheLauncher.niveauActuel);
        System.out.println("Dernier niveau: " + JeuLabyrintheLauncher.getDernierNiveauAtteint());
        
        JOptionPane.showMessageDialog(this, message, titre, JOptionPane.ERROR_MESSAGE);
        
        // DEBUG
        System.out.println("🔍 DEBUG - Après Game Over:");
        System.out.println("Niveau actuel: " + JeuLabyrintheLauncher.niveauActuel);
        System.out.println("Dernier niveau: " + JeuLabyrintheLauncher.getDernierNiveauAtteint());
        
        int choix = JOptionPane.showConfirmDialog(this, "Voulez-vous revenir au menu principal ?", "Fin du Jeu", JOptionPane.YES_NO_OPTION);
        if (choix == JOptionPane.YES_OPTION) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            parentFrame.dispose();
            MenuPrincipal.lancerNouvellePartie(); 
        } else {
            System.exit(0);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // --- 1. Dessin du labyrinthe et des sols ---
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
        
        // --- 2. Dessin des entités ---
        if (!cle.estRamassee()) { 
            g2.drawImage(keyImg, cle.getPos().y * TAILLE_CASE, cle.getPos().x * TAILLE_CASE, null); 
        }
        
        // PORTE
        if (door != null) { 
            g2.drawImage(doorImg, door.getPos().y * TAILLE_CASE, door.getPos().x * TAILLE_CASE, null); 
        }
        
        // TRESOR (uniquement niveau 10)
        if (tresor != null) { 
            g2.drawImage(treasureImg, tresor.getPos().y * TAILLE_CASE, tresor.getPos().x * TAILLE_CASE, null); 
        }
        
        // TOUS LES CŒURS
        for (Heart coeur : coeurs) {
            if (!coeur.estRamassee()) { 
                g2.drawImage(heartImg, coeur.getPos().y * TAILLE_CASE, coeur.getPos().x * TAILLE_CASE, null); 
            }
        }
        
        for (Weapon w : armes) {
            if (!w.estRamassee()) {
                Image img = (w.getType() == WeaponType.EPEE) ? swordImg : bowImg;
                g2.drawImage(img, w.getPos().y * TAILLE_CASE, w.getPos().x * TAILLE_CASE, null);
            }
        }
        
        for (Position m : monstres) { 
            g2.drawImage(monsterImg, m.y * TAILLE_CASE, m.x * TAILLE_CASE, null); 
        }
        
        g2.drawImage(ghostImg, fantome.getPos().y * TAILLE_CASE, fantome.getPos().x * TAILLE_CASE, null);
        
        if (zombieVivant) { 
            g2.drawImage(zombieImg, zombie.getPos().y * TAILLE_CASE, zombie.getPos().x * TAILLE_CASE, null); 
        }
        
        g2.drawImage(heroImg, hero.getY() * TAILLE_CASE, hero.getX() * TAILLE_CASE, null);
        
        // --- 3. HUD amélioré ---
        int hudY = grille.length * TAILLE_CASE + 30;
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Score : " + hero.getScore(), 10, hudY);
        
        // Barre de vie visuelle
        g2.drawString("Vie : ", 150, hudY);
        for (int i = 0; i < 5; i++) {
            if (i < hero.getPointsDeVie()) {
                g2.setColor(Color.RED);
                g2.fillRect(190 + i * 15, hudY - 12, 12, 12);
            }
            g2.setColor(Color.BLACK);
            g2.drawRect(190 + i * 15, hudY - 12, 12, 12);
        }
        
        String armeStr = hero.aUneArme() ? hero.getWeapon().toString() : "Aucune";
        g2.drawString("Arme : " + armeStr, 300, hudY);
        g2.drawString("Clé : " + (hero.hasKey() ? "🔑" : "❌"), 450, hudY);
        g2.drawString("Cœurs: " + getCoeursRestants() + "/" + coeurs.size(), 520, hudY);
        
        // Affichage des messages dans le HUD
        if (messageHUD.length() > 0 && (System.currentTimeMillis() - messageHUDTime) < DUREE_MESSAGE_HUD) {
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

            g.fillOval(mapX + i * (circleSize + 10) + 70, mapY, circleSize, circleSize);
            g.setColor(Color.BLACK);
            g.drawOval(mapX + i * (circleSize + 10) + 70, mapY, circleSize, circleSize);

            if (i == allLevels.length - 1) {
                g.setColor(new Color(255, 215, 0)); 
                g.fillRect(mapX + i * (circleSize + 10) + 70 + 5, mapY + 5, circleSize - 10, circleSize - 10);
            }

            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(i + 1), mapX + i * (circleSize + 10) + 70 + 4, mapY + 14);
        }
    }

    // Getters pour les tests
    public Heros getHero() { return hero; }
    public ArrayList<Position> getMonstres() { return monstres; }
    public boolean isZombieVivant() { return zombieVivant; }
    public boolean isPartieTerminee() { return partieTerminee; }
    public ArrayList<Heart> getCoeurs() { return coeurs; }
    public Cle getCle() { return cle; }
    public Door getDoor() { return door; }
    public Tresor getTresor() { return tresor; }
}
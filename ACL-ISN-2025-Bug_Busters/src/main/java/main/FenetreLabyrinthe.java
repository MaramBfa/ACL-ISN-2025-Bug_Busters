package main;

import entity.Heros;
import entity.Monstre;
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

//importation des librairies swing pour l'affichage et la fenêtre du jeu
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

//importation des librairies graphiques (couleurs, images, dessin...)
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * FenetreLabyrinthe:
 * classe principale qui gère l'affichage du labyrinthe, les déplacements,
 * les collisions, les monstres, les armes, la musique, bref tout le gameplay
 */

public class FenetreLabyrinthe extends JPanel {

    private static final long serialVersionUID = 1L;


    	/**
    	 * petite classe interne qui regroupe les chemins des images :
    	 * une image png pour le sol
    	 * une image png pour les murs
    	 * chaque niveau utilise un thème différent pour donner un style visuel différent 
    	 */   
    	private static class Theme {
        final String floorPath; //chemin de l'image du sol
        final String wallPath;//chemin de l'image du mur

        Theme(String floorPath, String wallPath) {
            this.floorPath = floorPath;
            this.wallPath = wallPath;
        }
    }


    private static final Theme[] THEMES = new Theme[] {
            //niveau 1
            new Theme("/tiles/floor1.jpg", "/tiles/wall1.jpg"),
            //niveau 2
            new Theme("/tiles/floor2.jpg", "/tiles/wall1.jpg"),
            //niveau 3
            new Theme("/tiles/floor1.jpg", "/tiles/wall2.jpg"),
            //niveau 4
            new Theme("/tiles/floor3.jpg", "/tiles/wall3.jpg"),
            //niveau 5
            new Theme("/tiles/floor4.jpg", "/tiles/wall2.jpg"),
            //niveau6
            new Theme("/tiles/floor2.jpg", "/tiles/wall4.jpg"),
            //niveau 7
            new Theme("/tiles/floor3.jpg", "/tiles/wall1.jpg"),
            //niveau 8
            new Theme("/tiles/floor4.jpg", "/tiles/wall3.jpg"),
            //niveau 9
            new Theme("/tiles/floor1.jpg", "/tiles/wall4.jpg"),
            //niveau 10
            new Theme("/tiles/floor4.jpg", "/tiles/wall4.jpg")
    };


    private char[][] grille;
    private Heros hero;
    private ArrayList<Monstre> monstres; //tableau de monstres(augmente avec le niveau)
    private ArrayList<Weapon> armes; //tableau d'armes(épée ou arc)
    private Cle cle; // une seule clé
    private Door door; //une seule porte
    private Tresor tresor; //un seul trésor
    private ArrayList<Heart> coeurs;  //tableau de coeurs , plusieurs vies
    private Ghost fantome; //un seul fantôme à la fois
    private Zombie zombie; // un seul zombie à la fois
    private boolean zombieVivant = true;

    private final int TAILLE_CASE = 40;//taille d'une case d'une grille
    private Level currentLevel;//level actuel
    private boolean partieTerminee = false;//timer qui est affiché dans chaque partie en haut

    private javax.swing.Timer timerMonstres; //timer qui contrôle les déplacements des monstres
    private javax.swing.Timer chronoTimer;

    // tiles du labyrinthe
    private Image tileFloor, tileWall;

    // Sprites entités
    private Image heroLeftImg, heroRightImg;
    private Image monsterImg, ghostImg, zombieImg,
            keyImg, treasureImg, swordImg, bowImg, heartImg, doorImg;

    //images des coeurs affichés dans le HUD , coeurs rempli / coeurs vides
    private Image heartFullImg, heartEmptyImg;

    private long startTime;   //utilisé pour calculer le temps écoulé
    private JLabel chronoLabel; //affiche le temps et le nombre de cœurs restants


    private long dernierDegatTime = 0;  //temps depuis la dernière collision (pour éviter spam de dégâts)
    private static final long DELAI_ENTRE_DEGATS = 2000; //délai entre deux dégâts (1 seconde)
    
    private StringBuilder messageHUD = new StringBuilder(); //message temporaire affiché à l'écran
    private long messageHUDTime = 0; //pour reperer quand effacer le message
    private static final long DUREE_MESSAGE_HUD = 3000; //message reste visible 3secs sur le hub


    //système de tir à l'arc
    private boolean bowAiming = false;  //true quand le joueur a appuyé sur T pour viser le sens de l'arc

    //système d’animation d’attaque
    private boolean animationAttaque = false;
    private long timeAttaque = 0;
    private static final int DUREE_ATTAQUE = 150; //durée de l'effet visuel
    private static final int DUREE_TRAJECTOIRE_FLECHE = 150; //durée en ms d'affichage de la trajectoire


    //trajectoire visuelle de la flèche quand on tire
    private java.util.List<Position> arrowTrail = new ArrayList<>();
    private long arrowTrailTime = 0;

    //direction horizontale du héros (pour savoir quel sprite afficher)
    private String lastHorizontalDirection = "right";

    //bouton ON/OFF pour la musique du jeu
    private boolean musicOn = true;
    private Button musicToggleBtn;


    public FenetreLabyrinthe(char[][] grille, Heros hero,
                             ArrayList<Monstre> monstres,
                             Cle cle, Door door, Tresor tresor,
                             ArrayList<Weapon> armes,
                             Ghost fantome, Zombie zombie,
                             Level level, ArrayList<Heart> coeurs) {
    	
        //on récupère toutes les informations envoyées par le JeuLAbyrithelauncher
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

        //on adapte le theme selon l'index du niveau
        int levelIndex = Arrays.asList(Level.values()).indexOf(level);
        
        //dans lecas d'une erreur ou une valeur limite , par exemple niveau<0, on force le thème 0(default)
        if (levelIndex < 0 || levelIndex >= THEMES.length) {
            levelIndex = 0;
        }
        //on récupère le thème correspondant au niveau
        Theme theme = THEMES[levelIndex];
        //chargement des images du sol et du mur depuis le thème
        tileFloor = loadImage(theme.floorPath);
        tileWall  = loadImage(theme.wallPath);

        //chargement des sprites du jeu
        heroLeftImg  = loadImage("/images/hero_left.png");//hero vue de gauche
        heroRightImg = loadImage("/images/hero_right.png");//hero vue de droite
        monsterImg   = loadImage("/images/monster.png");
        ghostImg     = loadImage("/images/ghost.png");
        zombieImg    = loadImage("/images/zombie.png");
        keyImg       = loadImage("/images/key.png");
        treasureImg  = loadImage("/images/treasure.png");
        swordImg     = loadImage("/images/sword.png");
        bowImg       = loadImage("/images/bow.png");
        heartImg     = loadImage("/images/Coeur.png"); //coeur posé au sol 
        doorImg      = loadImage("/images/door.png");

        //images pour l'affichage des vies du héros dans le hud
        heartFullImg  = loadImage("/images/heart_full.png");
        heartEmptyImg = loadImage("/images/heart_empty.png");

        //on définit la taille de la fenêtre selon la grille
        setPreferredSize(new Dimension(
                grille[0].length * TAILLE_CASE, //largeur
                grille.length * TAILLE_CASE + 150));//hauteur+zone HUD
        
        setFocusable(true);//pour que le panel puisse recevoir les touches clavier
        setLayout(new BorderLayout()); //layout simple pour HUD + zone de jeu

        //Chrono
        startTime = System.currentTimeMillis(); //heure du début
        chronoLabel = new JLabel("Temps : 0 sec - Cœurs restants: "
                + getCoeursRestants() + "/" + coeurs.size());
        
        //config du style de texte
        chronoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        chronoLabel.setForeground(Color.WHITE);

        //bouton musique(toggle OFF/ON)
        musicToggleBtn = new Button("🔊");
        musicToggleBtn.setBackground(Color.BLACK);
        musicToggleBtn.setForeground(Color.WHITE);
        musicToggleBtn.addActionListener(e -> {
            toggleMusic();
            //on redonne le focus clavier pour les éléments du jeu après modification de la musique
            FenetreLabyrinthe.this.requestFocusInWindow();
        });

        //panel supérieur avec chrono + bouton musique
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLACK);
        topPanel.add(chronoLabel, BorderLayout.WEST);
        topPanel.add(musicToggleBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        
        //timer pour mettre à jour le temps toutes les secondes
        chronoTimer = new Timer(1000, e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            chronoLabel.setText("Temps : " + elapsed
                    + " sec - Cœurs restants: "
                    + getCoeursRestants() + "/" + coeurs.size());
        });
        chronoTimer.start();

        //musique jungle du niveau (en boucle, uniquement pendant la partie)
        Sound.loop("/sounds/jungle_ambient.wav");
        musicOn = true;
        musicToggleBtn.setLabel("🔊");

        //gestion clavier: flèches pour bouger, T puis flèches pour l’arc, ESPACE pour épée
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (partieTerminee) return; //si la partie est finie alors aucune action

                int key = e.getKeyCode();

                //Attaque avec l'épée (en utilisant "ESPACE")
                if (key == KeyEvent.VK_SPACE) {
                    if (hero.peutUtiliserEpee()) {
                        demarrerAnimationAttaque();
                        boolean touche = attaquerEpee();
                        if (touche) {
                            hero.consommerEpee();
                        }
                    } else if (hero.aEpee()) {
                        setMessageHUD("🗡️ Tu as déjà utilisé ton épée !");
                    } else {
                        setMessageHUD("❌ Pas d'épée (Espace)");
                    }
                    repaint();
                    return;
                }

                //^préparation Arc (T) pour tirer
                if (key == KeyEvent.VK_T) {
                    if (hero.peutUtiliserArc()) {
                        bowAiming = true; //on passe en mode visée
                        setMessageHUD("🏹 Choisis une direction avec les flèches ↑ ↓ ← →");
                    } else if (hero.aArc()) {
                        setMessageHUD("🏹 Arc déjà utilisé !");
                    } else {
                        setMessageHUD("❌ Pas d'arc (T)");
                    }
                    repaint();
                    return;
                }

                //choisir la direction du tir de l’arc par les flèches
                if (bowAiming &&
                        (key == KeyEvent.VK_UP ||
                         key == KeyEvent.VK_DOWN ||
                         key == KeyEvent.VK_LEFT ||
                         key == KeyEvent.VK_RIGHT)) {

                    int dx = 0, dy = 0;

                    if (key == KeyEvent.VK_UP) dx = -1; //haut
                    if (key == KeyEvent.VK_DOWN) dx = 1; //bas
                    if (key == KeyEvent.VK_LEFT) dy = -1; //gauche
                    if (key == KeyEvent.VK_RIGHT) dy = 1; //droite

                    demarrerAnimationAttaque();
                    boolean touche = attaquerArc(dx, dy);
                    if (touche) {
                        hero.consommerArc();
                    }
                    bowAiming = false; //ici on sort du mode visé

                    repaint();
                    return;
                }

                //déplacement du héro
                if (!bowAiming &&
                        (key == KeyEvent.VK_UP ||
                         key == KeyEvent.VK_DOWN ||
                         key == KeyEvent.VK_LEFT ||
                         key == KeyEvent.VK_RIGHT)) {

                	//déplaecment dans la grille
                    hero.deplacer(key, grille[0].length, grille.length, grille);

                    //mise à jour direction horizontale pour pouvoir mettre à jour la direction de l'image du hero
                    String dir = hero.getDirection();
                    if ("left".equals(dir)) {
                        lastHorizontalDirection = "left";
                    } else if ("right".equals(dir)) {
                        lastHorizontalDirection = "right";
                    }
                    //verification de la collision
                    verifierCollisions();
                    verifierSiBloque();
                    repaint();
                }
            }
        });

        //timer pour monstre , zombie et fantôme
        timerMonstres = new Timer(500, e -> {
            if (!partieTerminee) {
                deplacerMonstres(); //déplacement des monstres de couleur violette (monstres normaux)
                fantome.move(grille[0].length, grille.length, grille);
                if (zombieVivant) {
                    zombie.moveTowards(new Position(hero.getX(), hero.getY()), grille);
                }
                verifierCollisions(); //si le monstre touche le hero
                repaint();
            }
        });
        timerMonstres.start();

        //on force le panel à recevoir les touches une fois affiché
        SwingUtilities.invokeLater(() -> FenetreLabyrinthe.this.requestFocusInWindow());
    }

    //gestion (toggle ON/OFF)
    private void toggleMusic() {
        if (musicOn) {
            //de ON à OFF: on coupe la musique
            Sound.stopMusic();
            musicOn = false;
            musicToggleBtn.setLabel("🔇");
        } else {
            //de OFF à ON: on relance le fichier depuis le début
            Sound.stopMusic();
            Sound.loop("/sounds/jungle_ambient.wav");
            musicOn = true;
            musicToggleBtn.setLabel("🔊");
        }
    }

    //lance l'effet visuel d'attaque autour du héro
    private void demarrerAnimationAttaque() {
        animationAttaque = true;
        timeAttaque = System.currentTimeMillis();  //on enregistre le moment où l'attaque commence
    }

 //charge une image depuis le dossier resources  , si image non existane on met un carré violet 
    //comme ça on sait que une image manque 
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
  //compte combien de coeurs il reste sur la carte (ceux non ramassés)
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
    //on fait bouger les monstres dans une direction au hasard (principe du jeu)
    private void deplacerMonstres() {
        Random rand = new Random();
        int[] dx = {-1, 1, 0, 0}; //deplacements possibles sur l'axe des x
        int[] dy = {0, 0, -1, 1};//deplacements possibles sur l'axe des Y
        int gridHeight = grille.length;
        int gridWidth = grille[0].length;

        for (int i = 0; i < monstres.size(); i++) {
            Monstre m = monstres.get(i);
            Position pos = m.getPos();
            int newX = pos.x;
            int newY = pos.y;

            int direction = rand.nextInt(4); //on choisit aléatoirement une direction
            newX += dx[direction];
            newY += dy[direction];
            //on vérifie que le monstre ne sort pas du labyrinthe et ne rentre pas dans un mur
            if (newX >= 0 && newX < gridHeight
                    && newY >= 0 && newY < gridWidth
                    && grille[newX][newY] != '#') {
                m.setPos(new Position(newX, newY));
            }
        }
    }
    //attaque à l'épée, touche toutes les cases autour du héros (carré 3x3)
    private boolean attaquerEpee() {
        int hx = hero.getX();
        int hy = hero.getY();
        boolean cibleTouchee = false;

        ArrayList<Monstre> aSupprimer = new ArrayList<>();
        
        //on regarde chaque monstre pour voir s'il est assez proche de l'attaque
        for (Monstre m : monstres) {
            Position pos = m.getPos();
            int dx = pos.x - hx;
            int dy = pos.y - hy;
            
            //si le monstre est dans la zone d'attaqe on le supprime
            if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
                aSupprimer.add(m);
                hero.ajouterScore(50);
                cibleTouchee = true;
                Sound.play("/sounds/monster_snarl.wav");
            }
        }
        monstres.removeAll(aSupprimer);

        //on regarde le zombie s'il est assez proche de l'attaque
        if (zombieVivant) {
            int dxZ = zombie.getPos().x - hx;
            int dyZ = zombie.getPos().y - hy;
            //si dans la zone d'attaque on le suprrime et on ajoute des points qu score (50)
            if (Math.abs(dxZ) <= 1 && Math.abs(dyZ) <= 1) {
                zombieVivant = false;
                hero.ajouterScore(50);
                cibleTouchee = true;
                Sound.play("/sounds/zombie_growl.wav");
            }
        }

        //si l'épée touche le fantôme on affiche juste un message pour dire qu'on ne peut pas tuer un fantôme
        int dxF = fantome.getPos().x - hx;
        int dyF = fantome.getPos().y - hy;
        if (Math.abs(dxF) <= 1 && Math.abs(dyF) <= 1) {
            Sound.play("/sounds/ghost_pass.wav");
            setMessageHUD("👻 Le fantôme est invincible !");
        }

        if (cibleTouchee) {
            setMessageHUD("🗡️ Coup d'épée réussi !");
        } else {
            setMessageHUD("🗡️ Coup d'épée dans le vide...");
        }
        
      //retourne vrai si au moins une cible a été touchée
        return cibleTouchee;
    }

    //attaque à l'arc, la flèche part tout droit jusqu'à toucher quelque chose ou un mur
    private boolean attaquerArc(int dirX, int dirY) {
        if (dirX == 0 && dirY == 0) return false; //si pas de direction, pas de tir 

        int x = hero.getX();
        int y = hero.getY();

        boolean toucheQuelqueChose = false;
        boolean aToucheMonstre = false;
        boolean aToucheZombie = false;
        boolean aToucheFantome = false;

        //on efface l'ancienne trajectoire(virtuelle)
        arrowTrail.clear();
        
        //la flèche avance case par case
        while (true) {
            x += dirX;
            y += dirY;
            
            //si elle sort du labyrinthe ou touche un mur, on arrête la flèche
            if (x < 0 || x >= grille.length || y < 0 || y >= grille[0].length) break;
            if (grille[x][y] == '#') break;

            //on garde cette position pour dessiner un petit carré jaune(effet visuel)
            arrowTrail.add(new Position(x, y));

            //on regarde si un monstre est sur la trajectoire pour l'attaquer
            for (int i = 0; i < monstres.size(); i++) {
                Monstre m = monstres.get(i);
                Position pos = m.getPos();
                if (pos.x == x && pos.y == y) {
                	//si oui , on le supprime et on imcrémente le score de 50
                    monstres.remove(i);
                    hero.ajouterScore(50);
                    toucheQuelqueChose = true;
                    aToucheMonstre = true;
                    i--; //pour éviter de sauter un monstre
                    Sound.play("/sounds/arrow_hit.wav");
                }
            }

            //si la fleche touche à un zombie
            if (zombieVivant &&
                    zombie.getPos().x == x &&
                    zombie.getPos().y == y) {
                zombieVivant = false;
                hero.ajouterScore(50);
                toucheQuelqueChose = true;
                aToucheZombie = true;
                Sound.play("/sounds/arrow_hit.wav");
            }

            //si la flèche traverse le fantôme (il ne peut pas mourir)
            if (fantome.getPos().x == x && fantome.getPos().y == y) {
                aToucheFantome = true;
                Sound.play("/sounds/ghost_pass.wav");
            }
        }

        //on affiche la trajéctoire pendant un court instant
        if (!arrowTrail.isEmpty()) {
            arrowTrailTime = System.currentTimeMillis();
        }
        
        //on affiche un message différent selon ce qui a été touché partie (HUD)
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

        return toucheQuelqueChose;
    }

    private void verifierCollisions() {
        //si la partie est finie on vérifie plus rien pour éviter des actions en retard
        if (partieTerminee) return;
        
        //position actuelle du héros sous forme d'un nouveau objet position
        Position heroPos = new Position(hero.getX(), hero.getY());
        
        //on récupère le temps actuel pour comparer avec le temps du dernier dégat 
        long currentTime = System.currentTimeMillis();
        
        //on vérifie si on peut reprendre des dégâts
        //ça empêche d'en perdre plusieurs d'un coup en restant collé à un monstre
        boolean peutPrendreDegat =
                (currentTime - dernierDegatTime) > DELAI_ENTRE_DEGATS;
                
        //on prépare des variables pour savoir si on a touché un ennemi et lequel
        boolean hit = false;
        String typeEnnemi = "";

        /* 
         * on vérifie d'abord le fantôme, ensuite le zombie, ensuite les monstres normaux
		 * equals() compare les positions
		 * si on touche quelque chose alors hit=true et on garde son nom pour le message
         */
        if (peutPrendreDegat) {
            if (fantome.getPos().equals(heroPos)) {
                hit = true;
                typeEnnemi = "fantôme";
            } else if (zombieVivant && zombie.getPos().equals(heroPos)) {
                hit = true;
                typeEnnemi = "zombie";
            } else {
                for (Monstre m : monstres) {
                    if (m.getPos().equals(heroPos)) {
                        hit = true;
                        typeEnnemi = "monstre";
                        break;
                    }
                }
            }
            //chaque ennemi a son bruit quand il touche le héro
            if (hit) {
                if ("monstre".equals(typeEnnemi)) {
                    Sound.play("/sounds/monster_snarl.wav");
                } else if ("zombie".equals(typeEnnemi)) {
                    Sound.play("/sounds/zombie_growl.wav");
                } else if ("fantôme".equals(typeEnnemi)) {
                    Sound.play("/sounds/ghost_pass.wav");
                }

                hero.perdreVie(); //on enlève 1 vie au héros
                hero.enleverScore(10);  //il perd aussi 10 points
                dernierDegatTime = currentTime;  //on met à jour le moment où il a pris le dégât pour les verifications
                
                //on affiche un message temporaire en bas
                setMessageHUD("💔 Touché par " + typeEnnemi + " ! -1❤️  Vie: " + hero.getPointsDeVie());  

                if (hero.getPointsDeVie() <= 0) {
                    finDePartie("GAME OVER ! Votre héros est à terre.", "Défaite");
                }
                return;
                
                //on quitte la méthode,sinon on ramasse un cœur ou une arme en même temps
            } 
        }

        //ramassage des coeurs (points de vie)
        for (Heart coeur : coeurs) {
        	//si on marche sur un cœur non ramassé
            if (!coeur.estRamassee()
                    && hero.getX() == coeur.getPos().x
                    && hero.getY() == coeur.getPos().y) {
                coeur.ramasser();
                //on récupère 1 vie et +10 points et on enleve le coeur de la map pour touujours
                hero.ajouterVie();
                hero.ajouterScore(10); 
                int restants = getCoeursRestants();
                setMessageHUD("❤️ Cœur ramassé ! +1 Vie ! +10 points (" + restants + " restant(s))");
                Sound.play("/sounds/heart.wav");
                break;
            }
        }

        //fonctionnement de la clé
        //la clé débloque la porte du niveau
        if (!cle.estRamassee()
                && hero.getX() == cle.getPos().x
                && hero.getY() == cle.getPos().y) {
            cle.ramasser();
            hero.pickKey();
            //on gagne aussi 10 points
            hero.ajouterScore(10);
            setMessageHUD("🔑 Clé ramassée ! +10 points");
            Sound.play("/sounds/key_pickup.wav");
        }

        //ragammasage des amrs: épée et arc peuvent être ramassés tous les deux
        //chaque arme a une seule utilisation
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
                //on gagne +20 points
                hero.ajouterScore(20);
                Sound.play("/sounds/heart.wav");
            }
        }

        //la porte
        //si le héros marche sur la porte
        if (door != null
                && hero.getX() == door.getPos().x
                && hero.getY() == door.getPos().y) {
        	
        	//si pas de clé on  ne peut pas sortir
            if (!hero.hasKey()) {
                setMessageHUD("🔒 Porte verrouillée ! Trouvez la clé d'abord.");
            } else { //si on a la clé on continue

            	//on consomme la clé
                hero.useKey();

                //on arrête lechrono
                chronoTimer.stop();
                timerMonstres.stop(); //on arrête les monstres
                partieTerminee = true;
                hero.ajouterScore(50); //bonus de fin de niveau
                hero.resetArmes();//on supprime les armes, pas de carry over
                Sound.stopMusic();//on coupe la musique

                //sons à démarrer
                Sound.play("/sounds/door_unlock.wav");
                Sound.play("/sounds/victory_jingle.wav");

                //on envoie les infos au launcher (score + temps)
                long finalTime = (System.currentTimeMillis() - startTime) / 1000;

                //le launcher charge le niveau suivant
                JeuLabyrintheLauncher.niveauTermine(hero, finalTime);
            }
        }


        //au niveau 10 il y a un trésor à ramasser pour finir le jeu
        if (tresor != null
                && hero.getX() == tresor.getPos().x
                && hero.getY() == tresor.getPos().y) {

            chronoTimer.stop();
            timerMonstres.stop();
            partieTerminee = true;
            hero.ajouterScore(200); // bonus final de 200 points pour le score
            

            //coupure de la musique
            Sound.stopMusic();
            Sound.play("/sounds/victory_jingle.wav");

            long finalTime = (System.currentTimeMillis() - startTime) / 1000;
            JeuLabyrintheLauncher.niveauTermine(hero, finalTime);
        }
    }

    //affiche un message dans le hud pendant quelques secondes
    public void setMessageHUD(String message) {
        messageHUD.setLength(0); //on vide l'ancien message
        messageHUD.append(message);
        messageHUDTime = System.currentTimeMillis(); //on note quand il a été affiché 
    }

    private void verifierSiBloque() {
        Position heroPos = new Position(hero.getX(), hero.getY());
        Set<Position> accessibles = trouverZonesAccessibles(heroPos);

        if (accessibles.size() < 10) {
            setMessageHUD("💡 Zone restreinte - Explorez les passages !");
        }
    }

    //renvoie toutes les cases accessibles depuis la position du héro
    //on utilise un petit bfs pour parcourir les cases ouvertes
    private Set<Position> trouverZonesAccessibles(Position start) {
        Set<Position> accessible = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();
        queue.add(start);
        accessible.add(start);
        
        
       //les déplacements possibles
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

       //on explore les voisins de chaque case
        while (!queue.isEmpty()) {
            Position current = queue.poll();
            for (int i = 0; i < 4; i++) {
                int newR = current.x + dr[i];
                int newC = current.y + dc[i];
               //on n'ajoute que les cases libres
               //au final on obtient la liste de toutes les cases accessibles depuis le héros
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
    	
        //on arrête tous les timers pour figer le jeu quand la partie se termine
        timerMonstres.stop();
        chronoTimer.stop();
        partieTerminee = true;

        //on coupe la musique du niveau et on met en marcheun son d'échec
        Sound.play("/sounds/fail.wav");

         //on affiche une fenêtre qui dit que c'est fini 
        JOptionPane.showMessageDialog(this, message, titre, JOptionPane.ERROR_MESSAGE);
        
        //fenêtre qui demande si on veut retourner au menu
        int choix = JOptionPane.showConfirmDialog(this,
                "Voulez-vous revenir au menu principal ?",
                "Fin du Jeu", JOptionPane.YES_NO_OPTION);
        if (choix == JOptionPane.YES_OPTION) {
        	//on récupère la fenêtre principale (le JFrame)
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            //on ferme la fenêtre du jeu
            if (parentFrame != null) parentFrame.dispose();
            
            //on rentourne au menu principal pour recommencer
            MenuPrincipal.lancerNouvellePartie();
        } else {
        	//si on dit non, ça ferme complètement le programme
            System.exit(0);
        }
    }

    //affiche une image de victoire en grand (utilisé à la fin d’un niveau)
    public int afficherImageVictoire(int niveau) {
        String path = "/images/" + niveau + ".jpg";
        ImageIcon icon = null;

        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
            	//on charge l'image du niveau terminé(à chaque niveau on a une sorte de map pour afficher ou est ce qu'one st dans le jeu
                Image img = new ImageIcon(url).getImage();
                
                //fixation d'une taille max pour ne pas dépasser l'écran
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                int maxW = (int) (screen.width * 0.6);
                int maxH = (int) (screen.height * 0.75);

                int imgW = img.getWidth(null);
                int imgH = img.getHeight(null);
                
                //on garde le ratio de l'image originale pour ne pas la déformer à l'affichage
                double ratio = (double) imgW / imgH;

                int newW = imgW;
                int newH = imgH;

                //si l'image dépasse en largeur on la réduit
                if (newW > maxW) {
                    newW = maxW;
                    newH = (int) (newW / ratio);
                }
                //si l'image dépasse en hauteur on la réduit encore
                if (newH > maxH) {
                    newH = maxH;
                    newW = (int) (newH * ratio);
                }

                //on met l'image redimensionnée dans un ImageIcon
                Image resized = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                icon = new ImageIcon(resized);
                
            } else {
            	//on affiche un messgae d'eeure si l'image n'existe pas
                System.err.println("❌ Image introuvable : " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //les boutons affichés sous l’image de victoire à la fin ed la partie
        Object[] options = (niveau == 10)
                ? new Object[]{"🏁 Fin du jeu", "❌ Quitter"}
                : new Object[]{"▶ Continuer", "❌ Quitter"};
        //affiche l'image dans une fenêtre avec les boutons à la fin de la partie
        return JOptionPane.showOptionDialog(
                null,
                "",
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
        
      //on utilise Graphics2D pour avoir plus d’options de dessin
        Graphics2D g2 = (Graphics2D) g;

        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[0].length; j++) {
            	
            	//on dessine toujours le sol en premier
                g2.drawImage(tileFloor,
                        j * TAILLE_CASE,
                        i * TAILLE_CASE,
                        null);
                
                //si la case est un mur on dessine la tile du mur par dessus
                if (grille[i][j] == '#') {
                    g2.drawImage(tileWall,
                            j * TAILLE_CASE,
                            i * TAILLE_CASE,
                            null);
                }
            }
        }

        //on dessine la clé seulement si elle n'a pas été ramassee
        if (!cle.estRamassee()) {
            g2.drawImage(keyImg, cle.getPos().y * TAILLE_CASE,
                    cle.getPos().x * TAILLE_CASE, null);
        }

        //on dessine la porte à sa position
        if (door != null) {
            g2.drawImage(doorImg, door.getPos().y * TAILLE_CASE,
                    door.getPos().x * TAILLE_CASE, null);
        }

        //le trésor est affiché au dernier niveau du jeu seulement, à sa position
        if (tresor != null) {
            g2.drawImage(treasureImg, tresor.getPos().y * TAILLE_CASE,
                    tresor.getPos().x * TAILLE_CASE, null);
        }

        //on dessine que les coeurs non ramassés sur la labyrinthe
        for (Heart coeur : coeurs) {
            if (!coeur.estRamassee()) {
                g2.drawImage(heartImg, coeur.getPos().y * TAILLE_CASE,
                        coeur.getPos().x * TAILLE_CASE, null);
            }
        }

        //on choisit l’image de l'arme selon son type
        for (Weapon w : armes) {
            if (!w.estRamassee()) {
                Image img = (w.getType() == WeaponType.EPEE) ? swordImg : bowImg;
                g2.drawImage(img, w.getPos().y * TAILLE_CASE,
                        w.getPos().x * TAILLE_CASE, null);
            }
        }

        //dessin des monstres violets
        for (Monstre m : monstres) {
            Position pos = m.getPos();
            g2.drawImage(monsterImg, pos.y * TAILLE_CASE,
                    pos.x * TAILLE_CASE, null);
        }

        //le fantôme peut traverser tout donc on le dessine toujours
        g2.drawImage(ghostImg, fantome.getPos().y * TAILLE_CASE,
                fantome.getPos().x * TAILLE_CASE, null);

        //on dessine le zombie s'il n'est pas tué par le hero
        if (zombieVivant) {
            g2.drawImage(zombieImg, zombie.getPos().y * TAILLE_CASE,
                    zombie.getPos().x * TAILLE_CASE, null);
        }

        //on choisit le sprite selon si le héros regarde à gauche ou à droite
        Image heroImgToDraw =
                "left".equals(lastHorizontalDirection) ? heroLeftImg : heroRightImg;

        g2.drawImage(heroImgToDraw, hero.getY() * TAILLE_CASE,
                hero.getX() * TAILLE_CASE, null);

        long now = System.currentTimeMillis();

        //effect visuel de la trajectoire de la flèche lors de son tir
        if (!arrowTrail.isEmpty() && now - arrowTrailTime < DUREE_TRAJECTOIRE_FLECHE) {
            g2.setColor(new Color(255, 255, 0, 200));
            for (Position p : arrowTrail) {
                int px = p.y * TAILLE_CASE + TAILLE_CASE / 4;
                int py = p.x * TAILLE_CASE + TAILLE_CASE / 4;
                int size = TAILLE_CASE / 2;
                g2.fillRect(px, py, size, size);
            }
        } else if (!arrowTrail.isEmpty() && now - arrowTrailTime >= DUREE_TRAJECTOIRE_FLECHE) {
        	//au bout de quelques ms on efface la trajectoire (après DUREE_TRAJECTOIRE_FLECHE)
            arrowTrail.clear();
        }

        //effet rouge autour du héros pendant l'attaque
        if (animationAttaque && now - timeAttaque < DUREE_ATTAQUE) {
            int hx = hero.getY() * TAILLE_CASE;
            int hy = hero.getX() * TAILLE_CASE;

            g2.setColor(new Color(255, 0, 0, 120));
            g2.fillOval(hx - 5, hy - 5, TAILLE_CASE + 10, TAILLE_CASE + 10);
        } else if (animationAttaque && now - timeAttaque >= DUREE_ATTAQUE) {
        	//on coupe l'effet quand le temps est écoulé
            animationAttaque = false;
        }

        // le HUD en bas
        //udY = position verticale où on commence à dessiner la barre d'informations en bas du jeu
        int hudY = grille.length * TAILLE_CASE + 30;
        
        //on affiche le score du joueur à gauche du hud
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Score : " + hero.getScore(), 10, hudY);

        //affichage des coeurs (point de vies héro restantes)
        int maxHearts = 5; //maximum 5 coeurs
        int pv = hero.getPointsDeVie(); //on récupere combien de coeurs il a à un moment
        int heartSize = 23; //taille du coeurs affiché dans le hud
        int startX = 130; //position horizontale où les coeurs commencent pour etre suffisemment espacé des autres elemtns du hud
        int y = hudY - 18;   //position veticale des coeurs

        g2.drawString("Vie :", 90, hudY);//teste vie à coté des coeurs

        for (int i = 0; i < maxHearts; i++) {
            Image img = (i < pv) ? heartFullImg : heartEmptyImg; //si i < nombre de vies alors coeur plein, sinon coeur vide
            
            //on dessine chaque coeur l'un après l'autre avec un petit espace entre eux
            g2.drawImage(img, startX + i * (heartSize + 4), y, heartSize, heartSize, null);
        }

        //affichage des armes et usages restants (on les decale à droite pour éviter chevauchement avec les autres elements du hub)
        //on construit une chaîne de texte qui dit combien d'utilisations il reste pour chaque arme
        String armeStr = "Épée: " + hero.getUsagesEpeeRestants()
                + " | Arc: " + hero.getUsagesArcRestants();
        
        //on place l'info des armes plus à droite pour que ça ne touche pas les coeurs
        g2.drawString("Armes : " + armeStr, 330, hudY);
        
        //si le héros a une clé → on affiche une clé, sinon on affiche un croix
        g2.drawString("Clé : " + (hero.hasKey() ? "🔑" : "❌"), 620, hudY);
        
       //on affiche combien de coeurs on peut ramasser restent dans cette partie sur la carte
        g2.drawString("Cœurs: " + getCoeursRestants() + "/" + coeurs.size(), 700, hudY);

        //message HUD
        if (messageHUD.length() > 0 &&
                (System.currentTimeMillis() - messageHUDTime) < DUREE_MESSAGE_HUD) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(10, hudY + 20, getWidth() - 20, 25);
            //on dessine un rectangle noir transparent derrière le message
            
            //on affiche le message temporaire (ramasser clé, blesser etc)
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(messageHUD.toString(), 20, hudY + 38);
        }

        dessinerCarteGlobale(g, hudY + 60);
        //on dessine la mini map des niveaux juste en dessous du hud
    }

    private void dessinerCarteGlobale(Graphics g, int mapY) {
        Level[] allLevels = Level.values();
        //currentLevelIndex coorespond au le numéro du niveau actuel 
        int currentLevelIndex = Arrays.asList(allLevels).indexOf(currentLevel);
        int circleSize = 18;
        int mapX = 10;

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Progression :", mapX, mapY - 5);
        
      //boucle qui dessine un cercle pour chaque niveau 
        for (int i = 0; i < allLevels.length; i++) {
            if (i < currentLevelIndex) {
            	//gris pour niveau terminé
                g.setColor(Color.LIGHT_GRAY);
            } else if (i == currentLevelIndex) {
            	//rouge niveau courant
                g.setColor(Color.RED);
            } else {
            	//on dessine un cercle gris foncé pour les niveaux pas encore atteints
                g.setColor(Color.DARK_GRAY);
            }

            //on dessine un cercle qui respresnte le niveau i
            g.fillOval(mapX + i * (circleSize + 10) + 70,
                    mapY, circleSize, circleSize);
              //contour noir du cercle
            g.setColor(Color.BLACK);
            
            g.drawOval(mapX + i * (circleSize + 10) + 70,
                    mapY, circleSize, circleSize);
            
             //si on est sur le dernier niveau alors il faut dessiner un petit carré doré  qui represente le trésor
            if (i == allLevels.length - 1) {
                g.setColor(new Color(255, 215, 0));
                g.fillRect(mapX + i * (circleSize + 10) + 70 + 5,
                        mapY + 5, circleSize - 10, circleSize - 10);
            }
            //ecrire le niveau en blanc dans le cercle allant de 1 à 10
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(i + 1),
                    mapX + i * (circleSize + 10) + 70 + 4,
                    mapY + 14);
        }
    }

    //renvoie le héros actuel du jeu
    public Heros getHero() { return hero; }
    //renvoie la liste de tous les monstres qui sont presents dans la labyrinthe
    public ArrayList<Monstre> getMonstres() { return monstres; }
    //nous dit si le zombie est encore en vie (true) ou pas
    public boolean isZombieVivant() { return zombieVivant; }
    //indique si la partie est finie, ça permet d'empêcher le héros de bouger ou d'agir
    public boolean isPartieTerminee() { return partieTerminee; }
    //donne la liste des coeurs posés dans la map 
    public ArrayList<Heart> getCoeurs() { return coeurs; }
    //renvoie la clé du niveau (comme ça vérifier si elle a été ramassée)
    public Cle getCle() { return cle; }
    //renvoie la porte du niveau pour savoir où elle est
    public Door getDoor() { return door; }
    //renvoie le trésor (uniquement pour le niveau 10)
    public Tresor getTresor() { return tresor; }
}

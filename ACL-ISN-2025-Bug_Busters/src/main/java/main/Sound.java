package main;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {

    //variable pour garder la derniere musique jouée (pratique pour stop/resume)
    private static Clip lastClip;

    //lecture d’un son une seule fois 
    public static void play(String path) {
        try {
            //on récupère le fichier dans resources
            URL url = Sound.class.getResource(path);
            //si le fichier existe pas on affiche une erreur
            if (url == null) {
                System.err.println("❌ [SOUND ERROR] ressource introuvable : " + path);
                return;
            }
            //on charge le son dans un audioinputstream
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            //on crée un clip pour lire le son
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            //on joue le son une fois
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            //si on utilise un mauvais format audio
            System.err.println("❌ format audio pas supporté : " + path);
            System.err.println("   faut un wav pcm 44100hz 16bit");
        } catch (Exception e) {
            //erreur quelconque
            System.err.println("❌ impossible de lire : " + path);
            e.printStackTrace();
        }
    }
    //jouer une musique en boucle 
    public static void loop(String path) {
        try {
            //on récupère le lien du fichier
            URL url = Sound.class.getResource(path);
            if (url == null) {
                System.err.println("❌ [SOUND ERROR] ressource introuvable : " + path);
                return;
            }
            //on charge la musique
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            //lecture infinie
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            //on retient cette musique pour la stopper plus tard
            lastClip = clip;

        } catch (Exception e) {
            System.err.println("❌ impossible de lire la musique en boucle : " + path);
            e.printStackTrace();
        }
    }

    //stopper la musique actuelle
    public static void stopMusic() {
        try {
            //si une musique existe et elle joue encore alors on stop
            if (lastClip != null && lastClip.isRunning()) {
                lastClip.stop();
            }
        } catch (Exception e) {
            System.err.println("❌ impossible d’arrêter la musique");
            e.printStackTrace();
        }
    }

    //reprendre la musique stoppée précédemment
    public static void resumeMusic() {
        try {
            //si ya une musique et elle est à l’arrêt alors on la relance
            if (lastClip != null && !lastClip.isRunning()) {
                lastClip.loop(Clip.LOOP_CONTINUOUSLY);
                lastClip.start();
            }
        } catch (Exception e) {
            System.err.println("❌ impossible de reprendre la musique");
            e.printStackTrace();
        }
    }

    //test pour voir si un fichier son existe dans resources
    public static void test(String path) {
        System.out.println("🔎 test du son : " + path);
        URL u = Sound.class.getResource(path);

        if (u == null) {
            System.err.println("❌ le son est introuvable : " + path);
        } else {
            System.out.println("✔ trouvé : " + u);
        }
    }
}

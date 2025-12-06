package main;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {

    // >>>>>>>>>> IMPORTANT : ceci manquait <<<<<<<<<<
    private static Clip lastClip;

    // Lecture "one-shot"
    public static void play(String path) {
        try {
            URL url = Sound.class.getResource(path);

            if (url == null) {
                System.err.println("❌ [SOUND ERROR] Ressource introuvable : " + path);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

        } catch (UnsupportedAudioFileException e) {
            System.err.println("❌ Format audio NON supporté : " + path);
            System.err.println("   Utilise : WAV PCM 44100Hz 16bit");
        } catch (Exception e) {
            System.err.println("❌ Impossible de lire : " + path);
            e.printStackTrace();
        }
    }

    // >>>>>>>>>> MUSIQUE EN BOUCLE (loop) <<<<<<<<<<
    public static void loop(String path) {
        try {
            URL url = Sound.class.getResource(path);
            if (url == null) {
                System.err.println("❌ [SOUND ERROR] Ressource introuvable : " + path);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            lastClip = clip; // mémorise la musique en cours

        } catch (Exception e) {
            System.err.println("❌ Impossible de lire la musique en boucle : " + path);
            e.printStackTrace();
        }
    }

    // >>>>>>>>>> STOP MUSIQUE <<<<<<<<<<
    public static void stopMusic() {
        try {
            if (lastClip != null && lastClip.isRunning()) {
                lastClip.stop();
            }
        } catch (Exception e) {
            System.err.println("❌ Impossible d’arrêter la musique");
            e.printStackTrace();
        }
    }

    // >>>>>>>>>> REPRENDRE MUSIQUE <<<<<<<<<<
    public static void resumeMusic() {
        try {
            if (lastClip != null && !lastClip.isRunning()) {
                lastClip.loop(Clip.LOOP_CONTINUOUSLY);
                lastClip.start();
            }
        } catch (Exception e) {
            System.err.println("❌ Impossible de reprendre la musique");
            e.printStackTrace();
        }
    }

    // Test rapide
    public static void test(String path) {
        System.out.println("🔎 Test du son : " + path);
        URL u = Sound.class.getResource(path);
        if (u == null) {
            System.err.println("❌ Le son n’est pas trouvé : " + path);
        } else {
            System.out.println("✔ Trouvé : " + u);
        }
    }
}

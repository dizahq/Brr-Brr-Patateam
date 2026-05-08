package Codes;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundManager {
    private static SoundManager instance;
    private Clip backgroundMusic;
    private String currentTrack = "";

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playMusic(String filePath) {
        // Prevent music restart if same track is alr playing
        if (currentTrack.equals(filePath)) return;

        stopMusic();
        try {
            File musicPath = new File(filePath);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioInput);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusic.start();
                currentTrack = filePath;
            }
        } catch (Exception e) {
            System.err.println("[SoundManager] Error loading music: " + e.getMessage());
        }
    }

    public void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
            currentTrack = "";
        }
    }
}

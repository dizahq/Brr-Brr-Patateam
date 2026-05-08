package Codes;

import java.io.File;
import java.lang.classfile.instruction.ConstantInstruction.ArgumentConstantInstruction;

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

    public void playSFX (String filePath){
        try{
            File sfxPath = new File(filePath);
            if (sfxPath.exists()){
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(sfxPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();

                clip.addLineListener( e->{
                    if (e.getType() == javax.sound.sampled.LineEvent.Type.STOP){
                        clip.close();
                    }
                });
            }
        }catch(Exception e){
             System.err.println("[SoundManager] Error playing SFX: " + e.getMessage());
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

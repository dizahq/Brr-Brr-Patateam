package Codes;

import java.io.File;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundManager {
    private static SoundManager instance;
    private Clip backgroundMusic;
    private String currentTrack = "";
    private float sfxVolume = 0.75f;   // 0.0 = mute, 1.0 = max
    private float musicVolume = 0.75f;

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
                setVolume(backgroundMusic, musicVolume);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
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
                setVolume(clip, sfxVolume);
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

    private void setVolume(Clip clip, float volume) {
        if (clip == null) return;
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        // Convert 0.0-1.0 range to decibels
        float dB = (float) (Math.log10(Math.max(volume, 0.0001f)) * 20f);
        dB = Math.max(control.getMinimum(), Math.min(control.getMaximum(), dB));
        control.setValue(dB);
    }

    public void setSFXVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
    }

    public void setMusicVolume(float volume) {
        System.out.println("[SoundManager] setMusicVolume(" + volume + ")");
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        setVolume(backgroundMusic, this.musicVolume); // apply immediately to playing music
    }

    public float getSFXVolume() { return sfxVolume; }
    public float getMusicVolume() { return musicVolume; }
}

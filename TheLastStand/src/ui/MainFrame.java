package ui;

import javax.swing.JFrame;
import java.awt.Toolkit;

import fileio.SaveManager;
import sound.SoundManager;

public class MainFrame extends JFrame{
    // Dynamically fetch screen resolution to ensure the game is truly full screen on any monitor
    private int frameWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private int frameHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    
    // Central container that holds all game layers (Menu, Game, Overlays)
    MainLayeredPane mainLayeredPane;
    
    public MainFrame(){
        // System initialization: ensures the save file is in clean state at launch
        SaveManager.resetFile();
        
        // Window configuration
        setBounds(0, 0, frameWidth, frameHeight); // Match screen size
        setUndecorated(true);             // Removes title bar/ borders for full screen feel
        setLocationRelativeTo(null);
        
        // Layer management
        mainLayeredPane = new MainLayeredPane(frameWidth, frameHeight); // Pass dimensions so the pane know how to scale its children
        add(mainLayeredPane);

        // Audio System Initialization
        // Singleton pattern: get single instance of SoundManager and start backgrond music
        SoundManager.getInstance().playMusic("TheLastStand/assets/music/MainMenu_music.wav");
        SoundManager.getInstance().setMusicVolume(.50f); // max volume
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Tests:
        System.out.println("Your screen size is " + frameWidth + "x" + frameHeight);
    }
}

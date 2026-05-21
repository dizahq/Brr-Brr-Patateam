package ui;

import java.awt.CardLayout;
import java.util.function.Consumer;
import javax.swing.JPanel;
import sound.SoundManager;

public class MainPanel extends JPanel{
    private CardLayout cards = new CardLayout();
    private Consumer<String> switchPanel;
    
    private Game game;
    private MainMenuPanel mainMenu;
    
    public MainPanel(int panelWidth, int panelHeight, MainLayeredPane rootLayeredPane){
        switchPanel = this::switchPanel;

        setBounds(0, 0, panelWidth, panelHeight);
        setLayout(cards);
        
        game = new Game(panelWidth, panelHeight, rootLayeredPane);
        mainMenu = new MainMenuPanel(rootLayeredPane, switchPanel, game, panelWidth, panelHeight);

        add(mainMenu, "mainMenu");
        add(game, "game");

        switchPanel("mainMenu");
    }
    public void switchPanel(String name){
        cards.show(this, name);

        if (name.equals("game")) {
            SoundManager.getInstance().setMusicVolume(0.30f); // max volume
            SoundManager.getInstance().setSFXVolume(1.0f); // game SFX louder
            SoundManager.getInstance().playMusic("/assets/music/Game_music.wav");
        } else if (name.equals("mainMenu")) {
            SoundManager.getInstance().setMusicVolume(.40f); // max volume
            SoundManager.getInstance().playMusic("/assets/music/MainMenu_music.wav");
        }
    }

    public Consumer<String> getSwitchPanel() {
        return switchPanel;
    }
    public Game getGame() {
        return game;
    }
}


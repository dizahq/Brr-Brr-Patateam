package Codes;

import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameOverPanel extends OverlayPanel {
    private JPanel container;
    private JLabel title = new JLabel("Game Over!");
    private JButton respawnBtn = new JButton("Respawn");
    private JButton mainMenuBtn = new JButton("Main Menu");

    private Game game;
    private Consumer<String> switchPanel;

    public GameOverPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, Game game) {
        super(panelWidth, panelHeight);
        this.switchPanel = switchPanel;
        this.game = game;

        container = getContainerPanel();

        title.setFont(new Font("Arial", Font.BOLD, 35));

        respawnBtn.setPreferredSize(new Dimension(200, 50));
        mainMenuBtn.setPreferredSize(new Dimension(200, 50));

        respawnBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
            SoundManager.getInstance().playMusic("Music/Game_music.wav");
            respawn();
        });

        mainMenuBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
            goToMainMenu();
        });

        container.add(title);
        container.add(respawnBtn);
        container.add(mainMenuBtn);
    }

    public void respawn() {
        setVisible(false);          // 1. hide game over screen first
        game.setLives(4);           // 2. respawns + reset lives again to max
        game.initializeWave(game.getCurrentLevel(), null); // 3. restart current level/wave. Create new player instance
        game.startGameThread();     // 4. start loop last, panel is visible and focused now
    
        System.out.println("[GameOverPanel] Respawning.");
    }

    private void goToMainMenu() {
        SaveManager.deleteSave();
        setVisible(false);
        game.stopGameThread();
        switchPanel.accept("mainMenu");

        // test
        System.out.println("[GameOverPanel] Returning to main menu.");
    }
}
package Codes;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GameOverPanel extends OverlayPanel {
    private JPanel container;

    private Image background = new ImageIcon("Entities/UserInterface/gameOver/game_over.png").getImage();
    private GameButton respawnBtn = new GameButton("gameOver/respawnButton.png", "gameOver/respawnButton_pressed.png", null);
    private GameButton mainMenuBtn = new GameButton("mainmenuButton.png", "mainmenuButton_pressed.png", null);

    private Game game;
    private Consumer<String> switchPanel;

    public GameOverPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, Game game) {
        super(panelWidth, panelHeight, true);
        this.switchPanel = switchPanel;
        this.game = game;

        container = getContainerPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        respawnBtn.setAlignmentX(CENTER_ALIGNMENT);
        mainMenuBtn.setAlignmentX(CENTER_ALIGNMENT);

        respawnBtn.setButtonSize(300, 100);
        mainMenuBtn.setButtonSize(300, 100);

        respawnBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
            SoundManager.getInstance().playMusic("Music/Game_music.wav");
            playerRespawn();
        });

        mainMenuBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
            goToMainMenu();
        });

        container.add(Box.createVerticalGlue());
        container.add(Box.createRigidArea(new Dimension(0, 300)));
        container.add(respawnBtn);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(mainMenuBtn);
        container.add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            super.paintComponent(g);
        }
    }

    public void playerRespawn() {
        setVisible(false);          // 1. hide game over screen first
        game.setLives(4);           // 2. respawns + reset lives again to max
        game.playerRespawn(); // 3. restart current level/wave. Create new player instance
        game.startGameThread();     // 4. start loop last, panel is visible and focused now
    
        System.out.println("[GameOverPanel] Player respawned.");
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
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

        respawnBtn.addActionListener(e -> respawn());

        mainMenuBtn.addActionListener(e -> goToMainMenu());

        container.add(title);
        container.add(respawnBtn);
        container.add(mainMenuBtn);
    }

    public void respawn() {
        SaveManager.deleteSave();
        setVisible(false);          // 1. hide game over screen first
        // game.resetGame();           // 2. reset state + clear keys
        switchPanel.accept("game"); // 3. switch to game panel (makes it visible)
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
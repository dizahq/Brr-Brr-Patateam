package Codes;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class WinPanel extends OverlayPanel {
    private JPanel container; // Inner container panel used to organize and position UI components
    private Image background;
    private GameButton mainMenuBtn;
    private GameButton exitBtn;
    private Game game;
    private Consumer<String> switchPanel;

    public WinPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, Game game) {
        super(panelWidth, panelHeight, true);
        this.switchPanel = switchPanel;
        this.game = game;

        // Initialize background image 
        background = new ImageIcon("Entities/UserInterface/gameWon/game_won.png").getImage(); // Full screen background image rendered behind all UI components
        // Initialize buttons 
        mainMenuBtn = new GameButton("mainmenuButton.png", "mainmenuButton_pressed.png", null);
        exitBtn = new GameButton("MainMenu/exitButton.png", "MainMenu/exitButton_pressed.png", null);

        setupLayout();
        setupActionListeners();
    }

    private void setupLayout() {
        container = getContainerPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        
        mainMenuBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);

        mainMenuBtn.setButtonSize(300, 100);
        exitBtn.setButtonSize(300, 80);

        // Using vertical glue and rigid areas for precise spacing
        container.add(Box.createVerticalGlue());
        container.add(Box.createRigidArea(new Dimension(0, 500))); // Vertical offset
        container.add(mainMenuBtn);
        container.add(Box.createRigidArea(new Dimension(0, 20))); // Gap between buttons
        container.add(exitBtn);
        container.add(Box.createVerticalGlue());
    }

    private void setupActionListeners() {
        mainMenuBtn.addActionListener(e -> {
            SaveManager.deleteSave(); // Clear progress upon completion
            game.stopGameThread(); // Safely terminate the game loop
            switchPanel.accept("mainMenu");
            setVisible(false);
        });

        exitBtn.addActionListener(e -> System.exit(0));
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Draw base overlay
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } 
    }
}
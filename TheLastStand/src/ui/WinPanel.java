package src.ui;

import src.fileio.SaveManager;

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
    
    private Consumer<String> switchPanel;
    private Game game;

    private Image background;
    private GameButton mainMenuBtn;
    private GameButton exitBtn;

    public WinPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, Game game) {
        super(panelWidth, panelHeight, true);
        this.switchPanel = switchPanel;
        this.game = game;

        background = new ImageIcon("TheLastStand/assets/interface/gameWon/game_won.png").getImage(); 
        mainMenuBtn = new GameButton("mainmenuButton.png", "mainmenuButton_pressed.png", null);
        exitBtn = new GameButton("mainMenu/exitButton.png", "mainMenu/exitButton_pressed.png", null);

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

        container.add(Box.createVerticalGlue());
        container.add(Box.createRigidArea(new Dimension(0, 500)));
        container.add(mainMenuBtn);
        container.add(Box.createRigidArea(new Dimension(0, 20))); 
        container.add(exitBtn);
        container.add(Box.createVerticalGlue());
    }

    private void setupActionListeners() {
        mainMenuBtn.addActionListener(e -> {
            SaveManager.deleteSave(); 
            game.stopGameThread(); 
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
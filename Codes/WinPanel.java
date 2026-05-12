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
    private JPanel container;

    private Image background = new ImageIcon("Entities/UserInterface/gameWon/game_won.png").getImage();
    private GameButton mainMenuBtn = new GameButton("mainmenuButton.png", "mainmenuButton_pressed.png", null);
    private GameButton exitBtn = new GameButton("MainMenu/newgameButton.png", "MainMenu/newgameButton_pressed.png", null);

    private Game game;
    private Consumer<String> switchPanel;

    public WinPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, Game game) {
        super(panelWidth, panelHeight, true);
        this.switchPanel = switchPanel;
        this.game = game;

        container = getContainerPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        
        mainMenuBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);

        mainMenuBtn.setButtonSize(300, 100);
        exitBtn.setButtonSize(300, 100);

        mainMenuBtn.addActionListener(e -> {
            SaveManager.deleteSave();
            setVisible(true);
            game.stopGameThread();
            switchPanel.accept("mainMenu");
        });

        exitBtn.addActionListener(e -> {
            System.exit(0);
        });

        container.add(Box.createVerticalGlue());
        container.add(Box.createRigidArea(new Dimension(0, 500)));
        container.add(mainMenuBtn);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(exitBtn);
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
}
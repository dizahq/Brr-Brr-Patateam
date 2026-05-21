package ui;

import fileio.SaveData;
import fileio.SaveManager;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import sound.SoundManager;

public class MainMenuPanel extends JPanel {
    private MainLayeredPane rootLayeredPane;
    private Consumer<String> switchPanel;
    private Game game;

    private Image backgroundImage;

    private GameButton newGameBtn = new GameButton("mainMenu/newgameButton.png", "mainMenu/newgameButton_pressed.png", null);
    private GameButton continueBtn = new GameButton("mainMenu/continueButton.png", "mainMenu/continueButton_pressed.png", "mainMenu/continueButton_locked.png");
    private GameButton exitBtn = new GameButton("mainMenu/exitButton.png", "mainMenu/exitButton_pressed.png", null);
    private GameButton helpBtn = new GameButton("mainMenu/helpButton.png", "mainMenu/helpButton_hover.png", null);
    private GameButton creditsBtn = new GameButton("mainMenu/creditsButton.png", "mainMenu/creditsButton_hover.png", null);

    public MainMenuPanel(MainLayeredPane rootLayeredPane, Consumer<String> switchPanel, Game game, int panelWidth, int panelHeight) {
        this.rootLayeredPane = rootLayeredPane;
        this.switchPanel = switchPanel;
        this.game = game;

        // Button sizes
        int btnWidth = (int)(panelWidth * 0.22);
        int btnHeight = (int)(panelHeight * 0.11);
        newGameBtn.setButtonSize(btnWidth, btnHeight);
        continueBtn.setButtonSize(btnWidth, btnHeight);
        exitBtn.setButtonSize(btnWidth, btnHeight);
        helpBtn.setButtonSize(100, 100);
        creditsBtn.setButtonSize(100, 100);

        // Center panel with GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Top glue — increase weighty to push buttons further down
        gbc.gridy = 0;
        gbc.weighty = 4.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(Box.createVerticalStrut(1), gbc);

        // Buttons
        gbc.weighty = 0;
        gbc.insets = new Insets(8, 0, 8, 0);

        gbc.gridy = 1;
        centerPanel.add(newGameBtn, gbc);

        gbc.gridy = 2;
        centerPanel.add(continueBtn, gbc);

        gbc.gridy = 3;
        centerPanel.add(exitBtn, gbc);

        // Bottom glue — increase weighty to push buttons further up
        gbc.gridy = 4;
        gbc.weighty = 0.01;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(Box.createVerticalStrut(1), gbc);

        // Action listeners
        newGameBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("/assets/music/click.wav");
            newGame();
        });
        continueBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("/assets/music/click.wav");
            continueGame();
        });
        exitBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("/assets/music/click.wav");
            exitGame();
        });
        helpBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("/assets/music/click.wav");
            help();
        });
        creditsBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("/assets/music/click.wav");
            credits();
        });

        // Bottom right buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 25, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(helpBtn);
        buttonPanel.add(creditsBtn);

        // Main layout
        setLayout(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        backgroundImage = new ImageIcon(getClass().getResource("/assets/background/mainmenu.png")).getImage();
        refreshButtons();

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                refreshButtons();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void refreshButtons() {
        continueBtn.setEnabled(SaveManager.hasSave());
    }

    public void newGame() {
        SaveManager.deleteSave();
        game.resetGame();
        switchPanel.accept("game");
        game.startGameThread();
        System.out.println("[MainMenuPanel] New Game.");
    }

    public void continueGame() {
        SaveData data = SaveManager.load();
        if (data != null) {
            game.restoreFromSave(data);
            System.out.println("[MainMenuPanel] Continuing from: " + data);
        } else {
            System.err.println("[MainMenuPanel] No valid save found, starting new.");
            game.setCurrentLevel(0);
            game.resetGame();
        }
        switchPanel.accept("game");
        game.startGameThread();
    }

    public void exitGame() { rootLayeredPane.getExitConfirm().setVisible(true); }
    public void help() { rootLayeredPane.getHelp().setVisible(true); }
    public void credits() { rootLayeredPane.getCredits().setVisible(true); }
}
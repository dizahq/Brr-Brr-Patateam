package ui;

// Main menu screen with New Game, Continue, and Exit buttons

import fileio.SaveManager;
import fileio.SaveData;
import sound.SoundManager;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class MainMenuPanel extends JPanel{
    private MainLayeredPane rootLayeredPane;
    private Consumer<String> switchPanel;
    private Game game;

    private Image backgroundImage;
    private GameButton newGameBtn;
    private GameButton continueBtn;
    private GameButton exitBtn;
    
    public MainMenuPanel(MainLayeredPane rootLayeredPane, Consumer<String> switchPanel, Game game){
        this.rootLayeredPane = rootLayeredPane;
        this.switchPanel = switchPanel;
        this.game = game;

        backgroundImage = new ImageIcon("TheLastStand/assets/background/mainmenu.png").getImage();
        newGameBtn = new GameButton("mainMenu/newgameButton.png", "mainMenu/newgameButton_pressed.png", null);
        continueBtn = new GameButton("mainMenu/continueButton.png", "mainMenu/continueButton_pressed.png", "mainMenu/continueButton_locked.png");
        exitBtn = new GameButton("mainMenu/exitButton.png", "mainMenu/exitButton_pressed.png", null); 
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        newGameBtn.setAlignmentX(CENTER_ALIGNMENT);
        continueBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);

        newGameBtn.setButtonSize(300, 100);
        continueBtn.setButtonSize(300, 100);
        exitBtn.setButtonSize(300, 100);

        newGameBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("TheLastStand/assets/music/click.wav");
            newGame();
        });
        continueBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("TheLastStand/assets/music/click.wav");
            continueGame();
        });
        exitBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("TheLastStand/assets/music/click.wav");
            exitGame();
        });

        add(Box.createVerticalGlue());
        add(Box.createRigidArea(new Dimension(0, 380)));
        add(newGameBtn);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(continueBtn);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(exitBtn);
        add(Box.createVerticalGlue());

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
            // Stretch image to fill panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void refreshButtons() {
        continueBtn.setEnabled(SaveManager.hasSave());
    }

    public void newGame(){
        SaveManager.deleteSave();
        game.resetGame();
        switchPanel.accept("game");
        game.startGameThread();
        System.out.println("[MainMenuPanel] New Game.");
    }

    public void continueGame(){
        SaveData data = SaveManager.load();
        if (data != null) {
            // Restore game state from the save file
            game.restoreFromSave(data);
            System.out.println("[MainMenuPanel] Continuing from: " + data);
        } else {
            // Save file wass corrupt/missing
            System.err.println("[MainMenuPanel] No valid save found, starting new game.");
            game.setCurrentLevel(0);
            game.resetGame();
        }
        switchPanel.accept("game");
        game.startGameThread();
    }
    public void exitGame(){
        rootLayeredPane.getExitConfirm().setVisible(true);
    }
}

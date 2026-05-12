package Codes;

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

    //new
    private Image backgroundImage;

    private GameButton newGameBtn = new GameButton("MainMenu/newgameButton.png", "MainMenu/newgameButton_pressed.png", null);
    private GameButton continueBtn = new GameButton("MainMenu/continueButton.png", "MainMenu/continueButton_pressed.png", "MainMenu/continueButton_locked.png");
    private GameButton exitBtn = new GameButton("MainMenu/exitButton.png", "MainMenu/exitButton_pressed.png", null);  
    
    public MainMenuPanel(MainLayeredPane rootLayeredPane, Consumer<String> switchPanel, Game game){
        this.rootLayeredPane = rootLayeredPane;
        this.switchPanel = switchPanel;
        this.game = game;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        newGameBtn.setAlignmentX(CENTER_ALIGNMENT);
        continueBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);

        // newGameBtn.setPreferredSize(new Dimension(300, 100));
        newGameBtn.setButtonSize(300, 100);
        continueBtn.setButtonSize(300, 100);
        exitBtn.setButtonSize(300, 100);

        newGameBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
            newGame();
        });
        continueBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
            continueGame();
        });
        exitBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
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

        //bg not final
        backgroundImage = new ImageIcon("Entities/Background/mainmenu.png").getImage();
        refreshButtons();

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                refreshButtons();
            }
        });
    }

    //new
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

        // test
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
            System.err.println("[MainMenuPanel] No valid save found, startingnew.");
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

package ui;

import fileio.SaveData;
import fileio.SaveManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import sound.SoundManager;

public class MainMenuPanel extends JPanel{
    private MainLayeredPane rootLayeredPane;
    private Consumer<String> switchPanel;
    private Game game;

    //new
    private Image backgroundImage;

    private GameButton newGameBtn = new GameButton("mainMenu/newgameButton.png", "mainMenu/newgameButton_pressed.png", null);
    private GameButton continueBtn = new GameButton("mainMenu/continueButton.png", "mainMenu/continueButton_pressed.png", "mainMenu/continueButton_locked.png");
    private GameButton exitBtn = new GameButton("mainMenu/exitButton.png", "mainMenu/exitButton_pressed.png", null);  
    private GameButton helpBtn = new GameButton("mainMenu/helpButton.png", "mainMenu/helpButton_hover.png", null);
    private GameButton creditsBtn = new GameButton("mainMenu/creditsButton.png", "mainMenu/creditsButton_hover.png", null);
    
    public MainMenuPanel(MainLayeredPane rootLayeredPane, Consumer<String> switchPanel, Game game){
        this.rootLayeredPane = rootLayeredPane;
        this.switchPanel = switchPanel;
        this.game = game;

        JPanel centerPanel = new JPanel();

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        newGameBtn.setAlignmentX(CENTER_ALIGNMENT);
        continueBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);

        // newGameBtn.setPreferredSize(new Dimension(300, 100));
        newGameBtn.setButtonSize(300, 100);
        continueBtn.setButtonSize(300, 100);
        exitBtn.setButtonSize(300, 100);

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

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createRigidArea(new Dimension(0, 380)));
        centerPanel.add(newGameBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(continueBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(exitBtn);
        centerPanel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 25, 0));

        helpBtn.setButtonSize(100, 100);
        creditsBtn.setButtonSize(100, 100);

        helpBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("/assets/music/click.wav");
            help();
        });
        creditsBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("/assets/music/click.wav");
            credits();
        });

        buttonPanel.add(helpBtn);
        buttonPanel.add(creditsBtn);

        setLayout(new BorderLayout());
        centerPanel.setOpaque(false);
        buttonPanel.setOpaque(false);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        //bg not final
        backgroundImage = new ImageIcon(getClass().getResource("/assets/background/mainmenu.png")).getImage();
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
    public void help(){
        rootLayeredPane.getHelp().setVisible(true);
    }
    public void credits(){
        rootLayeredPane.getCredits().setVisible(true);
    }
}

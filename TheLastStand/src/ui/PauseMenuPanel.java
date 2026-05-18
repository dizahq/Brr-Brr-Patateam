package src.ui;

import src.sound.SoundManager;
import src.fileio.SaveData;
import src.fileio.SaveManager;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

public class PauseMenuPanel extends OverlayPanel{
    private Consumer<String> switchPanel;
    private Game game;

    private Image background;
    private GameButton resumeBtn;
    private GameButton backToMainMenuBtn;
    private GameButton exitBtn;
    
    public PauseMenuPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, Game game){
        super(panelWidth, panelHeight, true);
        this.switchPanel = switchPanel;
        this.game = game;

        background = new ImageIcon("TheLastStand/assets/interface/pauseMenu/game_paused.png").getImage();
        resumeBtn = new GameButton("pauseMenu/resumeButton.png", "pauseMenu/resumeButton_pressed.png", null);;
        backToMainMenuBtn = new GameButton("mainmenuButton.png", "mainmenuButton_pressed.png", null);
        exitBtn = new GameButton("mainMenu/exitButton.png", "mainMenu/exitButton_pressed.png", null);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        backToMainMenuBtn.setAlignmentX(CENTER_ALIGNMENT);
        resumeBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);

        backToMainMenuBtn.setButtonSize(250, 85);
        resumeBtn.setButtonSize(250, 85);
        exitBtn.setButtonSize(250, 65);

        resumeBtn.setAlignmentX(CENTER_ALIGNMENT);
        backToMainMenuBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);

        backToMainMenuBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("TheLastStand/assets/music/click.wav");
            backToMainMenu();
        });

        resumeBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("TheLastStand/assets/music/click.wav");
            resume();
        });
        exitBtn.addActionListener(e ->{
            SoundManager.getInstance().playSFX("TheLastStand/assets/music/click.wav");
            exitGame();
        });

        this.removeAll(); // Clears anything prev added to the container and makes sure panel is a blank state
        add(Box.createVerticalGlue());
        add(Box.createRigidArea(new Dimension(0, 280)));
        add(resumeBtn);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(backToMainMenuBtn);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(exitBtn);
        add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void resume(){
        setVisible(false);
        game.resumeGameThread();
    }

    // Saves progress, stops game loop, and returns to main menu
    public void backToMainMenu(){
        saveProgress();
        setVisible(false);
        game.stopGameThread();
        switchPanel.accept("mainMenu");
    }

    public void exitGame() {
        SaveData data = new SaveData(
            game.getCurrentLevel(), 
            game.getCurrentWave(),
            game.getLives(), 
            game.getPlayerX(), 
            game.getPlayerY(),
            game.getSpawnRate()
        );
        SaveManager.save(data);

        game.stopGameThread();
        SoundManager.getInstance().stopMusic();
        System.exit(0);
    }

    private void saveProgress() {
        SaveData data = new SaveData(
            game.getCurrentLevel(), 
            game.getCurrentWave(),
            game.getLives(), 
            game.getPlayerX(), 
            game.getPlayerY(),
            game.getSpawnRate()
        );
        if (!SaveManager.save(data)) {
            System.err.println("[PauseMenuPanel] Warning: progress can't be saved.");
        }
    }
}

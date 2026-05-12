package Codes;

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

    private Image background = new ImageIcon("Entities/UserInterface/pauseMenu/game_paused.png").getImage();
    private GameButton resume = new GameButton("pauseMenu/resumeButton.png", "pauseMenu/resumeButton_pressed.png", null);
    private GameButton backToMainMenu = new GameButton("mainmenuButton.png", "mainmenuButton_pressed.png", null);
    private GameButton exit = new GameButton("MainMenu/exitButton.png", "MainMenu/exitButton_pressed.png", null);
    
    public PauseMenuPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, Game game){
        super(panelWidth, panelHeight, true);
        this.switchPanel = switchPanel;
        this.game = game;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        backToMainMenu.setAlignmentX(CENTER_ALIGNMENT);
        resume.setAlignmentX(CENTER_ALIGNMENT);
        exit.setAlignmentX(CENTER_ALIGNMENT);

        backToMainMenu.setButtonSize(250, 85);
        resume.setButtonSize(250, 85);
        exit.setButtonSize(250, 65);

        resume.setAlignmentX(CENTER_ALIGNMENT);
        backToMainMenu.setAlignmentX(CENTER_ALIGNMENT);
        exit.setAlignmentX(CENTER_ALIGNMENT);

        backToMainMenu.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
            backToMainMenu();
        });

        resume.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
            resume();
        });
        exit.addActionListener(e ->{
            SoundManager.getInstance().playSFX("Music/click.wav");
            exitGame();
        });

        this.removeAll(); // Clears anything prev added to the container and makes sure panel is a blank state
        add(Box.createVerticalGlue());
        add(Box.createRigidArea(new Dimension(0, 280)));
        add(resume);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(backToMainMenu);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(exit);
        add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            // Stretch image to fill panel
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void resume(){
        setVisible(false);
        game.resumeGameThread();
    }

    public void backToMainMenu(){
        // Save game progress
        SaveData data = new SaveData(
            game.getCurrentLevel(), 
            game.getCurrentWave(),
            game.getLives(), 
            game.getPlayerX(), 
            game.getPlayerY(),
            game.getSpawnRate()
        );
        boolean saved = SaveManager.save(data);
        if (!saved) {
            System.err.println("[PauseMenuPanel] Warning: progress can't be saved.");
        }

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
}

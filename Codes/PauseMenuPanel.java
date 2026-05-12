package Codes;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PauseMenuPanel extends OverlayPanel{
    private Consumer<String> switchPanel;
    private Game game;

    private JPanel container;

    private Image background = new ImageIcon("Entities/UserInterface/pauseMenu/game_paused.png").getImage();
    private GameButton backToMainMenu = new GameButton("mainmenuButton.png", "mainmenuButton_pressed.png", null);
    private GameButton resume = new GameButton("pauseMenu/resumeButton.png", "pauseMenu/resumeButton_pressed.png", null);
    private GameButton exit = new GameButton("MainMenu/exitButton.png", "MainMenu/exitButton_pressed.png", null);
    
    public PauseMenuPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, Game game){
        super(panelWidth, panelHeight, false);
        this.switchPanel = switchPanel;
        this.game = game;

        container = getContainerPanel();
        container.setOpaque(false); // false to see image
        this.remove(container); // remove old container and replace 
        container = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                } else {
                    super.paintComponent(g);
                }
            }
        };

        container.setPreferredSize(new Dimension(450, 550));
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        this.add(container);

        backToMainMenu.setAlignmentX(CENTER_ALIGNMENT);
        resume.setAlignmentX(CENTER_ALIGNMENT);
        exit.setAlignmentX(CENTER_ALIGNMENT);

        backToMainMenu.setButtonSize(200, 60);
        resume.setButtonSize(200, 60);
        exit.setButtonSize(200, 50);

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

        container.removeAll();
        container.add(Box.createRigidArea(new Dimension(0, 280)));
        container.add(backToMainMenu);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
        container.add(resume);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(exit);
        container.add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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

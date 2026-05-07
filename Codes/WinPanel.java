package Codes;

import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WinPanel extends OverlayPanel {
    private JPanel container;
    private JLabel title = new JLabel("Congratulations. You won!");
    private JButton mainMenuBtn = new JButton("Main Menu");
    private JButton exitBtn = new JButton("Exit Game");

    private Game game;
    private Consumer<String> switchPanel;

    public WinPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, Game game) {
        super(panelWidth, panelHeight);
        this.switchPanel = switchPanel;
        this.game = game;

        container = getContainerPanel();

        title.setFont(new Font("Arial", Font.BOLD, 35));
        
        mainMenuBtn.setPreferredSize(new Dimension(200, 50));
        exitBtn.setPreferredSize(new Dimension(200, 50));

        mainMenuBtn.addActionListener(e -> {
            SaveManager.deleteSave();
            setVisible(true);
            game.stopGameThread();
            switchPanel.accept("mainMenu");
        });

        exitBtn.addActionListener(e -> {
            System.exit(0);
        });

        container.add(title);
        container.add(mainMenuBtn);
        container.add(exitBtn);
    }
}
package ui;

import sound.SoundManager;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

// Confirmation panel that appears when the player tries to exit the game
public class ExitConfirmPanel extends OverlayPanel{
    private JPanel container;
    
    private Image background = new ImageIcon("TheLastStand/assets/interface/confirmExit/confirm_exit.png").getImage();
    private GameButton confirmExitBtn = new GameButton("confirmExit/yesButton.png", "confirmExit/yesButton_pressed.png", null);
    private GameButton cancelExitBtn = new GameButton("confirmExit/noButton.png", "confirmExit/noButton_pressed.png", null);

    public ExitConfirmPanel(int panelWidth, int panelHeight){
        super(panelWidth, panelHeight, false);
        container = getContainerPanel();

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
        
        confirmExitBtn.setAlignmentX(CENTER_ALIGNMENT);
        cancelExitBtn.setAlignmentX(CENTER_ALIGNMENT);

        confirmExitBtn.setButtonSize(200, 90);
        cancelExitBtn.setButtonSize(200, 90);

        confirmExitBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("TheLastStand/assets/music/click.wav");
            // SaveManager.deleteSave(); // use only if exit game = delete saved data
            System.exit(0);
        });
        cancelExitBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("TheLastStand/assets/music/click.wav");
            setVisible(false);
        });

        container.removeAll();
        container.add(Box.createRigidArea(new Dimension(0, 280)));
        container.add(confirmExitBtn);
        container.add(cancelExitBtn);
        container.add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}

package Codes;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ExitConfirmPanel extends OverlayPanel{
    private JPanel container;
    private JLabel title = new JLabel("Exit Game?");
    private JButton confirmExitBtn = new JButton("Yes");
    private JButton cancelExitBtn = new JButton("No");
    public ExitConfirmPanel(int panelWidth, int panelHeight){
        super(panelWidth, panelHeight);
        container = getContainerPanel();

        title.setFont(new Font("Arial", Font.BOLD, 35));
        confirmExitBtn.setPreferredSize(new Dimension(200, 50));
        cancelExitBtn.setPreferredSize(new Dimension(200, 50));

        confirmExitBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
            // SaveManager.deleteSave(); // use only if exit game = delete saved data
            System.exit(0);
        });
        cancelExitBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
            setVisible(false);
        });

        container.add(title);
        container.add(confirmExitBtn);
        container.add(cancelExitBtn);
    }
}

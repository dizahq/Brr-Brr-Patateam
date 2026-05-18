package ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import sound.SoundManager;

public class CreditsPanel extends OverlayPanel{
    private JPanel containerPanel = getContainerPanel();
    private int size = 600;
    private int panelWidth;
    private int panelHeight;
    private int currentPage = 1;
    private GameButton prevBtn = new GameButton("mainMenu/leftArrow_button.png", "mainMenu/leftArrow_button.png", null);
    private GameButton nextBtn = new GameButton("mainMenu/rightArrow_button.png", "mainMenu/rightArrow_button.png", null);
    private Image credits1 = new ImageIcon(getClass().getResource("/assets/interface/mainMenu/credits1.png")).getImage();
    private Image credits2 = new ImageIcon(getClass().getResource("/assets/interface/mainMenu/credits2.png")).getImage();
    private Image credits3 = new ImageIcon(getClass().getResource("/assets/interface/mainMenu/credits3.png")).getImage();

    public CreditsPanel(int panelWidth, int panelHeight){
        super(panelWidth, panelHeight, false);
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        setVisible(false);

        containerPanel.setLayout(new BorderLayout());

        prevBtn.setButtonSize(50, 50);
        nextBtn.setButtonSize(50, 50);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);

        prevBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("/assets/music/click.wav");
            if(currentPage > 1){
                currentPage--;
            }else{
                currentPage = 3;
            }
            repaint();
        });
        nextBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("/assets/music/click.wav");
            if(currentPage < 3){
                currentPage++;
            }else{
                currentPage = 1;
            }
            repaint();
        });

        buttonPanel.add(prevBtn);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(nextBtn);

        containerPanel.add(buttonPanel);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                setVisible(false);
                currentPage = 1;
            }
        });
    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        switch (currentPage) {
            case 1:
                g.drawImage(credits1, (panelWidth/2)-(size/2), (panelHeight/2)-(size/2), size, size, null);
                break;
            case 2:
                g.drawImage(credits2, (panelWidth/2)-(size/2), (panelHeight/2)-(size/2), size, size, null);
                break;
            case 3:
                g.drawImage(credits3, (panelWidth/2)-(size/2), (panelHeight/2)-(size/2), size, size, null);
                break;
            default:
                g.drawImage(credits1, (panelWidth/2)-(size/2), (panelHeight/2)-(size/2), size, size, null);
                break;
        }
    }
}

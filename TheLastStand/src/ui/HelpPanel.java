package ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

public class HelpPanel extends OverlayPanel{
    private Image helpImage = new ImageIcon(getClass().getResource("/assets/interface/mainMenu/help1.png")).getImage();
    private int size = 600;
    private int panelWidth;
    private int panelHeight;
    public HelpPanel(int panelWidth, int panelHeight){
        super(panelWidth, panelHeight, false);
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        setVisible(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                setVisible(false);
            }
        });
    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(helpImage, (panelWidth/2)-(size/2), (panelHeight/2)-(size/2), size, size, null);
    }
}

package Codes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import javax.swing.JPanel;

public abstract class OverlayPanel extends JPanel{
    private int panelWidth, panelHeight;
    private JPanel containerPanel = new JPanel();
    
    public OverlayPanel(int panelWidth, int panelHeight, boolean fullScreen){
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        setBounds(0, 0, panelWidth, panelHeight);
        setLayout(new GridBagLayout());
        setOpaque(false);
        addMouseListener(new MouseAdapter(){});
        
        // Control whether full screen or not
        if (fullScreen) {
            containerPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        } else {
            containerPanel.setPreferredSize(new Dimension(500, 600));
        }
        containerPanel.setOpaque(false);
        add(containerPanel);

        setVisible(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, panelWidth, panelHeight);
    }

    public JPanel getContainerPanel() {
        return containerPanel;
    }
}

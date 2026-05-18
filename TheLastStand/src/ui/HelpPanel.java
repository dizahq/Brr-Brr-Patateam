package ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class HelpPanel extends OverlayPanel{
    private JPanel help = getContainerPanel(); 
    public HelpPanel(int panelWidth, int panelHeight){
        super(panelWidth, panelHeight, false);
        setVisible(false);

        JLabel movementLabel = new JLabel("Movement: W, A, s, D");
        JLabel shootingLabel = new JLabel("Shoot: Directional Keys");

        help.setSize(500, 400);
        help.setLayout(new BorderLayout());
    }
}

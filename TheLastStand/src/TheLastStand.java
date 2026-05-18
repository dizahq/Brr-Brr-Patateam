
// The main entry point class
// Initializes window

import javax.swing.JFrame;
import ui.MainFrame;
import javax.swing.SwingUtilities;

public class TheLastStand extends JFrame{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(e-> {
            new MainFrame()
        });
    }
}

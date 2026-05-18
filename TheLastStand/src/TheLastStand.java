package src;
// The main entry point class
// Initializes window

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import src.ui.MainFrame;

public class TheLastStand extends JFrame{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}

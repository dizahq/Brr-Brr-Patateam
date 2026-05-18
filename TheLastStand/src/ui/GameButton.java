package src.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;

public class GameButton extends JButton {
    private BufferedImage normalImg;
    private BufferedImage pressedImg;
    private BufferedImage lockedImg;

    public GameButton(String normalPath, String pressedPath, String lockedPath) {
        this.normalImg = loadImage(normalPath);
        this.pressedImg = loadImage(pressedPath);
        this.lockedImg = loadImage(lockedPath);

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private BufferedImage loadImage(String path) {
        if (path == null) {
            return null;
        }
        try {
            return ImageIO.read(new File("TheLastStand/assets/interface/" + path));
        } catch (IOException e) {
            System.out.println("[GameButton]Error loading button: " + path);
            return null;
        }
    }

    // Scale image to fit button's dimensions
    @Override 
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (!isEnabled() && lockedImg != null) {
            g2d.drawImage(lockedImg, 0, 0, getWidth(), getHeight(), null);
        } else if (getModel().isPressed()) {
            g2d.drawImage(pressedImg, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.drawImage(normalImg, 0, 0, getWidth(), getHeight(), null);
        }
    }

    public void setButtonSize(int width, int height) {
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }
}

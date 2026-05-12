package Codes;

public class SpeedyEnemy extends Enemy{

    public SpeedyEnemy(int x, int y, int panelWidth, int panelHeight){
        super(x, y, panelWidth, panelHeight);
        setSpeed(5);

        // Load speedy-specific sprites once
        walkUp    = loadStrip("Entities/Enemy/Walk/Speedy/speedy_up", 8);
        walkDown  = loadStrip("Entities/Enemy/Walk/Speedy/speedy_down", 8);
        walkLeft  = loadStrip("Entities/Enemy/Walk/Speedy/speedy_left", 8);
        walkRight = loadStrip("Entities/Enemy/Walk/Speedy/speedy_right", 8);

        attackUp    = loadStrip("Entities/Enemy/Attack/Speedy/speedy_attack_up", 6);
        attackDown  = loadStrip("Entities/Enemy/Attack/Speedy/speedy_attack_down", 6);
        attackLeft  = loadStrip("Entities/Enemy/Attack/Speedy/speedy_attack_left", 6);
        attackRight = loadStrip("Entities/Enemy/Attack/Speedy/speedy_attack_right", 6);
    }
}

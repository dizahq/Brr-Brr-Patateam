package src.objects;

public class SpeedyEnemy extends Enemy{

    public SpeedyEnemy(int x, int y, int panelWidth, int panelHeight){
        super(x, y, panelWidth, panelHeight);
        setSpeed(5); // Set higher speed value compared to default enemies

        // Load speedy-specific sprites once
        // Movement
        walkUp    = loadStrip("assets/enemies/walk/speedy/speedy_up", 8);
        walkDown  = loadStrip("assets/enemies/walk/speedy/speedy_down", 8);
        walkLeft  = loadStrip("assets/enemies/walk/speedy/speedy_left", 8);
        walkRight = loadStrip("assets/enemies/walk/speedy/speedy_right", 8);

        // Combat
        attackUp    = loadStrip("assets/enemies/attack/speedy/speedy_attack_up", 6);
        attackDown  = loadStrip("assets/enemies/attack/speedy/speedy_attack_down", 6);
        attackLeft  = loadStrip("assets/enemies/attack/speedy/speedy_attack_left", 6);
        attackRight = loadStrip("assets/enemies/attack/speedy/speedy_attack_right", 6);
    }
}

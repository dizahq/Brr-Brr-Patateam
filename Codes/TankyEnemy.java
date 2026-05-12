package Codes;

public class TankyEnemy extends Enemy{

    public TankyEnemy(int x, int y, int panelWidth, int panelHeight){
        super(x, y, panelWidth, panelHeight);
        setHealth(2); // Harder to kill

        // Load specific sprites once
        // Movement 
        walkUp    = loadStrip("Entities/Enemy/Walk/Tank/tank_up", 8);
        walkDown  = loadStrip("Entities/Enemy/Walk/Tank/tank_down", 8);
        walkLeft  = loadStrip("Entities/Enemy/Walk/Tank/tank_left", 8);
        walkRight = loadStrip("Entities/Enemy/Walk/Tank/tank_right", 8);

        // Combat
        attackUp    = loadStrip("Entities/Enemy/Attack/Tank/tank_attack_up", 6);
        attackDown  = loadStrip("Entities/Enemy/Attack/Tank/tank_attack_down", 6);
        attackLeft  = loadStrip("Entities/Enemy/Attack/Tank/tank_attack_left", 6);
        attackRight = loadStrip("Entities/Enemy/Attack/Tank/tank_attack_right", 6);

    }
}

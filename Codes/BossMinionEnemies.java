package Codes;

public class BossMinionEnemies extends Enemy{
    public BossMinionEnemies(int x, int y, int panelWidth, int panelHeight){
        super(x, y, panelWidth, panelHeight);
        setSpeed(3);

        // Load minion-specific sprites once
        walkUp    = loadStrip("Entities/Enemy/Walk/Minion/minion_up", 8);
        walkDown  = loadStrip("Entities/Enemy/Walk/Minion/minion_down", 8);
        walkLeft  = loadStrip("Entities/Enemy/Walk/Minion/minion_left", 8);
        walkRight = loadStrip("Entities/Enemy/Walk/Minion/minion_right", 8);

        attackUp    = loadStrip("Entities/Enemy/Attack/Minion/minion_attack_up", 6);
        attackDown  = loadStrip("Entities/Enemy/Attack/Minion/minion_attack_down", 6);
        attackLeft  = loadStrip("Entities/Enemy/Attack/Minion/minion_attack_left", 6);
        attackRight = loadStrip("Entities/Enemy/Attack/Minion/minion_attack_right", 6);
    }
}

package src.objects;

// BossMinionEnemies are the weaker enemies that spawn during the boss fight
public class BossMinionEnemies extends Enemy{
    public BossMinionEnemies(int x, int y, int panelWidth, int panelHeight){
        super(x, y, panelWidth, panelHeight);
        setSpeed(3);

        // Load minion-specific sprites once
        walkUp    = loadStrip("assets/enemies/walk/minion/minion_up", 8);
        walkDown  = loadStrip("assets/enemies/walk/minion/minion_down", 8);
        walkLeft  = loadStrip("assets/enemies/walk/minion/minion_left", 8);
        walkRight = loadStrip("assets/enemies/walk/minion/minion_right", 8);

        attackUp    = loadStrip("assets/enemies/attack/minion/minion_attack_up", 6);
        attackDown  = loadStrip("assets/enemies/attack/minion/minion_attack_down", 6);
        attackLeft  = loadStrip("assets/enemies/attack/minion/minion_attack_left", 6);
        attackRight = loadStrip("assets/enemies/attack/minion/minion_attack_right", 6);
    }
}

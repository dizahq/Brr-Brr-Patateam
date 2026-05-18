package src.objects;

public class TankyEnemy extends Enemy{

    public TankyEnemy(int x, int y, int panelWidth, int panelHeight){
        super(x, y, panelWidth, panelHeight);
        setHealth(2); // Harder to kill

        // Load specific sprites once
        // Movement 
        walkUp    = loadStrip("TheLastStand/assets/enemies/walk/tank/tank_up", 8);
        walkDown  = loadStrip("TheLastStand/assets/enemies/walk/tank/tank_down", 8);
        walkLeft  = loadStrip("TheLastStand/assets/enemies/walk/tank/tank_left", 8);
        walkRight = loadStrip("TheLastStand/assets/enemies/walk/tank/tank_right", 8);

        // Combat
        attackUp    = loadStrip("TheLastStand/assets/enemies/attack/tank/tank_attack_up", 6);
        attackDown  = loadStrip("TheLastStand/assets/enemies/attack/tank/tank_attack_down", 6);
        attackLeft  = loadStrip("TheLastStand/assets/enemies/attack/tank/tank_attack_left", 6);
        attackRight = loadStrip("TheLastStand/assets/enemies/attack/tank/tank_attack_right", 6);

    }
}

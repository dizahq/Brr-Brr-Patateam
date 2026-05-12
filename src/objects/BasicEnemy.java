package src.objects;

public class BasicEnemy extends Enemy{ // basic enemy with no special properties, serves as a template for other enemy types and the most common enemy encountered in the game
    public BasicEnemy(int x, int y, int panelWidth, int panelHeight){
        super(x, y, panelWidth, panelHeight);
    }
}

package src.fileio;

// Container that holds all game info we want to save 

public class SaveData {
    // Public for quick access by SaveManager
    public int currentLevel;
    public int currentWave; 
    public int lives;
    public int playerX;
    public int playerY;
    public int spawnRate;

    // Constructor used by SaveMaanger.load() to rebuild game state
    // Every parameter corresponds to a line in Savegame.txt
    public SaveData(int currentLevel, int currentWave, int lives, int playerX, int playerY, int spawnRate) {
        this.currentLevel = currentLevel;
        this.currentWave = currentWave;
        this.lives = lives;
        this.playerX = playerX;
        this.playerY = playerY;
        this.spawnRate = spawnRate;
    }

    // Test
    @Override
    public String toString() {
        return "[SaveData] level = " + currentLevel + " | wave = " + currentWave + " | lives = " + lives +  " | spawn rate = " + spawnRate + "| pos = (" + playerX + "," + playerY + ")";
    }
}

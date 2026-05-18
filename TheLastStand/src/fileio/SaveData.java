package src.fileio;

// Container that holds all game info we want to save 

public class SaveData {

    public int currentLevel;
    public int currentWave; 
    public int lives;
    public int playerX;
    public int playerY;
    public int spawnRate;

    // Reconstructs game state from values read out of savegame.txt
    // Parameter order must match the line order SaveManager writes
    public SaveData(int currentLevel, int currentWave, int lives, int playerX, int playerY, int spawnRate) {
        this.currentLevel = currentLevel;
        this.currentWave = currentWave;
        this.lives = lives;
        this.playerX = playerX;
        this.playerY = playerY;
        this.spawnRate = spawnRate;
    }

    @Override
    public String toString() {
        return "[SaveData] level = " + currentLevel + " | wave = " + currentWave + " | lives = " + lives +  " | spawn rate = " + spawnRate + "| pos = (" + playerX + "," + playerY + ")";
    }
}

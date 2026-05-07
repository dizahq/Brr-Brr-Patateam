package Codes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class SaveManager {
    private static final String SAVE_DIR = "Save";
    private static final String SAVE_FILE = SAVE_DIR + File.separator + "Savegame.txt";

    // Checks if a save file exists
    public static boolean hasSave() {
        return Files.exists(Paths.get(SAVE_FILE));
    }

    public static boolean save(SaveData data) {
        try {
            // Create saves/directory if missing
            Files.createDirectories(Paths.get(SAVE_DIR));
            
            // Write data into savegame.txt
            try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
                writer.println(data.currentLevel);
                writer.println(data.currentWave);
                writer.println(data.lives);
                writer.println(data.spawnRate);
                writer.println(data.playerX);
                writer.println(data.playerY);
            }

            System.out.println("[SaveManager] Game saved: " + data);
            return true;
        } catch (IOException e) {
            System.err.println("[SaveManager] Save failed: " + e.getMessage());
            return false;
        }
    }

    public static SaveData load() {
        if (!hasSave()) {
            System.out.println("[SaveManager] No save file found.");
            return null;
        }

        try (Scanner scanner = new Scanner(new File(SAVE_FILE))) {
            int level = Integer.parseInt(scanner.nextLine());
            int wave = Integer.parseInt(scanner.nextLine());
            int lives = Integer.parseInt(scanner.nextLine());
            int pX = Integer.parseInt(scanner.nextLine());
            int pY = Integer.parseInt(scanner.nextLine());
            int spawnRate = scanner.hasNextLine() ? Integer.parseInt(scanner.nextLine()) : 5000;

            return new SaveData(level, wave, lives, pX, pY, spawnRate);
        } catch (IOException e) {
            System.err.println("[SaveManager] Load failed (corrupt file): " + e.getMessage());
            return null;
        }
    }

    // Erase save file
    public static boolean deleteSave() {
        try {
            boolean deleted = Files.deleteIfExists(Paths.get(SAVE_FILE));
            if (deleted) {
                System.out.println("[SaveManager] Save file deleted.");
            }
            return true;
        } catch (IOException e) {
            System.err.println("[SaveManager] Delete failed: " + e.getMessage());
            return false;
        }
    }
}
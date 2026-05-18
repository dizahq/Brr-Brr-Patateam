package fileio;

// Utility class for managing game persistence
// Handles saving, loading, and deleting game state data using File I/O

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class SaveManager {
    private static final String SAVE_DIR = "TheLastStand\\saves";
    private static final String SAVE_FILE = SAVE_DIR + File.separator + "savegame.txt"; // Separator ensures it works on both windows and max/linux

    // Returns true if a valid save exists, false otherwise
    public static boolean hasSave() {
        File file = new File(SAVE_FILE);

        // Check if file exists on the disk
        if (!file.exists()) return false;

        // Check if it's just a placeholder "RESET" file
        try (Scanner scanner = new Scanner(file)){
            if (scanner.hasNextLine()) {
                String firstLine = scanner.nextLine();
                return !firstLine.equals("RESET");
            }
        } catch (IOException e) {
            return false;
        }
       
        return false;
    }

    // Serializes SaveData object fields into a text file
    public static boolean save(SaveData data) {
        try {
            // Create saves/directory if missing. Ensures the save folder exists.
            Files.createDirectories(Paths.get(SAVE_DIR)); // If save folder is missing, esnures the game won't crash when trying to write a file. Will simply build the folder first.
            
            // Write data into savegame.txt. 
            try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) { // PrintWriter writes human-readable text. FileWriter(file) opens the stream.
                writer.println(data.currentLevel);
                writer.println(data.currentWave);
                writer.println(data.lives);
                writer.println(data.playerX);
                writer.println(data.playerY);
                writer.println(data.spawnRate);
            }

            System.out.println("[SaveManager] Game saved: " + data);
            return true;
        } catch (IOException e) {
            System.err.println("[SaveManager] Save failed: " + e.getMessage());
            return false;
        }
    }

    // Reads the save file and reconstructs a SaveData object
    public static SaveData load() {
        if (!hasSave()) {
            System.out.println("[SaveManager] No save file found.");
            return null;
        }

        try (Scanner scanner = new Scanner(new File(SAVE_FILE))) {
            // Must read in the exact same order it was saved. Level -> Wave -> Lives
            int level = Integer.parseInt(scanner.nextLine());
            int wave = Integer.parseInt(scanner.nextLine());
            int lives = Integer.parseInt(scanner.nextLine());
            int pX = Integer.parseInt(scanner.nextLine());
            int pY = Integer.parseInt(scanner.nextLine());
            int spawnRate = scanner.hasNextLine() ? Integer.parseInt(scanner.nextLine()) : 5000; // Ternary check for spawnRate to prevent errors if loading an older save version

            return new SaveData(level, wave, lives, pX, pY, spawnRate);
        } catch (IOException | NumberFormatException e) { // Prevents game from crashing if savegame.txt is manually edited. Will simply return null and log a corrupt file error            System.err.println("[SaveManager] Load failed (corrupt file): " + e.getMessage());
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

    // Overwrites the current save with a RESET flag. Clears progress without deleting the file itself.
    public static void resetFile() {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
            try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
                writer.println("RESET");
            } 
            System.out.println("[SaveManager] Save file reset for new main game.");
        } catch (IOException e) {
            System.err.println("[SaveManager] Failed to reset file: " + e.getMessage());
        }
    }
}
package Codes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Game extends JPanel {
    private Player player;
    private GameLoop gameLoop;
    private List<Obstacle> obstacles = new CopyOnWriteArrayList<>();
    private List<Enemy> enemies = new CopyOnWriteArrayList<>();
    private List<Bullet> bullets = new CopyOnWriteArrayList<>();
    private Powerup activePowerup = null;

    private int currentLevel;
    private int panelWidth, panelHeight;
    private final Set<Integer> heldKeys = java.util.Collections.synchronizedSet(new HashSet<>());

    private Image grassImage;
    private Image grassOverlay; //overlay, test (NEW)
    private Image lifeFullImage;
    private Image lifeEmptyImage;
    private Image[] waveImages = new Image[5];

    private static final int HEART_SIZE = 60;
    private static final int HEART_PADDING = 16;
    private static final int HEART_MARGIN = 16;
    
    private static final int BAR_HEIGHT = 25;

    private MainLayeredPane rootLayeredPane;
    private GameButton pauseBtn = new GameButton("pauseMenu/pauseButton.png", "pauseMenu/pauseButton_pressed.png", null);

    // spawning parameters
    private double lastEnemySpawnTime;
    private int spawnCount;
    private int spawnRate = 5000;
    private int currentRespawn = 0;
    private int respawns = 3;

    // wave display
    private int currentWave = 0;

    private BossEnemy bossEnemy;

    private boolean restoringFromSave = false;

    public Game(int panelWidth, int panelHeight, MainLayeredPane rootLayeredPane) {
        this.rootLayeredPane = rootLayeredPane;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        setLayout(null);
        setFocusable(true);

        //Game Loop
        gameLoop = new GameLoop (this);

        // Load assets (bg + lives)
        grassImage = new ImageIcon("Entities/Background/Grass BG.png").getImage();          //REPLACED
        grassOverlay = new ImageIcon("Entities/Background/BG Overlay.png").getImage();      //NEW
        lifeFullImage = new ImageIcon("Entities/UserInterface/life_Full.png").getImage();
        lifeEmptyImage = new ImageIcon("Entities/UserInterface/life_Empty.png").getImage();
        for (int i = 0; i < 5; i++) {
            waveImages[i] = new ImageIcon("Entities/UserInterface/wave/wave" + (i + 1) + ".png").getImage();
        }

        initializeWave(currentLevel, null);

        // Pause button
        pauseBtn.setBounds(panelWidth - 210, 20, 180, 70);
        pauseBtn.setFocusable(false);
        pauseBtn.addActionListener(e -> {
            SoundManager.getInstance().playSFX("Music/click.wav");
            gameLoop.pauseThread();
            rootLayeredPane.getPauseMenu().setVisible(true);
        });
        add(pauseBtn);


        // Key listeners
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                heldKeys.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                heldKeys.remove(e.getKeyCode());
            }
        });

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                requestFocusInWindow();
            }
        });
    }

    public void update() {
        player.update(heldKeys, obstacles, activePowerup);

        int playerCX = player.getX() + 30;
        int playerCY = player.getY() + 30;

        // --- Enemy movement + player damage ---
        for (Enemy enemy : enemies) {
           boolean dealDamage = enemy.moveTowards(playerCX, playerCY, obstacles);

            if (dealDamage) {
                player.deductLife();;
                System.out.println("[Game] Player hit! Lives: " + player.getCurrentLives());
                if (player.getCurrentLives() <= 0) {
                    gameLoop.stopThread();
                    SoundManager.getInstance().stopMusic(); // stop background music
                    SoundManager.getInstance().playSFX("Music/game over.wav"); // play game over sound
                    SwingUtilities.invokeLater(() -> 
                        rootLayeredPane.getGameOver().setVisible(true)
                    );
                    return;
                }
            }
        }

        // --- Bullet update + collision ---
        // Collect removals first, apply after — never remove during iteration on CopyOnWriteArrayList
        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            bullet.update(enemies, bulletsToRemove, enemiesToRemove, activePowerup, this, obstacles);

            // Remove bullet if off-screen
            if (bullet.getX() < 0 || bullet.getX() > panelWidth ||
                bullet.getY() < 0 || bullet.getY() > panelHeight) {
                bulletsToRemove.add(bullet);
                System.out.println("[Game] Bullet removed (off-screen). Remaining: " + (bullets.size() - bulletsToRemove.size()));
                continue;
            }
        }

        // Safe bulk removal after all iteration is done
        bullets.removeAll(bulletsToRemove);
        enemies.removeAll(enemiesToRemove);

        if (!bulletsToRemove.isEmpty()) {
            System.out.println("[Game] Bullets remaining: " + bullets.size());
        }
        if(!enemiesToRemove.isEmpty()) {
            System.out.println("[Game] Enemies remaining: " + enemies.size());
        }

        // --- Enemy respawn waves ---
        while (currentRespawn < respawns) {
            if (System.currentTimeMillis() - lastEnemySpawnTime > spawnRate) {
                spawnEnemies(spawnCount);
            } else {
                break;
            }
        }

        // --- Level up when all waves cleared and no enemies left ---
        if (currentRespawn == respawns && enemies.isEmpty()) {
            checkGameStatus(); // win if applicable
            if (currentLevel < 1) { // only level up if not the final level
                currentLevel++;
                initializeWave(currentLevel, this.player);
            }
        }

        checkPowerup(player);
        
        if(bossEnemy != null){
            List<Enemy> newEnemies = bossEnemy.spawnEnemies();
            enemies.addAll(newEnemies);
        }

        // Temp just to test try win condition
        if (heldKeys.contains(KeyEvent.VK_K)) {
            System.out.println("[Game] Debug: Manual win triggered.");
            this.killBoss(); 
            return;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        if (grassImage != null && grassImage.getWidth(null) != -1) {
            g.drawImage(grassImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(34, 139, 34));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw game objects
        for (Bullet bullet : bullets) bullet.draw(g);
        for (Enemy enemy : enemies) enemy.draw(g);
        if(this.activePowerup != null){
            activePowerup.draw(g);
        }

        List<GameObject> drawables = new ArrayList<>();
        drawables.addAll(obstacles);
        if (player != null) drawables.add(player);
        // Fixed - sorts by feet position (correct depth sorting)
        drawables.sort((a, b) -> Integer.compare(a.getY() + a.getHeight(), b.getY() + b.getHeight()));
        for (GameObject obj : drawables) obj.draw(g);

        //draw overlay (NEW)
        if (grassOverlay != null && grassOverlay.getWidth(null) != -1) {
            g.drawImage(grassOverlay, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(34, 139, 34));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (player != null) {
            drawLives(g, player); //draw GUI on top (NEW)
        }

        // Wave counter display
        drawWaveCounter(g);

        if(bossEnemy != null){
            drawHealthBar(g, bossEnemy);
        }
    }

    // Wave display counter
    private void drawWaveCounter(Graphics g) {
        if (currentWave <= 0 || currentWave > waveImages.length) {
            return;
        }

        Image img = waveImages[currentWave - 1]; // since 0-indexed, wave1 png is in waveImages[0]
        if (img != null && img.getWidth(null) != -1) {
            int imgWidth = 200;
            int imgHeight = 80;

            // To center image
            int currentWidth = (getWidth() > 0) ? getWidth() : panelWidth;

            int x = (currentWidth - imgWidth) / 2;
            int y = 10;

            g.drawImage(img, x, y, imgWidth, imgHeight, this);
        }
    }
    
    public void drawHealthBar(Graphics g, BossEnemy bossEnemy){
        int currentHealthWidth = (int)(panelWidth * ((double)bossEnemy.getHealth()/bossEnemy.getMaxHealth()));
        g.setColor(Color.BLACK);
        g.fillRect(0, panelHeight-BAR_HEIGHT, panelWidth, BAR_HEIGHT);
        g.setColor(Color.RED);
        g.fillRect(0, panelHeight-BAR_HEIGHT, currentHealthWidth, BAR_HEIGHT);
    }

    public void initializeWave(int currentLevel, Player player) {
        if(player != null){
            this.player = player;
        }else{
            this.player = new Player(panelWidth / 2, panelHeight / 2, panelWidth, panelHeight, this);
        }
        enemies.clear(); // clear leftover enemies from previous wave
        bullets.clear(); // clear leftover bullets
        obstacles.clear();


        if (currentLevel % 2 == 0){
            obstacles.add(new Obstacle(getWidth() /2 - 300, 150, 220, 80, panelWidth, panelHeight, 1, false));
            obstacles.add(new Obstacle(getWidth() /2 + 100,300, 220, 80, panelWidth, panelHeight, 1, false));
            obstacles.add(new Obstacle(getWidth() /2 - 350, getHeight()/2 + 100, 220, 80, panelWidth, panelHeight, 1, false));
            obstacles.add(new Obstacle(getWidth() /2 - 500, getHeight()/2 - 100, 50, 400, panelWidth, panelHeight, 2, true));
            obstacles.add(new Obstacle(getWidth() - 200, getHeight()/2 - 300, 40, 300, panelWidth, panelHeight, 2, true));
            obstacles.add(new Obstacle(getWidth()/2 + 250, getHeight()/2 + 100, 200, 110, panelWidth, panelHeight, 3, false));
            obstacles.add(new Obstacle(getWidth() /2 - 50, getHeight() - 200, 160, 120, panelWidth, panelHeight, 4, false));

        }

        if (currentLevel % 2 != 0){
            obstacles.add(new Obstacle(getWidth() /2 -600,200, 220, 80, panelWidth, panelHeight, 1, false));
            obstacles.add(new Obstacle(getWidth() - 300, getHeight()/2 -50, 40, 300, panelWidth, panelHeight, 2, true));
            obstacles.add(new Obstacle(getWidth()/2 - 200, getHeight()/2 -100, 30, 200, panelWidth, panelHeight, 2, true));
            obstacles.add(new Obstacle(getWidth()/2, getHeight()/2 - 300, 220, 100, panelWidth, panelHeight, 3, false));
            obstacles.add(new Obstacle(getWidth() /2, getHeight() - 300, 160, 140, panelWidth, panelHeight, 4, false));
        }

        safelyRepositionPlayer(); 
        currentRespawn = 0;
        spawnCount = ((currentLevel * currentLevel) - (currentLevel * 2) + 20) / respawns;
        if (!restoringFromSave) {
            spawnRate = spawnRate + (int)(currentLevel * 250);
        }

        // Increment wave 
        currentWave++;
        spawnEnemies(spawnCount);
    }

    public void spawnEnemies(int enemyCount) {
        int specialEnemySpawnChance = currentLevel * 5;
        Random specialEnemyRandom = new Random();

        if(currentLevel == 4 && currentRespawn == 2){
            bossEnemy = new BossEnemy(panelWidth, panelHeight);
            enemies.add(bossEnemy);
        }

        for (int i = 0; i < enemyCount; i++) {
            Enemy enemy;
            if(specialEnemyRandom.nextInt(100)+1 <= specialEnemySpawnChance){
                int specialEnemyType = specialEnemyRandom.nextInt(2)+1;
                switch (specialEnemyType) {
                    case 1:
                        enemy = new SpeedyEnemy(0, 0, panelWidth, panelHeight);
                        break;
                    case 2:
                        enemy = new TankyEnemy(0, 0, panelWidth, panelHeight);
                        break;
                    default:
                        enemy = new BasicEnemy(0, 0, panelWidth, panelHeight);
                        break;
                }
            }else{
                enemy = new BasicEnemy(0, 0, panelWidth, panelHeight);
            }
            enemy.respawn(); // random edge position
            enemies.add(enemy);
        }
        lastEnemySpawnTime = System.currentTimeMillis();
        currentRespawn++;
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void resetGame() {
        currentLevel = 0;
        currentWave = 0;
        spawnRate = 5000;
        bullets.clear();
        enemies.clear();
        bossEnemy = null;
        heldKeys.clear();
        initializeWave(currentLevel, null);
        gameLoop.startThread();
        SwingUtilities.invokeLater(()-> requestFocusInWindow());
        System.out.println("[Game] Game reset.");
    }

    public MainLayeredPane getRootLayeredPane() {
        return rootLayeredPane;
    }

    private void drawLives(Graphics g, Player player) {
        int maxLives = player.getMaxLives();
        int currentLives = player.getCurrentLives();
        for (int i = 0; i < maxLives; i++) {
            Image img = (i < currentLives) ? lifeFullImage : lifeEmptyImage;
            int heartX = HEART_MARGIN + i * (HEART_SIZE + HEART_PADDING);
            int heartY = HEART_MARGIN;
            if (img != null && img.getWidth(null) != -1) {
                g.drawImage(img, heartX, heartY, HEART_SIZE, HEART_SIZE, this);
            }
        }
    }
    // Check if powerup is past its duration
    private void checkPowerup(Player player){
        Map<Powerup, Long> currentPowerups = player.getCurrentPowerups();
        if(currentPowerups != null){
            currentPowerups.entrySet().removeIf(powerup -> {
                if ((System.currentTimeMillis() - powerup.getValue()) > powerup.getKey().getDuration()) {
                    Powerup p = powerup.getKey();
                    if (p instanceof FireRatePowerup) player.setFireRate(500);
                    else if (p instanceof MovementSpeedPowerup) player.setSpeed(2);
                    return true;
                }
                return false;
            });
        }
    }

    public void playerRespawn() {
        player.setPosition(panelWidth/2, panelHeight/2);
        player.getCurrentPowerups().clear();
        currentWave--;
        initializeWave(currentLevel, this.player);
    }

    private void checkGameStatus() {
        if (currentLevel == 4 && bossEnemy == null && enemies.isEmpty()) {
            triggerWin();
        }
    }

    private void triggerWin() {
        // to win
        gameLoop.stopThread();
        SwingUtilities.invokeLater(() ->
            rootLayeredPane.getWinPanel().setVisible(true)
        );
        SoundManager.getInstance().stopMusic(); // stop background music
        SoundManager.getInstance().playSFX("Music/win.wav"); // play win sound
        
        System.out.println("[Game] Boss defeated! Player wins!");
    }

    public int getSpawnRate() { return spawnRate; }
    public void restoreFromSave (SaveData data) {
        currentLevel = data.currentLevel;
        currentWave = data.currentWave; // wave read back from file
        spawnRate = data.spawnRate; // difficulty read back from file
        bossEnemy = null;
        heldKeys.clear();

        restoringFromSave = true;
        currentWave--; // pre-decrement so initializeWave lands on correct wave
        initializeWave(currentLevel, null);

        restoringFromSave = false;
        player.setCurrentLives(data.lives);    
    }
    
    private void safelyRepositionPlayer() {
        int[] center = { panelWidth / 2, panelHeight / 2 };
        int[][] candidates = {
            center,
            { panelWidth / 2, panelHeight / 4 },          // top-center
            { panelWidth / 2, 3 * panelHeight / 4 },      // bottom-center
            { panelWidth / 4, panelHeight / 2 },           // left-center
            { 3 * panelWidth / 4, panelHeight / 2 },      // right-center
            { panelWidth / 4, panelHeight / 4 },           // top-left
            { 3 * panelWidth / 4, panelHeight / 4 },      // top-right
            { panelWidth / 4, 3 * panelHeight / 4 },      // bottom-left
            { 3 * panelWidth / 4, 3 * panelHeight / 4 }, // bottom-right
        };

        for (int[] pos : candidates) {
            player.setPosition(pos[0] - player.getWidth() / 2, pos[1] - player.getHeight() / 2);
            boolean blocked = obstacles.stream().anyMatch(o -> player.getBounds().intersects(o.getBounds()));
            if (!blocked) return; // found a clear spot
        }

        // Last resort: just use center and hope for the best
        player.setPosition(center[0], center[1]);
        System.out.println("[Game] Warning: no clear spawn found for player");
    }

    public void setActivePowerup(Powerup activePowerup) {
        this.activePowerup = activePowerup;
    }

    public void killBoss(){
        this.bossEnemy = null;
        enemies.clear();
        bullets.clear();
        heldKeys.clear();

        triggerWin();
    }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int level) { this.currentLevel = level; }

    public int getCurrentWave() { return currentWave; }
    public void setCurrentWave(int wave) { this.currentWave = wave; } // sets display counter - doesn't trigger initializeWave
    
    public int getLives() { return player.getCurrentLives(); }
    public void setLives(int lives) { player.setCurrentLives(lives); }

    public int getPlayerX() { return player.getX(); }
    public int getPlayerY() { return player.getY(); }
    public void setPlayerPosition(int x, int y) { player.setPosition(x, y); }

    public void startGameThread() { gameLoop.startThread(); }
    public void pauseGameThread() { gameLoop.pauseThread(); }
    public void resumeGameThread() { gameLoop.resumeThread(); }
    public void stopGameThread() { gameLoop.stopThread(); }
    public int getSoawnRate(){ return spawnRate; }


}
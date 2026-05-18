package gameloop;

import ui.Game;

public class GameLoop implements Runnable {
    private final Game game;
    private Thread gameThread;
    private volatile boolean running = false;
    private volatile boolean paused = false;

    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1_000_000_000L / TARGET_FPS;
    private static final long MAX_LAG = OPTIMAL_TIME * 5; // caps catch-up to 5 frames

    public GameLoop(Game game) {
        this.game = game;
    }

    // Creates and start the game thread
    public synchronized void startThread() {
        if (gameThread != null && gameThread.isAlive()) return;

        running = true;
        paused = false;
        gameThread = new Thread(this, "GameThread");
        gameThread.start();
        System.out.println("[Game] Game loop started.");
    }

    // Pauses game logic and rendering without stopping thread
    public void pauseThread(){
        paused = true;
        System.out.println("[Game] Game paused.");
    }

    // Resumes a paused game loop
    public void resumeThread() {
        paused = false;
        
        synchronized (this) {
            notifyAll();
        }
        System.out.println("[Game] Game resumes.");
    }

    // Stops game loop and terminates the game thread cleanly
    public void stopThread(){
        running = false;
        paused = false;

        synchronized (this) {
            notifyAll(); 
        }

        if (gameThread != null) {
            gameThread.interrupt();

            try {
                gameThread.join(500); // wait up to 500ms for clean exit
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            gameThread = null; 
        }
        
        System.out.println("[Game] Game stopped.");
    }


    // Game loop
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long lag = 0L;

        while (running) {
            if (paused) {
                synchronized (this) {
                    while (paused && running) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
                lastTime = System.nanoTime(); // Reset timer so paused time isn't counted
                lag = 0L; // Reset lag
            }

            long now = System.nanoTime();
            long elapsed = now - lastTime;
            lastTime = now;

            lag += elapsed;
            if (lag > MAX_LAG) lag = MAX_LAG;
            

            // Fixed-timestep updates
            int ticks = (int) (lag / OPTIMAL_TIME);
            for (int i = 0; i < ticks; i++) game.update();
            lag -= (long) ticks * OPTIMAL_TIME;

            // Render
            game.repaint();

            // Calculate sleep to save CPU
            long syncTime = (OPTIMAL_TIME - (System.nanoTime() - now)) / 1_000_000L;
            if (syncTime > 0) {
                try {
                    Thread.sleep(syncTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
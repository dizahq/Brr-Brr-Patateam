package src.objects;

import src.sound.SoundManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.*;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;

public abstract class Enemy extends Entity {

    // Constants 
    private static final int ATTACK_RANGE = 55;  // px — triggers attack state
    private static final int CELL = 16;  // px — A* grid cell size
    private static final int PATH_REFRESH = 60;  // frames between path recalculations
    private static final int MOVE_THRESHOLD  = 45;  // px — player displacement that forces a replan
    private static final int CONTACT_COOLDOWN_MAX = 90; // frames between contact-damage hits
    private static final int WALK_ANIM_SPEED = 5;
    private static final int ATTACK_ANIM_SPEED  = 10;
    private static final int MISS_CHANCE  = 50;  // percent
    private static final int MISS_DURATION = 800;

    private static final Random RNG = new Random();

    // State 
    private enum State  { WALK, ATTACK }
    private enum Facing { UP, DOWN, LEFT, RIGHT }

    private State  state = State.WALK;
    private State  lastState = State.WALK;
    private Facing facing = Facing.DOWN;

    // Attributes
    private int speed = 1;
    private int health = 1;

    // Combat 
    private boolean attackLanded = false;
    private int contactCooldown = 0;
    private int strikeCount = 0;

    // Miss
    private Image missImage;
    private boolean showingMiss = false;
    private long missStartTime;

    // Pathfinding 
    private final Deque<int[]> path = new ArrayDeque<>();
    private int pathCooldown = 0;
    private int lastTargetX = -1;
    private int lastTargetY = -1;

    // Stuck detection
    private int stuckTimer = 0;
    private int lastX, lastY;

    // Sprites (shared across all instances) 
    protected Image[] walkUp, walkDown, walkLeft, walkRight;
    protected Image[] attackUp, attackDown, attackLeft, attackRight;
    protected int panelWidth, panelHeight;

    public Enemy(int x, int y, int panelWidth, int panelHeight) {
        super(x, y, 80, 80);
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        if (walkDown == null) {
            walkUp    = loadStrip("assets/enemies/walk/normal/up",    8);
            walkDown  = loadStrip("assets/enemies/walk/normal/down",  8);
            walkLeft  = loadStrip("assets/enemies/walk/normal/left",  8);
            walkRight = loadStrip("assets/enemies/walk/normal/right", 8);

            attackUp    = loadStrip("assets/enemies/attack/normal/attack_up",    6);
            attackDown  = loadStrip("assets/enemies/attack/normal/attack_down",  6);
            attackLeft  = loadStrip("assets/enemies/attack/normal/attack_left",  6);
            attackRight = loadStrip("assets/enemies/attack/normal/attack_right", 6);
        }

        currentImage = walkDown[0];
        lastX = x;
        lastY = y;

        missImage = new ImageIcon("assets/interface/miss.png").getImage();
    }

    //Public API
    public boolean canContactDamage() { return contactCooldown <= 0; }
    public void resetContactCooldown() { contactCooldown = CONTACT_COOLDOWN_MAX; }
    public boolean isAttacking() { return state == State.ATTACK; }

    public int  getHealth() { return health; }
    public void setHealth(int h) { this.health = h; }
    public void deductHealth() { this.health--; }
    public void setSpeed(int speed) { this.speed = speed; }

    //Teleports the enemy to a random screen edge and resets all state. 
    public void respawn() {
        int side = (int)(Math.random() * 4);
        switch (side) {
            case 0 -> { x = (int)(Math.random() * panelWidth);  y = 0; }                        // top
            case 1 -> { x = (int)(Math.random() * panelWidth);  y = panelHeight - height; }     // bottom
            case 2 -> { x = 0;                                  y = (int)(Math.random() * panelHeight); } // left
            case 3 -> { x = panelWidth - width;                 y = (int)(Math.random() * panelHeight); } // right
        }

        state        = State.WALK;
        attackLanded = false;
        strikeCount = 0;
        frameIndex = 0;
        animationTick = 0;
        contactCooldown = 0;
        lastTargetX = -1;
        lastTargetY = -1;
        stuckTimer = 0;
        lastX = x;
        lastY = y;
        path.clear();
        pathCooldown = 0;
    }

    //Advances the enemy one frame toward the target.
    public boolean moveTowards(int targetX, int targetY, List<Obstacle> obstacles) {
        if (contactCooldown > 0) contactCooldown--;

        int    centerX = x + width  / 2;
        int    centerY = y + height / 2;
        double dist    = Math.hypot(targetX - centerX, targetY - centerY);

        updateState(dist);

        if (state == State.ATTACK) {
            return tickAttackAnimation(getAttackStrip());
        }

        handleStuckDetection();

        // Recalculate center after a possible nudge
        centerX = x + width  / 2;
        centerY = y + height / 2;

        boolean playerMoved = Math.hypot(targetX - lastTargetX, targetY - lastTargetY) > MOVE_THRESHOLD;
        if (pathCooldown <= 0 || path.isEmpty() || playerMoved) {
            List<int[]> newPath = astar(centerX, centerY, targetX, targetY, obstacles);
            path.clear();
            if (newPath != null) path.addAll(newPath);
            pathCooldown = PATH_REFRESH;
            lastTargetX  = targetX;
            lastTargetY  = targetY;
        }
        pathCooldown--;

        followPath(centerX, centerY, obstacles);
        updateAnimation(getWalkStrip(), WALK_ANIM_SPEED);
        return false;
    }

    // Drawing
    @Override
    public void draw(Graphics g) {
        if (currentImage != null && currentImage.getWidth(null) != -1) {
            g.drawImage(currentImage, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(x, y, width, height);
        }

        if (showingMiss && missImage != null) {
            long elapsed = System.currentTimeMillis() - missStartTime;
            if (elapsed >= MISS_DURATION) {
                showingMiss = false;
            } else {
                Graphics2D g2d = (Graphics2D) g;

                float alpha = 1.0f - ((float) elapsed / MISS_DURATION);

                g2d.setComposite(
                    AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        alpha
                    )
                );

                // Above enemy
                g2d.drawImage(missImage, x + 10, y - 25, 50, 25, null);

                // Reset transparency
                g2d.setComposite(
                    AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        1.0f
                    )
                );
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    
    //Private Helpers
    //Transitions between WALK and ATTACK based on distance to target. */
    private void updateState(double dist) {
        if (dist <= ATTACK_RANGE) {
            state = State.ATTACK;
            if (lastState == State.WALK) {
                resetAttackAnim();
            } else if (isAttackFinished() && attackLanded) {
                resetAttackAnim();
            }
        } else if (state == State.ATTACK && isAttackFinished()) {
            state       = State.WALK;
            strikeCount = 0;
            resetAttackAnim();
        }
        lastState = state;
    }

    private void resetAttackAnim() {
        attackLanded  = false;
        frameIndex    = 0;
        animationTick = 0;
    }

    // Detects when the enemy hasn't moved for 30 frames and nudges it
    private void handleStuckDetection() {
        if (Math.abs(x - lastX) < 1 && Math.abs(y - lastY) < 1) {
            if (++stuckTimer > 30) {
                path.clear();
                pathCooldown = 0;
                stuckTimer   = 0;

                int nudgeX = (int)((Math.random() * 2 - 1) * CELL * 2);
                int nudgeY = (int)((Math.random() * 2 - 1) * CELL * 2);
                x = Math.max(0, Math.min(panelWidth  - width,  x + nudgeX));
                y = Math.max(0, Math.min(panelHeight - height, y + nudgeY));
            }
        } else {
            stuckTimer = 0;
        }
        lastX = x;
        lastY = y;
    }

    //Moves the enemy one step along the current A* path.
    private void followPath(int centerX, int centerY, List<Obstacle> obstacles) {
        if (path.isEmpty()) return;

        int[]  next = path.peek();
        double dx   = next[0] - centerX;
        double dy   = next[1] - centerY;
        double len  = Math.hypot(dx, dy);

        if (len <= CELL) {
            path.poll();
            return;
        }

        int newX = clampX((int) Math.round(x + (dx / len) * speed));
        int newY = clampY((int) Math.round(y + (dy / len) * speed));

        if      (getBlockingObstacle(newX, newY, obstacles) == null) { x = newX; y = newY; }
        else if (getBlockingObstacle(newX, y,    obstacles) == null) { x = newX; }
        else if (getBlockingObstacle(x,    newY, obstacles) == null) { y = newY; }
        else    { stuckTimer += 5; } // fully blocked — let stuckTimer handle the replan

        double actualDx = (x + width  / 2.0) - centerX;
        double actualDy = (y + height / 2.0) - centerY;
        if (actualDx != 0 || actualDy != 0) updateFacing(actualDx, actualDy);
    }

    private int clampX(int nx) { return Math.max(0, Math.min(panelWidth  - width,  nx)); }
    private int clampY(int ny) { return Math.max(0, Math.min(panelHeight - height, ny)); }

    // A* Pathfinding implementation
    private List<int[]> astar(int startX, int startY, int goalX, int goalY, List<Obstacle> obstacles) {
        int cols = panelWidth  / CELL + 1;
        int rows = panelHeight / CELL + 1;

        int sc = clamp(startX / CELL, 0, cols - 1);
        int sr = clamp(startY / CELL, 0, rows - 1);
        int gc = clamp(goalX  / CELL, 0, cols - 1);
        int gr = clamp(goalY  / CELL, 0, rows - 1);

        int inflate = (Math.max(width, height) / 2) / CELL + 1;

        boolean[][] blocked = buildBlockedGrid(rows, cols, inflate, obstacles);
        clearRadius(blocked, rows, cols, sr, sc, inflate);
        clearRadius(blocked, rows, cols, gr, gc, inflate);

        int[][] gCost  = new int[rows][cols];
        int[][] parent = new int[rows][cols];
        for (int[] row : gCost)  Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] row : parent) Arrays.fill(row, -1);

        PriorityQueue<int[]> open = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        gCost[sr][sc] = 0;
        open.offer(new int[]{ heuristic(sc, sr, gc, gr), sc, sr });

        int[][] dirs = {
            {-1,-1},{0,-1},{1,-1},
            {-1, 0},       {1, 0},
            {-1, 1},{0, 1},{1, 1}
        };

        boolean found = false;
        while (!open.isEmpty()) {
            int[] cur = open.poll();
            int cc = cur[1], cr = cur[2];
            if (cc == gc && cr == gr) { found = true; break; }

            for (int[] d : dirs) {
                int nc = cc + d[0], nr = cr + d[1];
                if (nc < 0 || nc >= cols || nr < 0 || nr >= rows) continue;
                if (blocked[nr][nc]) continue;
                if (d[0] != 0 && d[1] != 0 && (blocked[cr][cc + d[0]] || blocked[cr + d[1]][cc])) continue;

                int step = (d[0] != 0 && d[1] != 0) ? 14 : 10;
                int ng   = gCost[cr][cc] + step;
                if (ng < gCost[nr][nc]) {
                    gCost[nr][nc]  = ng;
                    parent[nr][nc] = cr * cols + cc;
                    open.offer(new int[]{ ng + heuristic(nc, nr, gc, gr), nc, nr });
                }
            }
        }

        if (!found) return null;

        List<int[]> waypoints = new ArrayList<>();
        int cc = gc, cr = gr;
        while (!(cc == sc && cr == sr)) {
            waypoints.add(0, new int[]{ cc * CELL + CELL / 2, cr * CELL + CELL / 2 });
            int p = parent[cr][cc];
            if (p < 0) break;
            cr = p / cols;
            cc = p % cols;
        }

        if (waypoints.isEmpty()) waypoints.add(new int[]{ goalX, goalY });
        return waypoints;
    }

    private boolean[][] buildBlockedGrid(int rows, int cols, int inflate, List<Obstacle> obstacles) {
        boolean[][] blocked = new boolean[rows][cols];
        for (Obstacle obs : obstacles) {
            Rectangle b = obs.getBounds();
            int minC = Math.max(0,        b.x / CELL - inflate);
            int maxC = Math.min(cols - 1, (b.x + b.width)  / CELL + inflate);
            int minR = Math.max(0,        b.y / CELL - inflate);
            int maxR = Math.min(rows - 1, (b.y + b.height) / CELL + inflate);
            for (int r = minR; r <= maxR; r++)
                for (int c = minC; c <= maxC; c++)
                    blocked[r][c] = true;
        }
        return blocked;
    }

    private void clearRadius(boolean[][] blocked, int rows, int cols, int centerR, int centerC, int radius) {
        for (int dr = -radius; dr <= radius; dr++)
            for (int dc = -radius; dc <= radius; dc++) {
                int r = centerR + dr, c = centerC + dc;
                if (r >= 0 && r < rows && c >= 0 && c < cols) blocked[r][c] = false;
            }
    }

    private int heuristic(int c1, int r1, int c2, int r2) {
        int dx = Math.abs(c1 - c2), dy = Math.abs(r1 - r2);
        return 10 * Math.max(dx, dy) + (dx + dy);
    }

    private int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }

    private Obstacle getBlockingObstacle(int nx, int ny, List<Obstacle> obstacles) {
        Rectangle test = new Rectangle(nx, ny, width, height);
        for (Obstacle obs : obstacles)
            if (test.intersects(obs.getBounds())) return obs;
        return null;
    }

    // Animation
    private boolean tickAttackAnimation(Image[] frames) {
        boolean isDamageFrame = false;
        if (++animationTick >= ATTACK_ANIM_SPEED) {
            animationTick = 0;
            if (frameIndex < frames.length - 1) frameIndex++;

            if (frameIndex == 2 && !attackLanded) {
                attackLanded = true;
                strikeCount++;
                SoundManager.getInstance().playSFX("assets/music/sword.wav");
                if (RNG.nextInt(100) >= MISS_CHANCE) {
                    isDamageFrame = true;
                    strikeCount   = 0;
                } else {
                    showingMiss = true;
                    missStartTime = System.currentTimeMillis();
                    System.out.println("Enemy attack missed!");
                }
            }
        }
        currentImage = frames[frameIndex];
        return isDamageFrame;
    }

    private boolean isAttackFinished() { return frameIndex >= getAttackStrip().length - 1; }

    private void updateFacing(double moveX, double moveY) {
        if (Math.abs(moveX) > Math.abs(moveY)) facing = (moveX > 0) ? Facing.RIGHT : Facing.LEFT;
        else                                    facing = (moveY > 0) ? Facing.DOWN  : Facing.UP;
    }

    private Image[] getWalkStrip() {
        return switch (facing) {
            case UP    -> walkUp;
            case DOWN  -> walkDown;
            case LEFT  -> walkLeft;
            case RIGHT -> walkRight;
        };
    }

    private Image[] getAttackStrip() {
        return switch (facing) {
            case UP    -> attackUp;
            case DOWN  -> attackDown;
            case LEFT  -> attackLeft;
            case RIGHT -> attackRight;
        };
    }
}
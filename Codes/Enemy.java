package Codes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.*;

public abstract class Enemy extends Entity {

    //Movement and behavior constants 
    private static final int ATTACK_RANGE = 55; //px - triggers attack state 
    private static final int CELL = 16; // px - A* grid cell size
    private static final int PATH_REFRESH = 60; // frames between path recalculations
    private static final int MOVE_THRESHOLD = 45; //px - player displacement that forces a replan   
    private static final int CONTACT_COOLDOWN_MAX = 90; //frames bbetween contact damage instances
    private static final int WALK_ANIM_SPEED   = 5; //frames per sprite change in walk animation
    private static final int ATTACK_ANIM_SPEED = 10;
    private static final int MISS_CHANCE = 50; // percent chance for an attack to miss, resulting in no damage dealt
    private static final Random missRate = new Random(); //new (missrate)

    //States
    private enum State  { WALK, ATTACK } // current behavior state
    private enum Facing { UP, DOWN, LEFT, RIGHT } // current facing direction for animation purposes
    private State  state  = State.WALK; //current behavior state
    private State  lastState  = State.WALK; //state from previous frame, used to detect transitions
    private Facing facing = Facing.DOWN; //current facing direction

    // Attributes:
    private int speed = 1; //movement speed
    private int health = 1; //number of hits to kill

    //Combat
    private int strikeCount = 0; //tracks how many swings have happened in current attack cycle
    private boolean attackLanded = false; // whether the current swing has already dealt damage
    private int contactCooldown = 0; //countdown timer for contact damage cooldown

    //Pathfinding
    private final Deque<int[]> path = new ArrayDeque<>();
    private int pathCooldown = 0;//countdown before next path recalculation
    private int lastTargetX = -1; //last known player X for move threshold check
    private int lastTargetY = -1; // last known player Y for move threshold check

    //Stuck detection
    private int stuckTimer = 0; //counts frames thep enemy hasn't moved
    private int lastX, lastY; //position last frame, used for stuck detection
    
    //Sprites
    protected Image[] walkUp, walkDown, walkLeft, walkRight;
    protected Image[] attackUp, attackDown, attackLeft, attackRight;

    //Panel
    protected int panelWidth, panelHeight; //game panel bound for clamping position

    //Constructors
    public Enemy(int x, int y, int panelWidth, int panelHeight) {
        super(x, y, 80, 80);
        this.panelWidth  = panelWidth;
        this.panelHeight = panelHeight;

        // Load sprites— all Enemy instances share the same arrays
        if (walkDown == null){
            walkUp    = loadStrip("Entities/Enemy/Walk/Normal/up",8);
            walkDown  = loadStrip("Entities/Enemy/Walk/Normal/down",8);
            walkLeft  = loadStrip("Entities/Enemy/Walk/Normal/left",  8);
            walkRight = loadStrip("Entities/Enemy/Walk/Normal/right", 8);

            attackUp    = loadStrip("Entities/Enemy/Attack/Normal/attack_up",6);
            attackDown  = loadStrip("Entities/Enemy/Attack/Normal/attack_down",6);
            attackLeft  = loadStrip("Entities/Enemy/Attack/Normal/attack_left",6);
            attackRight = loadStrip("Entities/Enemy/Attack/Normal/attack_right",6);
        }

        currentImage = walkDown[0]; // default idle frame facing down
        lastX = x; lastY = y;
    }

    // Returns true if enough time has passed since last contact damage
    public boolean canContactDamage()  {  return contactCooldown <= 0; }

    // Resets the cooldown timer after contact damage is dealt
    public void resetContactCooldown() { contactCooldown = CONTACT_COOLDOWN_MAX; }

    // Repositions the enemy at a random edge of the screen and resets all state
    public void respawn() {
        int side = (int)(Math.random() * 4); // pick a random screen edge
        strikeCount = 0;

        switch (side) {
            case 0: //top
                x = (int)(Math.random() * panelWidth);  
                y = 0; 
            break;
            case 1: //bottom
                x = (int)(Math.random() * panelWidth);  
                y = panelHeight - height; 
            break;
            case 2: 
                x = 0; //left
                y = (int)(Math.random() * panelHeight); 
            break;
            case 3: //right
                x = panelWidth - width; 
                y = (int)(Math.random() * panelHeight); 
            break;
        }

        // Reset all state to defaults
        state = State.WALK;
        attackLanded = false;
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

    // Main method to move enemy towards target (player) and handle state transitions
    public boolean moveTowards(int targetX, int targetY, List<Obstacle> obstacles) {
        if (contactCooldown > 0) contactCooldown--;

        int centerX = x + width  / 2;
        int centerY = y + height / 2;
        double dist = Math.hypot(targetX - centerX, targetY - centerY);

        updateState(dist);

        if (state == State.ATTACK) {
            return tickAttackAnimation(getAttackStrip());
        }

        handleStuckDetection();

        // Recalculate center after possible nudge
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

    //Draw
    @Override
    public void draw(Graphics g) {// Draw sprite if loaded, otherwise fall back to a plain oval
        if (currentImage != null && currentImage.getWidth(null) != -1) {
            g.drawImage(currentImage, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(x, y, width, height);
        }
    }

    @Override
    public Rectangle getBounds() {// for collision detection, based on current position and size
        return new Rectangle(x, y, width, height);
    }

    //Private helpers
    private void updateState(double dist){// handles switching between walk and attack states based on distance to player, and resets animation on state change
        if (dist <= ATTACK_RANGE) {
            state = State.ATTACK;
            if(lastState == State.WALK){
                resetAttackAnimation();
            } else if (isAttackFinished() && attackLanded) {
                resetAttackAnimation();
            }
        } else if (state == State.ATTACK && isAttackFinished()){
            state = State.WALK;
            strikeCount = 0;
            resetAttackAnimation();
        }
        lastState = state;  
    }

    private void resetAttackAnimation(){// called on attack start and after each strike in the attack cycle
        attackLanded  = false;
        frameIndex    = 0;
        animationTick = 0;
    }

    private void handleStuckDetection(){// detects if the enemy is stuck (not moved for a certain number of frames) and applies a random nudge to try to get unstuck, as well as resetting the path to force recalculation
        if (Math.abs(x - lastX) < 1 && Math.abs(y - lastY) < 1) {
            if (stuckTimer > 30) { // if stuck for more than 2 seconds
                path.clear(); // clear current path to force recalculation
                pathCooldown = 0; // reset cooldown to allow immediate pathfinding
                stuckTimer = 0;

                int nudgeX = (int)((Math.random() * 2 - 1) * CELL * 2);
                int nudgeY = (int)((Math.random() * 2 - 1) * CELL * 2);
                x = Math.max(0, Math.min(panelWidth  - width,  x + nudgeX));
                y = Math.max(0, Math.min(panelHeight - height, y + nudgeY));
            }
        } else {
            stuckTimer = 0; // reset timer if enemy has moved
        }
        lastX = x;
        lastY = y;
    }

    private void followPath(int centerX, int centerY, List<Obstacle> obstacles){ // moves enemy along current path, with basic collision handling
        if (path.isEmpty()) { return; }

        int[] next = path.peek();
        double dx = next[0] - centerX;
        double dy = next[1] - centerY;
        double len = Math.hypot(dx, dy);

        if (len <= CELL){
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

    private int clampX(int nx) { return Math.max(0, Math.min(panelWidth  - width,  nx)); } //  clamps position to stay within panel bounds
    private int clampY(int ny) { return Math.max(0, Math.min(panelHeight - height, ny)); } // clamps position to stay within panel bounds
    private boolean isAttackFinished() { return frameIndex >= getAttackStrip().length - 1; } // checks if the attack animation has completed its cycle, used to time state transitions and damage application


    // A* pathfinder 
    private List<int[]> astar(int startX, int startY, int goalX, int goalY, List<Obstacle> obstacles) { // returns a list of waypoints from start to goal, or null if no path is found
        int cols = panelWidth  / CELL + 1;
        int rows = panelHeight / CELL + 1;

        int sc = clamp(startX / CELL, 0, cols - 1);
        int sr = clamp(startY / CELL, 0, rows - 1);
        int gc = clamp(goalX  / CELL, 0, cols - 1);
        int gr = clamp(goalY  / CELL, 0, rows - 1);


        int inflate = (Math.max(width, height) / 2) / CELL + 1; // number of cells to inflate obstacles by

        boolean[][] blocked = buildBlockedGrid(rows, cols, inflate, obstacles); // mark cells blocked by obstacles
        clearRadius(blocked, rows, cols, sr, sc, inflate);
        clearRadius(blocked, rows, cols, gr, gc, inflate);

        int[][] gCost  = new int[rows][cols]; // cost from start to each cell
        int[][] parent = new int[rows][cols]; // parent pointers for path reconstruction, stored as single int (row * cols + col) to save memory
        for (int[] row : gCost)  Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] row : parent) Arrays.fill(row, -1);

        PriorityQueue<int[]> open = new PriorityQueue<>(Comparator.comparingInt(a -> a[0])); // min-heap based on f-cost (g + h)
        gCost[sr][sc] = 0;
        open.offer(new int[]{ heuristic(sc, sr, gc, gr), sc, sr }); // f-cost, column, row

        int[][] dirs = { // 8 possible movement directions (including diagonals)
            {-1,-1},{0,-1},{1,-1},
            {-1, 0},       {1, 0},
            {-1, 1},{0, 1},{1, 1}
        };

        boolean found = false;
        while (!open.isEmpty()) { // main A* loop
            int[] cur = open.poll(); // current cell with lowest f-cost
            int cc = cur[1], cr = cur[2]; // current column and row
            if (cc == gc && cr == gr) { found = true; break; } // goal reached

            for (int[] d : dirs) { // explore neighbors
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

        List<int[]> waypoints = new ArrayList<>(); // reconstruct path from goal to start using parent pointers
        int cc = gc, cr = gr;
        while (!(cc == sc && cr == sr)) { // while not back at the start cell
            waypoints.add(0, new int[]{ cc * CELL + CELL / 2, cr * CELL + CELL / 2 }); // add waypoint for center of current cell
            int p = parent[cr][cc];
            if (p < 0) break;
            cr = p / cols;
            cc = p % cols;
        }

        if (waypoints.isEmpty()) waypoints.add(new int[]{ goalX, goalY }); // if no waypoints (shouldn't happen), add the goal as the only waypoint
        return waypoints;
    }

    private boolean[][] buildBlockedGrid(int rows, int cols, int inflate, List<Obstacle> obstacles) { // builds a grid marking which cells are blocked by obstacles, inflated by the enemy's size to prevent pathfinding through spaces the enemy can't fit through
        boolean[][] blocked = new boolean[rows][cols]; // mark cells blocked by obstacles, inflated by the enemy's size
        for (Obstacle obs : obstacles) { // for each obstacle, mark cells it occupies as blocked, plus an additional radius around it based on the enemy's size
            Rectangle b = obs.getBounds();
            int minC = Math.max(0,        b.x / CELL - inflate);
            int maxC = Math.min(cols - 1, (b.x + b.width)  / CELL + inflate);
            int minR = Math.max(0,        b.y / CELL - inflate);
            int maxR = Math.min(rows - 1, (b.y + b.height) / CELL + inflate);
            for (int r = minR; r <= maxR; r++){
                for (int c = minC; c <= maxC; c++)blocked[r][c] = true;
            }
        }
        return blocked;
    }

    private void clearRadius(boolean[][] blocked, int rows, int cols, int centerR, int centerC, int radius) { // clears a radius of blocked cells around a given center cell, used to ensure the start and goal positions are not blocked in the grid, even if they are close to an obstacle
        for (int dr = -radius; dr <= radius; dr++){
            for (int dc = -radius; dc <= radius; dc++) {
                int r = centerR + dr, c = centerC + dc;
                if (r >= 0 && r < rows && c >= 0 && c < cols) blocked[r][c] = false;
            }
        }
    }

    private int heuristic(int c1, int r1, int c2, int r2) { // heuristic function for A* (octile distance), estimates cost from current cell to goal, used to prioritize nodes in the open set
        int dx = Math.abs(c1 - c2);
        int dy = Math.abs(r1 - r2);
        return 10 * Math.max(dx, dy) + (dx + dy);
    }

    private int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); } // utility function to clamp a value between a minimum and maximum, used for grid indexing and position clamping

    private Obstacle getBlockingObstacle(int nx, int ny, List<Obstacle> obstacles) { // checks if a given position would collide with any obstacles, used for basic collision handling during movement
        Rectangle test = new Rectangle(nx, ny, width, height);
        for (Obstacle obs : obstacles){
            if (test.intersects(obs.getBounds())) return obs;
        }
        return null;
    }

    // Animation  
    private boolean tickAttackAnimation(Image[] frames) {// handles timing and frame changes for the attack animation, as well as determining when to apply damage based on the strike count and a random miss chance, returns true if this frame should apply damage to the player
        boolean isDamageFrame = false;
        if (++animationTick >= ATTACK_ANIM_SPEED) {
            animationTick = 0;
            if (frameIndex < frames.length - 1) frameIndex++;

            if (frameIndex == 2 && !attackLanded) {
                attackLanded = true;
                strikeCount++;
                SoundManager.getInstance().playSFX("Music/sword.wav");
                if (missRate.nextInt(100) >= MISS_CHANCE) {
                    isDamageFrame = true;
                    strikeCount   = 0;
                } else {
                    System.out.println("MISS"); // TODO: replace with miss SFX + visual cue
                }
            }
        }
        currentImage = frames[frameIndex];
        return isDamageFrame;
    }

    private void updateFacing(double moveX, double moveY) { // updates the facing direction based on the movement vector, used to select the correct animation strip for both walking and attacking
        if (Math.abs(moveX) > Math.abs(moveY)) facing = (moveX > 0) ? Facing.RIGHT : Facing.LEFT;
        else facing = (moveY > 0) ? Facing.DOWN : Facing.UP;
    }

    private Image[] getWalkStrip() {// returns the correct walking animation strip based on the current facing direction
        return switch (facing) {
            case UP    -> walkUp;
            case DOWN  -> walkDown;
            case LEFT  -> walkLeft;
            case RIGHT -> walkRight;
        };
    }

    private Image[] getAttackStrip() {// returns the correct attack animation strip based on the current facing direction
        return switch (facing) {
            case UP    -> attackUp;
            case DOWN  -> attackDown;
            case LEFT  -> attackLeft;
            case RIGHT -> attackRight;
        };
    }




    public boolean isAttacking() { return state == State.ATTACK; }

    public void deductHealth(){
        this.health--;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
}

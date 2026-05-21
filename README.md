# The Last Stand
A fast-paced, top-down 2D survival shooter built in Java Swing. Fight through escalating waves of enemies, collect power-ups, and defeat the final boss to survive.


## Gameplay & Controls

Move and shoot **independently** in 8 directions to outmaneuver threats. You start with **4 lives** (hearts). Clear 5 waves to advance to the next level.

| Action | Keys |
|---|---|
| **Move** | `↑` `↓` `←` `→` Arrow Keys |
| **Shoot** | `W` `A` `S` `D` |
| **Diagonal Shoot** | Hold two adjacent shoot keys (e.g., `W` + `D`) |
| **Pause** | Click the Pause button (top-right corner) |


## Enemy Types

**Basic Enemy** — Standard speed; takes 1 hit to defeat.
**Speedy Enemy** — Fast movement and rapid animations.
**Tanky Enemy** — Slower but durable; takes 2 hits to defeat.
**Boss Enemy** — Spawns on Level 4, Wave 3. Features a dedicated health bar and summons minions. Defeat the boss to win the game!


## Power-Ups

Walk over power-ups to gain an immediate advantage:

**Fire Rate (Red)** — Shoots more than twice as fast. *(Lasts 5 seconds)*
**Movement Speed (Blue)** — Increases character running speed. *(Lasts 10 seconds)*
**Heal (Green)** — Instantly restores 1 heart (up to the max of 4).


## Save System

The game **automatically saves** your level, wave, health, and position when you enter the Pause Menu and select **Back to Main Menu** or **Exit**.

Selecting *New Game*, or returning to the menu after a *Game Over* or *Victory*, will reset your save file.


## How to Run

**Requirements:** Java 11 or higher installed.

Double-click **TheLastStand.exe** or **TheLastStand.jar** to launch in full-screen. Or compile and run via terminal:

javac -d TheLastStand/out TheLastStand/src/TheLastStand.java TheLastStand/src/fileio/*.java TheLastStand/src/gameloop/*.java TheLastStand/src/objects/*.java TheLastStand/src/sound/*.java TheLastStand/src/ui/*.java

or 

cd Brr-Brr-Patateam
mkdir TheLastStand\out
javac -d TheLastStand/out TheLastStand/src/TheLastStand.java TheLastStand/src/fileio/*.java TheLastStand/src/gameloop/*.java TheLastStand/src/objects/*.java TheLastStand/src/sound/*.java TheLastStand/src/ui/*.java

java -cp "TheLastStand/out;TheLastStand" TheLastStand
```

> **Note:** Ensure the `asset/` folder stays in the same directory as the game executable for graphics and audio to load correctly.

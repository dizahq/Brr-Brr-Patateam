# The Last Stand

A Java Swing top-down 2D shooter where you fight through wave-based levels, collect power-ups, and take down a final boss to survive.

---

## Table of Contents

- [Gameplay Overview](#gameplay-overview)
- [Controls](#controls)
- [Levels & Waves](#levels--waves)
- [Enemies](#enemies)
- [Power-Ups](#power-ups)
- [UI & Screens](#ui--screens)
- [Save System](#save-system)
- [Sound](#sound)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)

---

## Gameplay Overview

Survive escalating waves of enemies across multiple levels. Move in 8 directions, shoot in 8 directions, dodge obstacles, and collect power-ups. Defeat the Boss Enemy on Level 4 to win.

- You start with 4 lives displayed as hearts in the top-left corner
- Enemies spawn from the edges of the screen in waves
- Each level has a different obstacle layout
- A boss with a health bar spawns at Level 4, Wave 3

---

## Controls

| Action              | Key(s)                                           |
|---------------------|--------------------------------------------------|
| Move                | `↑` `↓` `←` `→` Arrow Keys                       |
| Shoot               | `W` `A` `S` `D`                                  |
| Diagonal shoot      | Hold two shoot keys (e.g. `W` + `D` = Northeast) |
| Pause               | Pause button (top-right corner)                  |

Movement and shooting are fully **independent** — you can move in one direction and shoot in another simultaneously.

---

## Levels & Waves

- The game has 5 waves of enemies
- Enemy count per wave is calculated as: `(level² - 2×level + 20) / 3`
- Spawn delay between waves increases by `level × 250ms` each level
- Map layout alternates between even and odd level designs
- After all waves are cleared and no enemies remain, the game advances to the next level
- The game ends in victory when the boss on Wave 5 is defeated

---

## Enemies

| Type              | Description                                                       |
|-------------------|-------------------------------------------------------------------|
| `BasicEnemy`      | Default enemy; moves toward the player at normal speed            |
| `SpeedyEnemy`     | Faster movement speed; 8-frame walk/attack animations             |
| `TankyEnemy`      | Higher health (2 hits); slower but more durable                   |
| `BossEnemy`       | Spawns at Level 4, Wave 3; displays a health bar; summons minions |

Special enemies (`SpeedyEnemy`, `TankyEnemy`) have a spawn chance of `level × 5%`, increasing each level.

---

## Power-Ups

| Power-Up                  | Effect                                     | Duration   |
|---------------------------|--------------------------------------------|------------|
| 🔴 `FireRatePowerup`      | Fire rate: 500ms → 200ms cooldown          | 5 seconds  |
| 🔵 `MovementSpeedPowerup` | Player speed: 3 → 5                        | 10 seconds |
| 🟢 `HealPowerUp`          | Restores 1 life (only if below max)        | Instant    |

When a timed power-up expires, the stat resets to its default value automatically.

---

## UI & Screens

| Screen             | Description                                                         |
|--------------------|---------------------------------------------------------------------|
| Main Menu          | New Game, Continue (disabled if no save), Exit                      |
| Game               | Main gameplay panel with HUD (hearts, wave counter, boss health bar)|
| Pause Menu         | Resume, Back to Main Menu (auto-saves), Exit (auto-saves)           |
| Game Over          | Respawn (restart current level) or return to Main Menu              |
| Win Screen         | Return to Main Menu or Exit                                         |
| Exit Confirm       | Yes/No confirmation dialog before quitting                          |

All overlay panels use a semi-transparent dark background layered over the game via `JLayeredPane`.

---

## Save System

Progress is saved to `Save/Savegame.txt` as plain text.

Saved data includes:
- Current level and wave
- Player lives remaining
- Player position (X, Y)
- Enemy spawn rate (difficulty scaling)

| Action                   | When it happens                                  |
|--------------------------|--------------------------------------------------|
| Save                     | Pause → Back to Main Menu, or Pause → Exit       |
| Load                     | Main Menu → Continue                             |
| Delete                   | New Game, Game Over → Main Menu, Win → Main Menu |
| Reset (mark as empty)    | On every application startup                     |

---

## Sound

`SoundManager` is a singleton that handles background music and sound effects.

- Background music loops continuously and switches between `MainMenu_music.wav` and `Game_music.wav`
- The same track will not restart if it is already playing
- SFX clips are opened, played, and automatically closed after playback
- Volume is set in decibels converted from a `0.0–1.0` float range

---

## Project Structure

```
__TheLastStand/
|── asset/
|   ├── background/               
│   ├── enemies/ 
|   |   |── attack/
|   |   |── walk/
|   |── music/ 
|   |── object/
|   |── obstacles/
|   |── player/
|   |   |── attack/
|   |   |── normal/
|   |── ui/
|── config/
|   |──README.md
|── saves/
|── src/       
│   ├── fileio/   
|   |   |── SaveData.java
|   |   |── SaveManager.java
|   |   |── SoundManager.java              
│   ├── objects/
|   |   |── Enemy.java
|   |   |── BasicEnemy.java
|   |   |── BossEnemy.java 
|   |   |── Entity.java
|   |   |── GameObject.java  
|   |   |── Obstacle.java
|   |   |── SpeedyEnemy.java
|   |   |── TankyEnemy.java
|   |   |── Player.java           
│   |   ├── powerUps/ 
|   |   |   |── FireRatePowerup.java
|   |   |   |── HealPowerUp.java
|   |   |   |── MovementSpeedPowerup.java
|   |   |   |── Powerup.java
|   |   |── main/
|   |   |   |── Game.java
|   |   |   |── GameLoop.java
|   |   |   |── TheLastStand.java
|   |── threads 
|   |   |──GameLoop.java
|   |── ui
|   |   |── MainLayeredPane.java
|   |   |── MainPanel.java
|   |   |── MainMenuPanel.java
|   |   |── PauseMenuPanel.java
|   |   |── GameOverPanel.java
|   |   |── WinPanel.java
|   |   |── ExitConfirmPanel.java
|   |   |── OverlayPanel.java
|   |   |── GameButton.java
|   |___|
|___|

---

## How to Run

### Requirements

- Java 11 or higher
- No external libraries — uses the Java standard library only

### Steps

1. Clone or download the project
2. Compile all source files from the project root:
   ```bash
   javac Codes/*.java
   ```
3. Run the entry point:
   ```bash
   java Codes.TheLastStand
   ```

The game launches full-screen and undecorated, sized to your monitor's resolution. Ensure the `Entities/` and `Music/` folders are in the same directory you run the command from.

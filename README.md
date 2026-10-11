# Shile

> "Whatever you do, do not look at Shile."

**Shile** is an atmospheric client-server horror mod for Minecraft (Fabric 26.2). A stealthy entity appears behind the player, moving along an orbital trajectory to reach their front. Looking directly at Shile triggers a mandatory, high-stakes Quick-Time Event (QTE).

---

## Concept

Inspired by the entity Alma from Roblox DOORS, Shile introduces psychological tension into Minecraft. Shile does not rush or jump-scare the player. Instead, it slowly circles around the player, attempting to get into their line of sight. The player must actively avoid looking at Shile until the encounter ends.

---

## Core Mechanics

### 1. Stealthy Spawn
- Spawns directly behind the player (180 degrees) at a radius of 2 blocks, always from behind.
- Accompanied by an unsettling audio cue.
- Remains active for a 30-second encounter window unless the mini-game is triggered. Changing dimension restarts this window.

### 2. Orbital Trajectory (60 deg/sec)
- Circles around the player at a fixed 2-block radius, at the player's height.
- Always chooses the shortest angular path (clockwise or counter-clockwise) to reach the player's front.
- Trajectory updates dynamically when the player turns.
- Stops once it reaches the player's front.

### 3. Gaze Detection (POV Raycasting)
- If the player maintains direct, uninterrupted eye contact with Shile for 1.0 second (20 ticks), the mandatory QTE mini-game begins.
- Block occlusions break line-of-sight and reset the continuous gaze timer.

### 4. QTE Mini-Game
- All open game menus and screens are immediately closed.
- At most 2-3 random letters (A-Z) are visible at once, at least 2 slots away from the screen edge. The same key never appears twice in a row.
- The player has 1.0 second per key, with a 0.5 second gap between keys. No visual effects.
- All other keys (WASD, Space, Shift) and F5 are disabled; mouse look stays active.
- The player must survive for 15.0 continuous seconds.

### 5. Server Authority and Sudden Death
- Missing a key or pressing a wrong key makes the client report failure; the server then kills the player instantly.
- Surviving the 15 seconds dispels Shile.
- Once Shile ends, an 8 to 25-minute random cooldown begins. It pauses on the pause menu and the death screen, and resumes where it left off after quitting and rejoining. Dying to Shile picks a fresh random cooldown.

---

## Technical Architecture

- Target Platform: Minecraft 26.2 (Fabric)
- Language / Toolchain: Java 25, Fabric Loom
- Networking: Type-safe CustomPacketPayload (records and stream codecs)
- Rendering: Lightweight client-side billboard quad, visible only to the target player. Shile is not a physical server mob. (Exact render method is still being verified for 26.2.)

---

## Developer
- Author: realseezz
- Status: Active Development (server state foundation)

---

## Lisans

Bu proje MIT lisansı ile yayınlanmıştır. Lisans, projedeki tüm dosyaları (kod, resim ve ses dosyaları) kapsar. Ayrıntı için LICENSE dosyasına bak.


# Shile

> "Whatever you do, do not look at Shile."

**Shile** is an atmospheric client-server horror mod for Minecraft (Fabric 26.2). A stealthy entity appears behind the player, moving along an orbital trajectory to reach their front. Looking directly at Shile triggers a mandatory, high-stakes Quick-Time Event (QTE).

---

## Concept

Inspired by the entity Alma from Roblox DOORS, Shile introduces psychological tension into Minecraft. Shile does not rush or jump-scare the player. Instead, it slowly circles around the player, attempting to get into their line of sight. The player must actively avoid looking at Shile until the encounter ends.

---

## Core Mechanics

### 1. Stealthy Spawn
- Spawns directly behind the player at an approximate radius of 2 blocks.
- Accompanied by an unsettling audio cue.
- Remains active for a 30-second encounter window unless the mini-game is triggered.

### 2. Orbital Trajectory (60 deg/sec)
- Circles around the player at a fixed 2-block radius.
- Always chooses the shortest angular path (clockwise or counter-clockwise) to reach the player's front.
- Trajectory updates dynamically based on the player's orientation.

### 3. Gaze Detection (POV Raycasting)
- The core threat is looking at Shile.
- If the player maintains direct, uninterrupted eye contact with Shile for 1.0 second (20 ticks), the mandatory QTE mini-game begins.
- Block occlusions and obstructions break line-of-sight and reset the continuous gaze timer.

### 4. QTE Mini-Game
- All open game menus and screens are immediately closed.
- Random letters (A-Z) appear at non-edge screen coordinates.
- The player has 1.0 second per key to press the corresponding letter.
- Player movement (WASD/Jump) is locked, while mouse look remains functional.
- The player must survive for 15.0 continuous seconds.

### 5. Server Authority and Sudden Death
- Missing a key or pressing the incorrect key instantly triggers server-authoritative death.
- Successfully surviving the 15-second duration dispels Shile.
- Once dispelled, an 8 to 25-minute random cooldown begins.

---

## Technical Architecture

- Target Platform: Minecraft 26.2 (Fabric)
- Language / Toolchain: Java 21, Fabric Loom
- Networking: Type-safe CustomPacketPayload (records and stream codecs)
- Rendering: Lightweight client-side billboard quad projection (WorldRenderEvents). Shile is not a physical server mob, ensuring zero server lag and complete exclusivity to the target player.

---

## Developer
- Author: seezzalith
- Status: Active Development (Phase 1)

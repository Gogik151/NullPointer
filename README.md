# 🌌 Project: NullPointer

> **Cinematic ARG Psychological Horror Mod for Minecraft Fabric 1.21.11**

`NullPointer` is a deep psychological horror mod that transforms your Minecraft survival world into an atmospheric ARG nightmare. Unlike basic jumpscare mods, `NullPointer` features custom Blockbench-grade 3D entities with multi-joint procedural animations, an autonomous 5-night nightmare progression engine, and real-time meta-horror events.

---

## 👁️ Features

### 1. Custom 3D Entities & Procedural Animations
- **The Void Walker (`nullpointer:static_walker`):**
  - Towering **3.4-block** skeletal frame with segmented spine.
  - Exposed 4-pair 3D ribcage enclosing an undulating, pulsing **Void Core**.
  - Unhinging lower jaw with sudden wide-jaw gaping spasms.
  - **4 sinuous void tendrils** emerging from the back with phase-shifted sine waving.
  - Digitigrade legs with spider-like strides and instant freezing stalk upon direct eye contact (*Weeping Angel* mechanic).
  - Jacob's ladder violent head-snaps and proximity-based VHS glitch shaders.

- **The Mimic (`nullpointer:mimic`):**
  - Grotesquely asymmetrical torso with twisted shoulder hump.
  - Blank static TV-noise face plate cavity.
  - Dislocated left arm bent backward past the knees; reaching, twitching right arm.
  - **Dragging limp gait:** Asymmetrical limp where the dislocated limb drags and swings irregularly.
  - Violent neck snapping (up to 70° onto shoulder) and player chat imitation.

### 2. Autonomous 5-Night Storyline Progression (`NightmareEngine`)
The world deteriorates dynamically as nights pass without any manual setup:
- **Night 1 — Whispers & Footsteps:** Binaural 3D whispers, sudden torch snuffing, 10% distant glimpse of The Mimic.
- **Night 2 — The Mimicry:** Active stalking, flickering redstone lamps, uncanny chat interception echoing player words.
- **Night 3 — Emergence:** The Void Walker enters your world; desktop wallpaper shifts to inverted glitch art; cryptic system note created on Desktop.
- **Night 4 — Sleep Paralysis & Dual Hunt:** Bed skipping is blocked with heavy breathing audio and locked FOV; coordinated hunting by both entities.
- **Night 5 — Culmination:** Total audio blackout, catastrophic screen glitching, culminating in a simulated Blue Screen of Death (BSOD) / crash screen.

### 3. Client Shaders & Commands
- Proximity-based chromatic aberration, VHS scanlines, and screen shakes.
- In-game debug commands:
  - `/summon nullpointer:static_walker ~ ~ ~`
  - `/summon nullpointer:mimic ~ ~ ~`
  - `/nullpointer glitch <ticks> <intensity>`
  - `/nullpointer crash [mc|bsod]`
  - `/nullpointer note [text]`

---

## 🛠️ Installation
- **Minecraft:** `1.21.11`
- **Loader:** Fabric Loader `>=0.16.0`
- **Fabric API:** Required

---

## 📜 License
MIT License

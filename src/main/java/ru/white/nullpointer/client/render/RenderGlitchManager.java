package ru.white.nullpointer.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import ru.white.nullpointer.config.NullPointerConfig;
import ru.white.nullpointer.sound.ModSounds;

import java.util.Random;

public class RenderGlitchManager {
    private static final RenderGlitchManager INSTANCE = new RenderGlitchManager();
    private final Random random = new Random();

    // Glitch state
    private int glitchTicks = 0;
    private int maxGlitchTicks = 0;
    private float currentIntensity = 0.0f;

    // Camera & Screen Shake
    private float shakePitch = 0.0f;
    private float shakeYaw = 0.0f;
    private float shakeRoll = 0.0f;

    // HUD Jitter
    private int hudJitterX = 0;
    private int hudJitterY = 0;

    // Jumpscare
    private boolean jumpscareActive = false;
    private int jumpscareTicks = 0;

    // Sleep Paralysis
    private boolean paralyzed = false;
    private int paralysisTicks = 0;

    // Scanline & noise animation
    private float scanlineOffset = 0.0f;

    public static RenderGlitchManager getInstance() {
        return INSTANCE;
    }

    public void triggerGlitch(int ticks, float intensity) {
        this.glitchTicks = Math.max(this.glitchTicks, ticks);
        this.maxGlitchTicks = this.glitchTicks;
        this.currentIntensity = Math.min(3.0f, intensity * NullPointerConfig.getInstance().glitchIntensity);
    }

    public void triggerJumpscare(int ticks) {
        if (!NullPointerConfig.getInstance().canTriggerJumpscare()) {
            triggerGlitch(ticks * 2, 1.0f);
            return;
        }

        this.jumpscareActive = true;
        this.jumpscareTicks = ticks;
        this.glitchTicks = Math.max(this.glitchTicks, ticks + 10);
        this.currentIntensity = 2.5f * NullPointerConfig.getInstance().glitchIntensity;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.playSound(ModSounds.JUMPSCARE_SCREECH, 2.5f, 0.7f + random.nextFloat() * 0.3f);
            client.player.playSound(ModSounds.WALKER_DRONE, 1.5f, 0.4f);
        }
    }

    public void setParalyzed(int ticks) {
        this.paralyzed = true;
        this.paralysisTicks = ticks;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.playSound(ModSounds.AMBIENT_HEARTBEAT, 1.8f, 0.75f);
        }
    }

    public boolean isParalyzed() {
        return paralyzed;
    }

    public void tick() {
        if (glitchTicks > 0) {
            glitchTicks--;
            float decay = (float) glitchTicks / Math.max(1, maxGlitchTicks);
            float activeIntensity = currentIntensity * decay;

            if (NullPointerConfig.getInstance().screenShake) {
                shakePitch = (random.nextFloat() * 2.0f - 1.0f) * activeIntensity * 1.8f;
                shakeYaw = (random.nextFloat() * 2.0f - 1.0f) * activeIntensity * 1.8f;
                shakeRoll = (random.nextFloat() * 2.0f - 1.0f) * activeIntensity * 2.5f;
            } else {
                shakePitch = shakeYaw = shakeRoll = 0.0f;
            }

            if (NullPointerConfig.getInstance().hudDistortion) {
                int range = Math.max(1, (int) (activeIntensity * 5.0f));
                hudJitterX = random.nextInt(range * 2 + 1) - range;
                hudJitterY = random.nextInt(range * 2 + 1) - range;
            } else {
                hudJitterX = hudJitterY = 0;
            }

            scanlineOffset = (scanlineOffset + 2.5f) % 40.0f;
        } else {
            shakePitch = shakeYaw = shakeRoll = 0.0f;
            hudJitterX = hudJitterY = 0;
            currentIntensity = 0.0f;
        }

        if (jumpscareTicks > 0) {
            jumpscareTicks--;
            if (jumpscareTicks <= 0) {
                jumpscareActive = false;
            }
        }

        if (paralysisTicks > 0) {
            paralysisTicks--;
            if (paralysisTicks <= 0) {
                paralyzed = false;
            }
        }
    }

    /**
     * Renders VHS scanlines, chromatic flicker, static grain, and jumpscare flash directly via DrawContext.
     */
    public void renderOverlay(DrawContext context, int width, int height) {
        if (glitchTicks <= 0 && !jumpscareActive && !paralyzed) {
            return;
        }

        float decay = (float) glitchTicks / Math.max(1, Math.max(1, maxGlitchTicks));
        float intensity = currentIntensity * decay;

        // 1. Sleep Paralysis Tunnel Vision
        if (paralyzed) {
            int tunnelH = height / 4;
            int tunnelW = width / 5;
            context.fill(0, 0, width, tunnelH, 0xF0000000);
            context.fill(0, height - tunnelH, width, height, 0xF0000000);
            context.fill(0, tunnelH, tunnelW, height - tunnelH, 0xF0000000);
            context.fill(width - tunnelW, tunnelH, width, height - tunnelH, 0xF0000000);
            // Pulsing dark red tint
            int redAlpha = (int) (40 + 20 * Math.sin(System.currentTimeMillis() * 0.005));
            context.fill(0, 0, width, height, (redAlpha << 24) | 0x330000);
        }

        // 2. VHS Scanlines Effect (scales with intensity)
        int scanStep = 4;
        int alpha = Math.min(160, (int) (intensity * 65));
        if (alpha > 0) {
            int scanlineColor = (alpha << 24) | 0x050505;
            for (int y = (int) scanlineOffset; y < height; y += scanStep * 2) {
                context.fill(0, y, width, y + scanStep, scanlineColor);
            }
        }

        // 3. Chromatic RGB Edge Split Flash (scales with proximity intensity)
        if (intensity > 0.25f && random.nextFloat() < Math.min(0.85f, 0.25f + intensity * 0.3f)) {
            int rgbSplitOffset = (int) (intensity * 5.0f);
            int splitAlpha = Math.min(95, (int) (intensity * 38));
            int redTint = (splitAlpha << 24) | 0xFF0000;
            int cyanTint = (splitAlpha << 24) | 0x00FFFF;
            context.fill(0, 0, width, height, redTint);
            context.fill(rgbSplitOffset, 0, width, height, cyanTint);
        }

        // 4. Static Random Noise Grain (procedural bars, heavy glitch close up)
        if (intensity > 0.4f) {
            int noiseBars = (int) (intensity * 10);
            for (int i = 0; i < noiseBars; i++) {
                int barY = random.nextInt(height);
                int barH = 1 + random.nextInt(5);
                int barAlpha = 50 + random.nextInt(140);
                int barColor = (barAlpha << 24) | 0xE0E0E0;
                context.fill(0, barY, width, barY + barH, barColor);
            }
        }

        // 5. Jumpscare Sequence: Rapid Strobe, Pitch Black, and Terrifying Void Face
        if (jumpscareActive) {
            renderJumpscare(context, width, height);
        }
    }

    private void renderJumpscare(DrawContext context, int width, int height) {
        // High frequency strobe (alternate red, inverted, black)
        int strobe = jumpscareTicks % 3;
        if (strobe == 0) {
            context.fill(0, 0, width, height, 0xEE000000); // deep black
        } else if (strobe == 1) {
            context.fill(0, 0, width, height, 0xDDBB0000); // blood red
        } else {
            context.fill(0, 0, width, height, 0xEEFFFFFF); // white flash
        }

        // Render an ominous distorted face silhouette in center
        int centerX = width / 2;
        int centerY = height / 2;
        int eyeSize = 14 + (jumpscareTicks % 4) * 3;

        // Glowing hollow red eyes
        context.fill(centerX - 45 - eyeSize / 2, centerY - 25, centerX - 45 + eyeSize / 2, centerY - 25 + eyeSize, 0xFFFF0000);
        context.fill(centerX + 45 - eyeSize / 2, centerY - 25, centerX + 45 + eyeSize / 2, centerY - 25 + eyeSize, 0xFFFF0000);

        // Black pupils
        context.fill(centerX - 45 - 3, centerY - 22, centerX - 45 + 3, centerY - 16, 0xFF000000);
        context.fill(centerX + 45 - 3, centerY - 22, centerX + 45 + 3, centerY - 16, 0xFF000000);

        // Agonized distorted void mouth
        int mouthW = 80 + (jumpscareTicks % 5) * 6;
        int mouthH = 40 + (jumpscareTicks % 6) * 8;
        context.fill(centerX - mouthW / 2, centerY + 20, centerX + mouthW / 2, centerY + 20 + mouthH, 0xFF000000);
        // Jagged teeth lines
        for (int x = centerX - mouthW / 2 + 4; x < centerX + mouthW / 2 - 4; x += 8) {
            context.fill(x, centerY + 20, x + 3, centerY + 32, 0xFFE0E0E0);
            context.fill(x + 4, centerY + 20 + mouthH - 12, x + 7, centerY + 20 + mouthH, 0xFFE0E0E0);
        }

        // Glitch text watermark
        String warning = "N U L L _ P O I N T E R _ E X C E P T I O N";
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.textRenderer != null) {
            int tw = client.textRenderer.getWidth(warning);
            context.drawText(client.textRenderer, warning, centerX - tw / 2, centerY + 90, 0xFFFF2222, true);
        }
    }

    public boolean isGlitching() {
        return glitchTicks > 0 || jumpscareActive || paralyzed;
    }

    public float getShakePitch() {
        return shakePitch;
    }

    public float getShakeYaw() {
        return shakeYaw;
    }

    public float getShakeRoll() {
        return shakeRoll;
    }

    public int getHudJitterX() {
        return hudJitterX;
    }

    public int getHudJitterY() {
        return hudJitterY;
    }
}

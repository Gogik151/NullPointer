package ru.white.nullpointer.client.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import ru.white.nullpointer.client.render.RenderGlitchManager;
import ru.white.nullpointer.meta.MetaHorrorManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FakeCrashScreen extends Screen {
    public enum Style {
        MINECRAFT_CRASH,
        BSOD
    }

    private final Style style;
    private final List<String> logLines = new ArrayList<>();
    private int ticksOpen = 0;
    private static final int AUTO_TRIGGER_TICKS = 90; // ~4.5 seconds

    private final String userName;
    private final String timestamp;

    public FakeCrashScreen(Style style) {
        super(Text.literal("Crash Report"));
        this.style = style;
        this.userName = System.getProperty("user.name", "Player");
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        generateCrashLog();
    }

    public FakeCrashScreen() {
        this(Style.MINECRAFT_CRASH);
    }

    private void generateCrashLog() {
        logLines.add("---- Minecraft Crash Report ----");
        logLines.add("// He is watching you from outside the memory space.");
        logLines.add("");
        logLines.add("Time: " + timestamp);
        logLines.add("Description: Ticking entity");
        logLines.add("");
        logLines.add("java.lang.EntityObservedException: Subject '" + userName + "' has breached observation bounds.");
        logLines.add("\tat ru.white.nullpointer.core.RealityLeak.pierceReality(RealityLeak.java:13)");
        logLines.add("\tat net.minecraft.world.World.tickEntities(World.java:666)");
        logLines.add("\tat net.minecraft.client.MinecraftClient.render(MinecraftClient.java:1204)");
        logLines.add("\tat net.minecraft.client.main.Main.main(Main.java:234)");
        logLines.add("");
        logLines.add("A detailed walkthrough of the error, its code path and all known details:");
        logLines.add("--------------------------------------------------------------------------------");
        logLines.add("-- Anomaly Stacktrace --");
        logLines.add("Details:");
        logLines.add("\tTarget Host: " + userName + "@" + System.getProperty("os.name", "Windows"));
        logLines.add("\tObserver Proximity: 0.4 meters behind player");
        logLines.add("\tMemory Corrupted: 0xDEADBEEF -> NULL_POINTER");
        logLines.add("\tJava VM: " + System.getProperty("java.vm.name", "OpenJDK"));
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int bottomY = height - 36;

        if (style == Style.MINECRAFT_CRASH) {
            // Fake vanilla crash buttons
            addDrawableChild(ButtonWidget.builder(Text.literal("Open Crash Report"), b -> triggerCrashJumpScare())
                    .dimensions(centerX - 155, bottomY, 150, 20).build());

            addDrawableChild(ButtonWidget.builder(Text.literal("Close Game"), b -> triggerCrashJumpScare())
                    .dimensions(centerX + 5, bottomY, 150, 20).build());
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        // Trap the player! Escape is locked during fake crash
        return false;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (style == Style.BSOD) {
            triggerCrashJumpScare();
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        // Any keypress after initial shock accelerates the scare
        if (ticksOpen > 20) {
            triggerCrashJumpScare();
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public void tick() {
        super.tick();
        ticksOpen++;
        if (ticksOpen >= AUTO_TRIGGER_TICKS) {
            triggerCrashJumpScare();
        }
    }

    private void triggerCrashJumpScare() {
        // Fire intense jumpscare overlay & camera shake
        RenderGlitchManager.getInstance().triggerJumpscare(35);
        RenderGlitchManager.getInstance().triggerGlitch(120, 2.0f);

        // Safely spawn the desktop note in the background
        MetaHorrorManager.spawnDesktopNote("You tried to close the game. But the door locked behind you, " + userName + ".");

        // Cleanly return to the world without crashing actual client
        if (client != null) {
            client.setScreen(null);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (style == Style.BSOD) {
            renderBsod(context);
        } else {
            renderMinecraftCrash(context);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderMinecraftCrash(DrawContext context) {
        // Darkened crash background
        context.fill(0, 0, width, height, 0xFF121212);

        // Crash banner header
        context.fill(0, 0, width, 32, 0xFF281010);
        context.drawText(textRenderer, "The game crashed whilst unexpected error", width / 2 - 130, 8, 0xFFFF5555, false);
        context.drawText(textRenderer, "Minecraft 1.21.11 - Fatal Exception", width / 2 - 130, 20, 0xFFAAAAAA, false);

        // Log box
        int boxLeft = 24;
        int boxTop = 42;
        int boxRight = width - 24;
        int boxBottom = height - 46;

        context.fill(boxLeft, boxTop, boxRight, boxBottom, 0xFF000000);

        // Border
        int borderColor = 0xFF442222;
        context.fill(boxLeft, boxTop, boxRight, boxTop + 1, borderColor);
        context.fill(boxLeft, boxBottom - 1, boxRight, boxBottom, borderColor);
        context.fill(boxLeft, boxTop, boxLeft + 1, boxBottom, borderColor);
        context.fill(boxRight - 1, boxTop, boxRight, boxBottom, borderColor);

        int lineY = boxTop + 8;
        for (String line : logLines) {
            if (lineY + 10 > boxBottom - 6) break;

            int color = 0xFFCCCCCC;
            if (line.startsWith("----") || line.startsWith("Description:")) {
                color = 0xFFFF5555;
            } else if (line.startsWith("//")) {
                color = 0xFF888888;
            } else if (line.contains("EntityObservedException")) {
                color = 0xFFFF3333;
            } else if (line.contains(userName)) {
                color = 0xFFFFAA00;
            }

            context.drawText(textRenderer, line, boxLeft + 10, lineY, color, false);
            lineY += 11;
        }

        // Countdown flicker at bottom
        int remain = Math.max(0, (AUTO_TRIGGER_TICKS - ticksOpen) / 20);
        if (ticksOpen % 10 < 7) {
            String timer = "Memory dump in progress... [" + remain + "s]";
            context.drawText(textRenderer, timer, width / 2 - textRenderer.getWidth(timer) / 2, height - 14, 0xFF666666, false);
        }
    }

    private void renderBsod(DrawContext context) {
        // Windows 10/11 Cobalt Blue BSOD
        context.fill(0, 0, width, height, 0xFF0078D7);

        int startX = Math.max(40, width / 8);
        int currentY = Math.max(30, height / 6);

        // Sad face :(
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(3.0f, 3.0f);
        context.drawText(textRenderer, ":(", (int) (startX / 3.0f), (int) (currentY / 3.0f), 0xFFFFFFFF, false);
        context.getMatrices().popMatrix();

        currentY += 55;

        // BSOD Main text
        context.drawText(textRenderer, "Your PC ran into a problem and needs to restart.", startX, currentY, 0xFFFFFFFF, false);
        currentY += 14;
        context.drawText(textRenderer, "We're just collecting some error info, and then we'll restart for you.", startX, currentY, 0xFFFFFFFF, false);
        currentY += 28;

        int percent = Math.min(100, (ticksOpen * 100) / AUTO_TRIGGER_TICKS);
        String pctStr = (percent > 95 ? "NULL%" : percent + "% complete");
        context.drawText(textRenderer, pctStr, startX, currentY, 0xFFFFFFFF, false);
        currentY += 36;

        // Stop code info
        context.drawText(textRenderer, "For more information about this issue and possible fixes, visit:", startX, currentY, 0xFFD0E0FF, false);
        currentY += 14;
        context.drawText(textRenderer, "Stop code: CRITICAL_OBSERVER_BREACH", startX, currentY, 0xFFFFFFFF, false);
        currentY += 12;
        context.drawText(textRenderer, "What failed: NullPointer.sys (" + userName + ")", startX, currentY, 0xFFFFFFFF, false);
    }
}

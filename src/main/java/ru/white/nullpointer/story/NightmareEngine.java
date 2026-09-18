package ru.white.nullpointer.story;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class NightmareEngine {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static NightmareEngine INSTANCE;

    public enum Phase {
        PHASE_1_WHISPERS(1, "Night 1: Whispers & Shadows"),
        PHASE_2_THE_MIMIC(2, "Night 2: The Mimic & Chat"),
        PHASE_3_STATIC_WALKER(3, "Night 3: The Static Walker & Meta Shift"),
        PHASE_4_SLEEP_PARALYSIS(4, "Night 4: Sleep Paralysis & Dual Hunt"),
        PHASE_5_CULMINATION(5, "Night 5: Culmination & Blackout");

        private final int requiredDays;
        private final String displayName;

        Phase(int requiredDays, String displayName) {
            this.requiredDays = requiredDays;
            this.displayName = displayName;
        }

        public int getRequiredDays() {
            return requiredDays;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Phase fromDays(int days) {
            if (days >= 5) return PHASE_5_CULMINATION;
            if (days == 4) return PHASE_4_SLEEP_PARALYSIS;
            if (days == 3) return PHASE_3_STATIC_WALKER;
            if (days == 2) return PHASE_2_THE_MIMIC;
            return PHASE_1_WHISPERS;
        }
    }

    private int daysSurvived = 1;
    private long lastCheckedTime = 0;
    private boolean finaleTriggered = false;

    public static NightmareEngine getOrCreate(MinecraftServer server) {
        if (INSTANCE == null) {
            INSTANCE = load(server);
        }
        return INSTANCE;
    }

    public static NightmareEngine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NightmareEngine();
        }
        return INSTANCE;
    }

    private static File getFile(MinecraftServer server) {
        Path root = server.getSavePath(WorldSavePath.ROOT);
        return root.resolve("nullpointer_progress.json").toFile();
    }

    private static NightmareEngine load(MinecraftServer server) {
        File file = getFile(server);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                NightmareEngine loaded = GSON.fromJson(reader, NightmareEngine.class);
                if (loaded != null) return loaded;
            } catch (Exception e) {
                System.err.println("[NullPointer] Failed to load progress: " + e.getMessage());
            }
        }
        NightmareEngine fresh = new NightmareEngine();
        fresh.save(server);
        return fresh;
    }

    public void save(MinecraftServer server) {
        try (FileWriter writer = new FileWriter(getFile(server))) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            System.err.println("[NullPointer] Failed to save progress: " + e.getMessage());
        }
    }

    public void tick(ServerWorld world) {
        long timeOfDay = world.getTimeOfDay() % 24000L;

        // Detect dawn (transition from night to day ~0..500 ticks)
        if (timeOfDay < 500 && lastCheckedTime >= 23000) {
            daysSurvived++;
            System.out.println("[NullPointer] Day incremented: Day " + daysSurvived + " (" + getPhase().getDisplayName() + ")");
            save(world.getServer());
        }
        lastCheckedTime = timeOfDay;

        // Execute anomalies
        AnomalyController.tick(world, this);
    }

    public Phase getPhase() {
        return Phase.fromDays(daysSurvived);
    }

    public int getDaysSurvived() {
        return daysSurvived;
    }

    public void setDaysSurvived(int days, MinecraftServer server) {
        this.daysSurvived = Math.max(1, days);
        this.finaleTriggered = false;
        save(server);
    }

    public boolean isFinaleTriggered() {
        return finaleTriggered;
    }

    public void setFinaleTriggered(boolean finaleTriggered, MinecraftServer server) {
        this.finaleTriggered = finaleTriggered;
        save(server);
    }
}

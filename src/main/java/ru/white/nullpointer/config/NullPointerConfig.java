package ru.white.nullpointer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class NullPointerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("nullpointer.json").toFile();
    private static NullPointerConfig INSTANCE;

    // --- Meta-Horror Features ---
    public boolean enableMetaHorror = true;
    public String desktopNoteFileName = "HELP_ME.txt";
    public boolean allowWallpaperChange = true;

    // --- Visual & Audio Horror ---
    public boolean enableFakeCrash = true;
    public boolean enableJumpscares = true;
    public boolean screenShake = true;
    public float glitchIntensity = 1.0f; // 0.1 to 3.0
    public boolean hudDistortion = true;

    // --- Safety Switch ---
    public boolean safeMode = false; // Disables meta-horror and intense jumpscares

    public static NullPointerConfig getInstance() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, NullPointerConfig.class);
            } catch (Exception e) {
                System.err.println("[NullPointer] Failed to load config, generating defaults: " + e.getMessage());
                INSTANCE = new NullPointerConfig();
                save();
            }
        } else {
            INSTANCE = new NullPointerConfig();
            save();
        }
    }

    public static void save() {
        if (INSTANCE == null) {
            INSTANCE = new NullPointerConfig();
        }
        try {
            File parent = CONFIG_FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            System.err.println("[NullPointer] Failed to save config: " + e.getMessage());
        }
    }

    public boolean canExecuteMetaHorror() {
        return !safeMode && enableMetaHorror;
    }

    public boolean canTriggerJumpscare() {
        return !safeMode && enableJumpscares;
    }
}

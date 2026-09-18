package ru.white.nullpointer.meta;

import ru.white.nullpointer.config.NullPointerConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MetaHorrorManager {
    private static final ExecutorService ASYNC_POOL = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "NullPointer-MetaWorker");
        t.setDaemon(true);
        return t;
    });

    /**
     * Safely generates an eerie text file on the user's desktop or user home directory.
     * Guaranteed non-destructive and asynchronous.
     */
    public static CompletableFuture<Boolean> spawnDesktopNote(String customMessage) {
        if (!NullPointerConfig.getInstance().canExecuteMetaHorror()) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String userName = System.getProperty("user.name", "Player");
                String fileName = NullPointerConfig.getInstance().desktopNoteFileName;
                if (fileName == null || fileName.isBlank()) {
                    fileName = "HELP_ME.txt";
                }

                Path targetDir = Paths.get(System.getProperty("user.home"), "Desktop");
                if (!Files.exists(targetDir) || !Files.isWritable(targetDir)) {
                    targetDir = Paths.get(System.getProperty("user.home"));
                }

                Path targetFile = targetDir.resolve(fileName);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                StringBuilder content = new StringBuilder();
                content.append("====================================================================\n");
                content.append("           PROJECT: NULLPOINTER - ANOMALY LOG                       \n");
                content.append("====================================================================\n\n");
                content.append("TIMESTAMP: ").append(timestamp).append("\n");
                content.append("SUBJECT: ").append(userName).append("\n");
                content.append("STATUS: BREACH DETECTED IN SECTOR 0x00000000\n\n");
                content.append("--------------------------------------------------------------------\n");
                if (customMessage != null && !customMessage.isBlank()) {
                    content.append(customMessage).append("\n\n");
                } else {
                    content.append("Why did you keep digging?\n");
                    content.append("The walls are no longer solid. The light stops reaching you.\n");
                    content.append("Do not look behind you when the screen flickers.\n");
                    content.append("He is not inside the game anymore, ").append(userName).append(".\n\n");
                }
                content.append("--------------------------------------------------------------------\n");
                content.append("SYSTEM RECOVERY: FAILED\n");
                content.append("MEMORY DUMP: [0xDEADBEEF, 0x00000000, 0x66666666]\n");
                content.append("====================================================================\n");

                Files.writeString(targetFile, content.toString());
                System.out.println("[NullPointer] Safe meta note written to: " + targetFile.toAbsolutePath());
                return true;
            } catch (IOException e) {
                System.err.println("[NullPointer] Note generation failed: " + e.getMessage());
                return false;
            }
        }, ASYNC_POOL);
    }

    /**
     * Automatically extracts or procedurally generates the horror wallpaper and applies it.
     */
    public static CompletableFuture<Boolean> triggerHorrorWallpaper() {
        File wallpaper = AssetExtractor.getOrGenerateWallpaper();
        if (wallpaper != null) {
            return setDesktopWallpaper(wallpaper);
        }
        return CompletableFuture.completedFuture(false);
    }

    /**
     * Changes desktop wallpaper on Windows OS via Win32 User32 SystemParametersInfo.
     * Safely wrapped with fallback and async execution.
     */
    public static CompletableFuture<Boolean> setDesktopWallpaper(File imageBmpOrJpg) {

        if (!NullPointerConfig.getInstance().canExecuteMetaHorror() || !NullPointerConfig.getInstance().allowWallpaperChange) {
            return CompletableFuture.completedFuture(false);
        }

        if (!System.getProperty("os.name", "").toLowerCase().contains("win")) {
            return CompletableFuture.completedFuture(false);
        }

        if (imageBmpOrJpg == null || !imageBmpOrJpg.exists()) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                // SPI_SETDESKWALLPAPER = 0x0014, SPIF_UPDATEINIFILE = 0x01, SPIF_SENDCHANGE = 0x02
                final int SPI_SETDESKWALLPAPER = 20;
                final int SPIF_UPDATEINIFILE = 0x01;
                final int SPIF_SENDCHANGE = 0x02;

                Class<?> user32Class = Class.forName("com.sun.jna.platform.win32.User32");
                Object instance = user32Class.getField("INSTANCE").get(null);
                Method method = user32Class.getMethod("SystemParametersInfo", int.class, int.class, String.class, int.class);
                Object result = method.invoke(instance, SPI_SETDESKWALLPAPER, 0, imageBmpOrJpg.getAbsolutePath(), SPIF_UPDATEINIFILE | SPIF_SENDCHANGE);

                System.out.println("[NullPointer] Wallpaper invocation result: " + result);
                return Boolean.TRUE.equals(result);
            } catch (Throwable t) {
                System.err.println("[NullPointer] JNA Wallpaper switch failed or unsupported: " + t.getMessage());
                return false;
            }
        }, ASYNC_POOL);
    }
}

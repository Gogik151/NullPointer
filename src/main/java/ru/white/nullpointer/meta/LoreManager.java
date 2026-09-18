package ru.white.nullpointer.meta;

import ru.white.nullpointer.config.NullPointerConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoreManager {
    private static final ExecutorService POOL = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "NullPointer-LoreWorker");
        t.setDaemon(true);
        return t;
    });

    private static Path getDesktopDir() {
        Path desktop = Paths.get(System.getProperty("user.home"), "Desktop");
        if (!Files.exists(desktop) || !Files.isWritable(desktop)) {
            return Paths.get(System.getProperty("user.home"));
        }
        return desktop;
    }

    /**
     * Generates Phase 3 technical breach report (LOG_0x77.txt)
     */
    public static CompletableFuture<Boolean> spawnPhase3Note() {
        if (!NullPointerConfig.getInstance().canExecuteMetaHorror()) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String userName = System.getProperty("user.name", "Host");
                String osName = System.getProperty("os.name", "Windows");
                Path target = getDesktopDir().resolve("LOG_0x77.txt");

                StringBuilder sb = new StringBuilder();
                sb.append("=======================================================================\n");
                sb.append(" [KERNEL MEMORY DUMP - SECTOR 0x77] BREACH DETECTED                    \n");
                sb.append("=======================================================================\n\n");
                sb.append("TARGET HOST: ").append(userName).append("@").append(osName).append("\n");
                sb.append("TIMESTAMP:   ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
                sb.append("EXCEPTION:   ru.white.nullpointer.core.RealityLeakException\n\n");
                sb.append("-----------------------------------------------------------------------\n");
                sb.append("RESEARCH TRANSCRIPT // LOG ENTRY 14:\n\n");
                sb.append("We initially thought it was a memory leak in the OpenGL buffer allocator.\n");
                sb.append("Then chunk borders began shifting without chunk updates.\n");
                sb.append("The entities weren't spawned by the world generator. They were spawned\n");
                sb.append("by something reading your active system threads.\n\n");
                sb.append(ZalgoUtil.corrupt("IT HAS IDENTIFIED SUBJECT: " + userName, 2)).append("\n\n");
                sb.append("If this file has appeared on your desktop, the process isolation is broken.\n");
                sb.append("Do NOT sleep in a bed during the night.\n");
                sb.append("Do NOT let it reach Day 7.\n\n");
                sb.append("=======================================================================\n");
                sb.append("RAW ADDRESS DUMP: [0xDEADBEEF, 0x00000000, 0x77777777, 0x66666666]\n");

                Files.writeString(target, sb.toString(), StandardCharsets.UTF_8);
                System.out.println("[NullPointer] Spawned Phase 3 note: " + target.toAbsolutePath());
                return true;
            } catch (IOException e) {
                System.err.println("[NullPointer] Failed to spawn Phase 3 note: " + e.getMessage());
                return false;
            }
        }, POOL);
    }

    /**
     * Generates Phase 4 culmination note (DO_NOT_OPEN.txt)
     */
    public static CompletableFuture<Boolean> spawnPhase4Note(String worldPath) {
        if (!NullPointerConfig.getInstance().canExecuteMetaHorror()) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String userName = System.getProperty("user.name", "Host");
                String timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                Path target = getDesktopDir().resolve("DO_NOT_OPEN.txt");

                StringBuilder sb = new StringBuilder();
                sb.append("TIME OF OBSERVATION: ").append(timeStr).append("\n");
                sb.append("HOST: ").append(userName).append("\n");
                if (worldPath != null) {
                    sb.append("CONTAINMENT BREACH IN: ").append(worldPath).append("\n\n");
                }
                sb.append("-------------------------------------------------------------------\n\n");
                sb.append(ZalgoUtil.corrupt("I AM STANDING RIGHT BEHIND YOUR CHAIR, " + userName, 3)).append("\n\n");
                sb.append("Do not look at the mirror.\n");
                sb.append("Do not look at the monitor.\n");
                sb.append("The game was never the container. You were.\n\n");
                sb.append(ZalgoUtil.corrupt("G O O D B Y E", 4)).append("\n");

                Files.writeString(target, sb.toString(), StandardCharsets.UTF_8);
                System.out.println("[NullPointer] Spawned Phase 4 note: " + target.toAbsolutePath());
                return true;
            } catch (IOException e) {
                System.err.println("[NullPointer] Failed to spawn Phase 4 note: " + e.getMessage());
                return false;
            }
        }, POOL);
    }
}

package ru.white.nullpointer.meta;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public class AssetExtractor {
    private static final String FOLDER_NAME = "NullPointer";
    private static final String WALLPAPER_FILE = "nullpointer_wallpaper.bmp";

    public static File getOrGenerateWallpaper() {
        try {
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), FOLDER_NAME);
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }

            File target = tempDir.resolve(WALLPAPER_FILE).toFile();
            if (target.exists() && target.length() > 1000) {
                return target;
            }

            // Try extracting from jar resources
            try (InputStream in = AssetExtractor.class.getResourceAsStream("/assets/nullpointer/textures/meta/horror_wallpaper.bmp")) {
                if (in != null) {
                    Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    return target;
                }
            } catch (Exception ignored) {}

            // Procedural fallback generator (1920x1080 24-bit Horror Canvas)
            return generateProceduralWallpaper(target);
        } catch (Exception e) {
            System.err.println("[NullPointer] Failed to get/generate wallpaper: " + e.getMessage());
            return null;
        }
    }

    private static File generateProceduralWallpaper(File target) {
        int width = 1920;
        int height = 1080;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        Random rnd = new Random();

        // 1. Deep Abyssal Black
        g.setColor(new Color(6, 6, 8));
        g.fillRect(0, 0, width, height);

        // 2. Analog Glitch Noise Scanlines
        for (int y = 0; y < height; y += 4) {
            int alpha = rnd.nextInt(35);
            g.setColor(new Color(180, 0, 0, alpha));
            g.fillRect(0, y, width, 2);
        }

        // 3. Digital Static Bars
        for (int i = 0; i < 40; i++) {
            int barY = rnd.nextInt(height);
            int barH = 2 + rnd.nextInt(12);
            int barW = 80 + rnd.nextInt(width);
            int barX = rnd.nextInt(width - barW);
            g.setColor(new Color(255, 10, 10, 15 + rnd.nextInt(40)));
            g.fillRect(barX, barY, barW, barH);
        }

        // 4. Eerie Void Eyes Silhouette in Center
        int cx = width / 2;
        int cy = height / 2 - 40;

        // Left Eye
        g.setColor(new Color(255, 0, 0, 220));
        g.fillOval(cx - 160, cy - 30, 70, 70);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 145, cy - 15, 40, 40);

        // Right Eye
        g.setColor(new Color(255, 0, 0, 220));
        g.fillOval(cx + 90, cy - 30, 70, 70);
        g.setColor(Color.BLACK);
        g.fillOval(cx + 105, cy - 15, 40, 40);

        // 5. Typography Warning
        String userName = System.getProperty("user.name", "PLAYER");
        g.setFont(new Font("Monospaced", Font.BOLD, 52));
        g.setColor(new Color(230, 20, 20));
        String title = "I T   C A N   S E E   Y O U";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, cx - tw / 2, cy + 160);

        g.setFont(new Font("Monospaced", Font.PLAIN, 28));
        g.setColor(new Color(170, 170, 170));
        String sub = "HOST IDENTITY: " + userName + " // MEMORY BREACH ACTIVE";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, cx - sw / 2, cy + 220);

        g.dispose();

        try {
            ImageIO.write(img, "bmp", target);
            System.out.println("[NullPointer] Generated procedural horror wallpaper at: " + target.getAbsolutePath());
            return target;
        } catch (Exception e) {
            System.err.println("[NullPointer] Failed writing BMP wallpaper: " + e.getMessage());
            return null;
        }
    }
}

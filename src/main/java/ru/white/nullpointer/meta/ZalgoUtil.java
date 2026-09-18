package ru.white.nullpointer.meta;

import java.util.Random;

public class ZalgoUtil {
    private static final Random RANDOM = new Random();

    private static final char[] UP = {
            '\u030d', '\u030e', '\u0304', '\u0305', '\u033f', '\u0311', '\u0306', '\u0310',
            '\u0352', '\u0357', '\u0351', '\u0307', '\u0308', '\u030a', '\u0342', '\u0343'
    };

    private static final char[] MID = {
            '\u0315', '\u031b', '\u0340', '\u0341', '\u0358', '\u0321', '\u0322', '\u0327',
            '\u0328', '\u0334', '\u0335', '\u0336', '\u0337', '\u0338'
    };

    private static final char[] DOWN = {
            '\u0316', '\u0317', '\u0318', '\u0319', '\u031c', '\u031d', '\u031e', '\u031f',
            '\u0320', '\u0324', '\u0325', '\u0326', '\u0329', '\u032a', '\u032b', '\u032c'
    };

    public static String corrupt(String text, int intensity) {
        if (text == null || intensity <= 0) return text;
        StringBuilder sb = new StringBuilder();

        for (char c : text.toCharArray()) {
            sb.append(c);
            if (Character.isLetterOrDigit(c)) {
                int upCount = RANDOM.nextInt(intensity + 1);
                int midCount = RANDOM.nextInt(intensity / 2 + 1);
                int downCount = RANDOM.nextInt(intensity + 1);

                for (int i = 0; i < upCount; i++) {
                    sb.append(UP[RANDOM.nextInt(UP.length)]);
                }
                for (int i = 0; i < midCount; i++) {
                    sb.append(MID[RANDOM.nextInt(MID.length)]);
                }
                for (int i = 0; i < downCount; i++) {
                    sb.append(DOWN[RANDOM.nextInt(DOWN.length)]);
                }
            }
        }
        return sb.toString();
    }
}

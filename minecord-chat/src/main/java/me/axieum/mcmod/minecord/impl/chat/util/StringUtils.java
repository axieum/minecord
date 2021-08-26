package me.axieum.mcmod.minecord.impl.chat.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * Utility methods for building and manipulating strings.
 */
public final class StringUtils
{
    private StringUtils() {}

    /**
     * Converts bytes to a human-readable string, base 10.
     *
     * @param bytes number of bytes
     * @return human-readable bytes in base 10
     */
    public static String bytesToHuman(long bytes)
    {
        if (-1000 < bytes && bytes < 1000) return bytes + " B";
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}

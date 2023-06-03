package me.axieum.mcmod.minecord.api.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.Identifier;

@DisplayName("String Utils")
public class StringUtilsTests
{
    @Test
    @DisplayName("Convert bytes to human-readable strings")
    public void bytesToHuman()
    {
        assertEquals("-1.7 EB", StringUtils.bytesToHuman(-1670674413465640806L));
        assertEquals("-1.5 PB", StringUtils.bytesToHuman(-1467280456954828L));
        assertEquals("-1.7 TB", StringUtils.bytesToHuman(-1676528299409L));
        assertEquals("-1.9 GB", StringUtils.bytesToHuman(-1875645323L));
        assertEquals("-1.8 MB", StringUtils.bytesToHuman(-1766906L));
        assertEquals("-1.9 kB", StringUtils.bytesToHuman(-1906L));
        assertEquals("-953 B", StringUtils.bytesToHuman(-953L));
        assertEquals("-1 B", StringUtils.bytesToHuman(-1L));
        assertEquals("0 B", StringUtils.bytesToHuman(0L));
        assertEquals("1 B", StringUtils.bytesToHuman(1L));
        assertEquals("851 B", StringUtils.bytesToHuman(851L));
        assertEquals("1.6 kB", StringUtils.bytesToHuman(1643L));
        assertEquals("2.0 MB", StringUtils.bytesToHuman(1987654L));
        assertEquals("1.7 GB", StringUtils.bytesToHuman(1674324975L));
        assertEquals("2.0 TB", StringUtils.bytesToHuman(1968857463543L));
        assertEquals("1.5 PB", StringUtils.bytesToHuman(1546546463542643L));
        assertEquals("1.4 EB", StringUtils.bytesToHuman(1427965224842685628L));
    }

    @Nested
    @DisplayName("Translate Minecraft-formatted text to Discord flavoured markdown")
    public class MinecraftToDiscord
    {
        @Test
        @DisplayName("Suppress @everyone and @here mentions")
        public void suppressGlobalMentions()
        {
            assertEquals(
                "I can't mention @_everyone_!",
                StringUtils.minecraftToDiscord("I can't mention @everyone!")
            );
            assertEquals(
                "I can't mention everyone @_here_!",
                StringUtils.minecraftToDiscord("I can't mention everyone @here!")
            );
        }

        @Test
        @DisplayName("Strip any left over formatting")
        public void stripFormatting()
        {
            assertEquals(
                "This is green text!",
                StringUtils.minecraftToDiscord("This is §agreen§r text!")
            );
        }
    }

    @Test
    @DisplayName("Derive World Name")
    public void deriveWorldName()
    {
        assertEquals(
            "Overworld",
            StringUtils.deriveWorldName(new Identifier("minecraft", "overworld"))
        );
        assertEquals(
            "Deep Dark",
            StringUtils.deriveWorldName(new Identifier("extrautils", "the_deep_dark"))
        );
    }
}

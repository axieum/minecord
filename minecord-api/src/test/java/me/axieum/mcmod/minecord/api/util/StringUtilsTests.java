package me.axieum.mcmod.minecord.api.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @DisplayName("Translate Discord flavoured markdown to Minecraft-formatted text")
    public class DiscordToMinecraft
    {
        @Test
        @DisplayName("Translate bold text")
        public void bold()
        {
            assertEquals(
                "This is \u00A7lbold text\u00A7r!",
                StringUtils.discordToMinecraft("This is **bold text**!")
            );
        }

        @Test
        @DisplayName("Translate underlined text")
        public void underline()
        {
            assertEquals(
                "This is \u00A7nunderlined text\u00A7r!",
                StringUtils.discordToMinecraft("This is __underlined text__!")
            );
        }

        @Test
        @DisplayName("Translate italicised text")
        public void italics()
        {
            assertEquals(
                "This is \u00A7oitalicised text\u00A7r!",
                StringUtils.discordToMinecraft("This is _italicised text_!")
            );
            assertEquals(
                "This is \u00A7oitalicised text\u00A7r!",
                StringUtils.discordToMinecraft("This is *italicised text*!")
            );
        }

        @Test
        @DisplayName("Translate strikethrough text")
        public void strikethrough()
        {
            assertEquals(
                "This is \u00A7mstrikethrough text\u00A7r!",
                StringUtils.discordToMinecraft("This is ~~strikethrough text~~!")
            );
        }

        @Test
        @DisplayName("Obfuscate spoilers")
        public void spoilers()
        {
            assertEquals(
                "This is \u00A7ksecret text\u00A7r!",
                StringUtils.discordToMinecraft("This is ||secret text||!")
            );
        }

        @Test
        @DisplayName("Darken code blocks")
        public void code()
        {
            final String json = """
                {
                  "enabled": true,
                  "command": "whitelist"
                }""";
            assertEquals(
                "This is (json) \u00A77" + json + "\u00A7r text!",
                StringUtils.discordToMinecraft("This is ```json\n" + json + "``` text!")
            );
            assertEquals(
                "This is \u00A77" + json + "\u00A7r text!",
                StringUtils.discordToMinecraft("This is ```" + json + "``` text!")
            );
            assertEquals(
                "This is \u00A77inline code\u00A7r!",
                StringUtils.discordToMinecraft("This is `inline code`!")
            );
        }

        @Test
        @DisplayName("Translate emojis from unicode formatted text")
        public void emojis()
        {
            assertEquals(
                "This is a smiley :slightly_smiling: face!",
                StringUtils.discordToMinecraft("This is a smiley \uD83D\uDE42 face!")
            );
        }
    }

    @Nested
    @DisplayName("Translate Minecraft-formatted text to Discord flavoured markdown")
    public class MinecraftToDiscord
    {
        @Test
        @DisplayName("Collapse line breaks")
        public void collapseLineBreaks()
        {
            assertEquals(
                "There are no line breaks in this text!",
                StringUtils.minecraftToDiscord("There are no line\nbreaks in\n\nthis text!")
            );
        }

        @Test
        @DisplayName("Translate bold text")
        public void bold()
        {
            assertEquals(
                "This is **bold text**!",
                StringUtils.minecraftToDiscord("This is \u00A7lbold text\u00A7r!")
            );
        }

        @Test
        @DisplayName("Translate underline text")
        public void underline()
        {
            assertEquals(
                "This is __underlined text__!",
                StringUtils.minecraftToDiscord("This is \u00A7nunderlined text\u00A7r!")
            );
        }

        @Test
        @DisplayName("Translate italics text")
        public void italics()
        {
            assertEquals(
                "This is _italicised text_!",
                StringUtils.minecraftToDiscord("This is \u00A7oitalicised text\u00A7r!")
            );
        }

        @Test
        @DisplayName("Translate strikethrough text")
        public void strikethrough()
        {
            assertEquals(
                "This is ~~strikethrough text~~!",
                StringUtils.minecraftToDiscord("This is \u00A7mstrikethrough text\u00A7r!")
            );
        }

        @Test
        @DisplayName("Obfuscate spoilers")
        public void spoilers()
        {
            assertEquals(
                "This is ||secret text||!",
                StringUtils.minecraftToDiscord("This is \u00A7ksecret text\u00A7r!")
            );
        }

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
                StringUtils.minecraftToDiscord("This is \u00A7agreen\u00A7r text!")
            );
        }
    }
}

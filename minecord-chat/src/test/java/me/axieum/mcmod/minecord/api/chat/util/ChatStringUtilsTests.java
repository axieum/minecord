package me.axieum.mcmod.minecord.api.chat.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.Identifier;

@DisplayName("Chat String Utils")
public class ChatStringUtilsTests
{
    @Test
    @DisplayName("Derive World Name")
    public void deriveWorldName()
    {
        assertEquals(
            "Overworld",
            ChatStringUtils.deriveWorldName(new Identifier("minecraft", "overworld"))
        );
        assertEquals(
            "Deep Dark",
            ChatStringUtils.deriveWorldName(new Identifier("extrautils", "the_deep_dark"))
        );
    }
}

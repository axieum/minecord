package me.axieum.mcmod.minecord.api.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("String Template")
public class StringTemplateTests
{
    private StringTemplate st = null;

    @BeforeEach
    public void beforeEach()
    {
        st = new StringTemplate();
    }

    @Test
    @DisplayName("Replace variables")
    public void variables()
    {
        st.add("name", "John Doe")
            .add("age", 18)
            .add("dob", LocalDate.of(1999, 9, 9))
            .add("elapsed", Duration.ofSeconds(3720));
        assertEquals(
            "Hi, John Doe! I was born 1999-09-09 and am 18 years old. The task took 1 hour 2 minutes.",
            st.format("Hi, ${name}! I was born ${dob} and am ${age} years old. The task took ${elapsed}.")
        );
    }

    @Test
    @DisplayName("Resolve lazy variables")
    public void lazyVariables()
    {
        final Random random = new Random(9);
        st.add("roll", () -> random.nextInt(1, 6));
        assertEquals("You rolled a 5!", st.format("You rolled a ${roll}!"));
        assertEquals("You rolled a 2!", st.format("You rolled a ${roll}!"));
    }

    @Test
    @DisplayName("Resolve erroneous lazy variables")
    public void erroneousLazyVariables()
    {
        assertDoesNotThrow(() -> {
            st.add("name", () -> {
                throw new RuntimeException("Intended.");
            });
            assertEquals("Hi, !", st.format("Hi, ${name}!"));
            assertEquals("Hi, John Doe!", st.format("Hi, ${name:-John Doe}!"));
        }, "Exceptions raised when evaluating lazy variables should be caught");
    }

    @Test
    @DisplayName("Format decimals")
    public void formatDecimals()
    {
        st.add("price", 1351.4781f);
        assertEquals("The cost is $1,351.48.", st.format("The cost is ${price:\\$#,###.##}."));
    }

    @Test
    @DisplayName("Format dates")
    public void formatDates()
    {
        st.add("now", LocalDateTime.of(2021, 9, 15, 9, 27, 0));
        assertEquals(
            "The selected date is 15/09/2021 at 09:27.",
            st.format("The selected date is ${now:dd/MM/yyyy 'at' HH:mm}.")
        );
        assertEquals(
            "The selected date is Wednesday, 15 September, 2021.",
            st.format("The selected date is ${now:EEEE, d MMMM, yyyy}.")
        );
    }

    @Test
    @DisplayName("Format durations")
    public void formatDurations()
    {
        st.add("elapsed", Duration.ofSeconds(482));
        assertEquals(
            "It took 482 seconds.",
            st.format("It took ${elapsed:s} seconds.")
        );
        assertEquals(
            "It took 8m 2s.",
            st.format("It took ${elapsed:m'm' s's'}.")
        );
    }

    @Test
    @DisplayName("Handle erroneous formats")
    public void erroneousFormats()
    {
        st.add("price", 1351.4781f);
        assertDoesNotThrow(
            () -> assertEquals("The cost is ${price:\\$#,$###.##}.", st.format("The cost is ${price:\\$#,$###.##}.")),
            "Exceptions raised when formatting variables should be caught"
        );
    }

    @Test
    @DisplayName("Provide default values")
    public void defaults()
    {
        st.add("name", "John Doe")
            .add("age", null);
        assertEquals(
            "Hi, John Doe! Your age is missing.",
            st.format("Hi, ${name:-Jane Doe}! Your age is ${age:-missing}.")
        );
        assertEquals(
            "Hi, your age is still missing!",
            st.format("Hi, your age is still ${age:#,###:-missing}!")
        );
    }

    @Test
    @DisplayName("Apply transformations")
    public void transforms()
    {
        st.add("name", "Jane Doe")
            .transform(String::toLowerCase)
            .transform(s -> s.replace('a', 'z'))
            .transform(s -> s.replace('z', 'y'));
        assertEquals(
            "hi, jyne doe!",
            st.format("Hi, ${name}!")
        );
    }
}

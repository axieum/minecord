package me.axieum.mcmod.minecord.api.presence.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A Minecord presence category that holds presence suppliers to choose from.
 */
public class PresenceCategory
{
    // An instance of random to aid in choosing presences
    protected static final Random RANDOM = new Random();

    // The number of seconds between presence updates (at least 15s)
    protected long interval = 60;
    // True if presences are chosen randomly, else round-robin
    protected boolean random = false;
    // A list of presence suppliers to choose from
    protected final List<PresenceSupplier> presenceSuppliers = new ArrayList<>();
    // The current index of presence supplier, in the case of round-robin mode
    protected int index = 0;

    /**
     * Initialises a new Minecord presence category.
     */
    public PresenceCategory() {}

    /**
     * Initialises a new Minecord presence category with interval and
     * randomness overrides.
     *
     * @param interval number of seconds between presence updates (at least 15s)
     * @param random   true if presences are chosen randomly, else round-robin
     */
    public PresenceCategory(long interval, boolean random)
    {
        this.interval = interval;
        this.random = random;
    }

    /**
     * Returns the next presence supplier in the category.
     *
     * <p>In the case where {@link random} is {@code true}, the presence
     * supplier is chosen randomly.
     *
     * @return presence supplier
     * @throws IllegalStateException if there are no presence suppliers
     */
    public PresenceSupplier getPresenceSupplier() throws IllegalStateException
    {
        // Ensure there exists any presence suppliers
        if (presenceSuppliers.isEmpty()) {
            throw new IllegalStateException("No presence suppliers were provided!");
        }

        // Select the next presence supplier index
        if (isRandom()) {
            index = RANDOM.nextInt(presenceSuppliers.size());
        } else {
            index = (index + 1) % presenceSuppliers.size();
        }

        // Return the presence supplier at the chosen index
        return presenceSuppliers.get(index);
    }

    /**
     * Returns a mutable list of all presence suppliers.
     *
     * @return mutable list of presence suppliers
     */
    public List<PresenceSupplier> getPresenceSuppliers()
    {
        return presenceSuppliers;
    }

    /**
     * Adds one or more presence suppliers.
     *
     * @param presence  presence supplier
     * @param presences zero or more additional presence suppliers
     * @return {@code this} for chaining
     */
    public PresenceCategory addPresenceSuppliers(PresenceSupplier presence, PresenceSupplier... presences)
    {
        this.presenceSuppliers.add(presence);
        if (presences != null) {
            this.presenceSuppliers.addAll(List.of(presences));
        }
        return this;
    }

    /**
     * Clears all presence suppliers.
     */
    public void clearPresenceSuppliers()
    {
        presenceSuppliers.clear();
    }

    /**
     * Returns the number of seconds between presence updates.
     *
     * @return number of seconds between presence updates
     */
    public long getInterval()
    {
        return interval;
    }

    /**
     * Sets the interval between presence updates.
     *
     * @param interval number of seconds between presence updates (at least 15s)
     * @return {@code this} for chaining
     */
    public PresenceCategory setInterval(long interval)
    {
        this.interval = interval;
        return this;
    }

    /**
     * Returns whether presence suppliers are chosen randomly, else round-robin.
     *
     * @return true if suppliers should be chosen randomly, else round-robin
     */
    public boolean isRandom()
    {
        return random;
    }

    /**
     * Sets whether presence suppliers are chosen randomly, else round-robin.
     *
     * @param random true if suppliers should be chosen randomly, else round-robin
     * @return {@code this} for chaining
     */
    public PresenceCategory setRandom(boolean random)
    {
        this.random = random;
        return this;
    }
}

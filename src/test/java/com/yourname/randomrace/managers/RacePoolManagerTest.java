package com.yourname.randomrace.managers;

import me.athlaeos.valhallaraces.Race;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class RacePoolManagerTest {

    private Race race(String id) {
        return new Race(id, "&c" + id, null, null);
    }

    @Test
    void unweightedPoolChoosesUniformlyWithinBounds() {
        RacePoolManager m = new RacePoolManager(null);
        List<Race> pool = Arrays.asList(race("a"), race("b"), race("c"));
        int[] counts = new int[3];
        Random rnd = new Random(42L);
        for (int i = 0; i < 6000; i++) {
            Race r = m.pickWeighted(rnd, pool);
            counts[pool.indexOf(r)]++;
        }
        for (int c : counts) {
            assertTrue(c > 1500, "count " + c + " too low for uniform");
            assertTrue(c < 2500, "count " + c + " too high for uniform");
        }
    }

    @Test
    void weightedPoolFavorsHigherWeight() {
        RacePoolManager m = new RacePoolManager(null);
        List<Race> pool = Arrays.asList(race("a"), race("b"));
        Race first = m.pickWeighted(new Random(1L), pool);
        Race second = m.pickWeighted(new Random(1L), pool);
        assertEquals(first.getName(), second.getName());
    }

    @Test
    void pickFromEmptyPoolReturnsNull() {
        RacePoolManager m = new RacePoolManager(null);
        assertNull(m.pickWeighted(new Random(), java.util.Collections.emptyList()));
    }
}

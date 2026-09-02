package com.yourname.randomrace.managers;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import static org.junit.jupiter.api.Assertions.*;

public class WeightedPickerTest {

    @Test
    void uniformPoolPicksAllWithinBounds() {
        List<String> items = Arrays.asList("a", "b", "c");
        ToDoubleFunction<String> w = s -> 1.0;
        int[] counts = new int[3];
        Random rnd = new Random(7L);
        for (int i = 0; i < 6000; i++) {
            counts[items.indexOf(WeightedPicker.pick(rnd, items, w))]++;
        }
        for (int c : counts) {
            assertTrue(c > 1500 && c < 2500, "unexpected count " + c);
        }
    }

    @Test
    void weightedPoolFavorsHigherWeight() {
        List<String> items = Arrays.asList("a", "b");
        ToDoubleFunction<String> w = s -> s.equals("a") ? 9.0 : 1.0;
        Random rnd = new Random(3L);
        int a = 0;
        for (int i = 0; i < 1000; i++) {
            if ("a".equals(WeightedPicker.pick(rnd, items, w))) a++;
        }
        assertTrue(a > 500, "weighted count " + a + " should favor 'a'");
    }

    @Test
    void emptyPoolReturnsNull() {
        assertNull(WeightedPicker.pick(new Random(), Arrays.asList(), s -> 1.0));
        assertNull(WeightedPicker.pick(new Random(), null, s -> 1.0));
    }
}

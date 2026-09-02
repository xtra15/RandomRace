package com.yourname.randomrace.managers;

import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;

public final class WeightedPicker {
    private WeightedPicker() {}

    public static <T> T pick(Random random, List<T> items, ToDoubleFunction<T> weight) {
        if (items == null || items.isEmpty()) return null;
        double total = 0;
        for (T item : items) total += Math.max(0.0, weight.applyAsDouble(item));
        if (total <= 0) return items.get(0);
        double roll = random.nextDouble() * total;
        double cumulative = 0;
        for (T item : items) {
            cumulative += Math.max(0.0, weight.applyAsDouble(item));
            if (roll < cumulative) return item;
        }
        return items.get(items.size() - 1);
    }
}

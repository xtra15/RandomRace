package com.yourname.randomrace.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundUtil {
    private SoundUtil() {}

    public static void play(Player p, Sound s) {
        if (p == null || !p.isOnline()) return;
        p.playSound(p.getLocation(), s, 1.0f, 1.0f);
    }

    public static void play(Player p, Sound s, float pitch) {
        if (p == null || !p.isOnline()) return;
        p.playSound(p.getLocation(), s, 1.0f, pitch);
    }
}

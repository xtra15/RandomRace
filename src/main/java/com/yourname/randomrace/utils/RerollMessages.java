package com.yourname.randomrace.utils;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.managers.RerollService;
import org.bukkit.entity.Player;

public final class RerollMessages {
    private RerollMessages() {
    }

    public static void sendRerollsLeft(Player p, RandomRacePlugin plugin) {
        RerollService s = plugin.getRerollService();
        int rr = s.raceRerolls(p.getUniqueId());
        int cr = s.classRerolls(p.getUniqueId());
        String msg = plugin.getConfig().getString("messages.rerolls-left",
                "&7You have &e{rrace}&7 race reroll{race_plural} and &e{cclass}&7 class reroll{class_plural} left.");
        msg = MessageUtil.replace("{rrace}", String.valueOf(rr), msg);
        msg = MessageUtil.replace("{race_plural}", plural(rr), msg);
        msg = MessageUtil.replace("{cclass}", String.valueOf(cr), msg);
        msg = MessageUtil.replace("{class_plural}", plural(cr), msg);
        p.sendMessage(MessageUtil.color(msg));
    }

    public static void sendReminder(Player p, RandomRacePlugin plugin) {
        RerollService s = plugin.getRerollService();
        int rr = s.raceRerolls(p.getUniqueId());
        int cr = s.classRerolls(p.getUniqueId());
        String msg = plugin.getConfig().getString("messages.reroll-reminder",
                "&6You have &e{rrace}&6 race reroll{race_plural} and &e{cclass}&6 class reroll{class_plural}. Re-roll with &c/rerollrace&e / &c/rerollclass&e.");
        msg = MessageUtil.replace("{rrace}", String.valueOf(rr), msg);
        msg = MessageUtil.replace("{race_plural}", plural(rr), msg);
        msg = MessageUtil.replace("{cclass}", String.valueOf(cr), msg);
        msg = MessageUtil.replace("{class_plural}", plural(cr), msg);
        p.sendMessage(MessageUtil.color(msg));
    }

    public static String plural(int n) {
        return n == 1 ? "" : "s";
    }
}
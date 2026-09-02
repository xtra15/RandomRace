package com.yourname.randomrace.managers;

import com.yourname.randomrace.RandomRacePlugin;
import me.athlaeos.valhallaraces.Class;
import me.athlaeos.valhallaraces.ClassManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ClassPoolManager {
    private final RandomRacePlugin plugin;
    private final List<Class> pool = new ArrayList<>();

    public ClassPoolManager(RandomRacePlugin plugin) {
        this.plugin = plugin;
    }

    public void refresh() {
        pool.clear();
        if (ClassManager.getRegisteredClasses() == null) return;
        pool.addAll(ClassManager.getRegisteredClasses().values());
    }

    public void poolForTest(List<Class> classes) {
        pool.clear();
        pool.addAll(classes);
    }

    public List<Class> candidatesFor(int group, Player player, String playerRace) {
        List<Class> out = new ArrayList<>();
        for (Class c : pool) {
            if (c.getGroup() != group) continue;
            if (!permissionOk(player, c.getPermissionRequired())) continue;
            if (!passRaceFilter(c.getLimitedToRaces(), playerRace)) continue;
            out.add(c);
        }
        return out;
    }

    public Class pickForGroup(Random random, int group, Player player, String playerRace) {
        List<Class> candidates = candidatesFor(group, player, playerRace);
        if (candidates.isEmpty()) return null;
        return WeightedPicker.pick(random, candidates, this::weightFor);
    }

    public static final int TOTAL_GROUPS = 10;

    public List<Integer> eligibleGroups(Player player, String playerRace, java.util.Set<Integer> skip) {
        List<Integer> out = new ArrayList<>();
        for (int g = 1; g <= TOTAL_GROUPS; g++) {
            if (skip != null && skip.contains(g)) continue;
            if (!candidatesFor(g, player, playerRace).isEmpty()) out.add(g);
        }
        return out;
    }

    public List<Integer> pickRandomGroups(Random random, int count, Player player, String playerRace, java.util.Set<Integer> skip) {
        List<Integer> eligible = eligibleGroups(player, playerRace, skip);
        List<Integer> chosen = new ArrayList<>();
        while (!eligible.isEmpty() && chosen.size() < count) {
            chosen.add(eligible.remove(random.nextInt(eligible.size())));
        }
        return chosen;
    }

    public double weightFor(Class c) {
        if (plugin == null) return 1.0;
        double w = plugin.getConfig().getDouble("class-weights." + c.getName(), 1.0);
        return Math.max(1.0, w);
    }

    static boolean permissionOk(Player player, String required) {
        if (required == null) return true;
        return player != null && player.hasPermission(required);
    }

    static boolean passRaceFilter(Collection<String> limitedTo, String playerRace) {
        if (limitedTo == null || limitedTo.isEmpty()) return true;
        if (playerRace == null) return false;
        return limitedTo.contains(playerRace);
    }
}

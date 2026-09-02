package com.yourname.randomrace.managers;

import com.yourname.randomrace.RandomRacePlugin;
import me.athlaeos.valhallaraces.Race;
import me.athlaeos.valhallaraces.RaceManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RacePoolManager {
    private final RandomRacePlugin plugin;
    private final List<Race> pool = new ArrayList<>();

    public RacePoolManager(RandomRacePlugin plugin) {
        this.plugin = plugin;
    }

    public void refresh() {
        pool.clear();
        if (RaceManager.getRegisteredRaces() == null) return;
        pool.addAll(RaceManager.getRegisteredRaces().values());
    }

    public List<Race> getAvailableRaces(Player player) {
        List<Race> out = new ArrayList<>();
        for (Race r : pool) {
            String perm = r.getPermissionRequired();
            if (perm != null && (player == null || !player.hasPermission(perm))) continue;
            out.add(r);
        }
        return out;
    }

    public Race pickWeighted(Random random, List<Race> available) {
        return WeightedPicker.pick(random, available, this::weightFor);
    }

    public double weightFor(Race r) {
        if (plugin == null) return 1.0;
        double w = plugin.getConfig().getDouble("race-weights." + r.getName(), 1.0);
        return Math.max(1.0, w);
    }

    public Material materialFor(Race r) {
        if (plugin != null) {
            String name = plugin.getConfig().getString("race-materials." + r.getName());
            if (name != null) {
                Material m = Material.matchMaterial(name);
                if (m != null) return m;
            }
        }
        ItemStack icon = r.getIcon();
        if (icon != null && icon.getType() != null && icon.getType() != Material.AIR) {
            return icon.getType();
        }
        return Material.PAPER;
    }
}

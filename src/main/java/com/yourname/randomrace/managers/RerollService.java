package com.yourname.randomrace.managers;

import com.yourname.randomrace.RandomRacePlugin;
import me.athlaeos.valhallaraces.ClassManager;
import me.athlaeos.valhallaraces.RaceManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RerollService {
    private final PlayerDataManager data;
    private final int defaultClassSlots;

    public RerollService(PlayerDataManager data, int defaultClassSlots) {
        this.data = data;
        this.defaultClassSlots = defaultClassSlots;
    }

    public RerollService(RandomRacePlugin plugin) {
        this(plugin.getPlayerDataManager(), plugin.getConfig().getInt("classes-count", 3));
    }

    public boolean trySpendRaceReroll(UUID uuid) {
        if (data.getRaceRerolls(uuid) <= 0) return false;
        data.setRaceRerolls(uuid, data.getRaceRerolls(uuid) - 1);
        return true;
    }

    public boolean trySpendClassReroll(UUID uuid) {
        if (data.getClassRerolls(uuid) <= 0) return false;
        data.setClassRerolls(uuid, data.getClassRerolls(uuid) - 1);
        return true;
    }

    public int raceRerolls(UUID uuid) {
        return data.getRaceRerolls(uuid);
    }

    public int classRerolls(UUID uuid) {
        return data.getClassRerolls(uuid);
    }

    public boolean hasAnyRerolls(UUID uuid) {
        return raceRerolls(uuid) > 0 || classRerolls(uuid) > 0;
    }

    public int classSlots(UUID uuid) {
        int slots = data.getClassSlots(uuid);
        return slots < 0 ? defaultClassSlots : slots;
    }

    public void addRaceRerolls(UUID uuid, int amount) {
        data.setRaceRerolls(uuid, Math.max(0, data.getRaceRerolls(uuid) + amount));
    }

    public void setRaceRerolls(UUID uuid, int amount) {
        data.setRaceRerolls(uuid, Math.max(0, amount));
    }

    public void addClassRerolls(UUID uuid, int amount) {
        data.setClassRerolls(uuid, Math.max(0, data.getClassRerolls(uuid) + amount));
    }

    public void setClassRerolls(UUID uuid, int amount) {
        data.setClassRerolls(uuid, Math.max(0, amount));
    }

    public void setClassSlots(UUID uuid, int slots) {
        data.setClassSlots(uuid, Math.max(1, Math.min(10, slots)));
    }

    public boolean isRaceClaimed(Player p) {
        return RaceManager.getRace(p) != null;
    }

    public boolean areClassesFull(Player p) {
        return ClassManager.getClasses(p).size() >= classSlots(p.getUniqueId());
    }
}
package com.yourname.randomrace.managers;

import com.yourname.randomrace.RandomRacePlugin;
import me.athlaeos.valhallaraces.Race;
import me.athlaeos.valhallaraces.RaceManager;
import org.bukkit.entity.Player;

public class AssignmentManager {
    private final RandomRacePlugin plugin;

    public AssignmentManager() {
        this.plugin = null;
    }

    public AssignmentManager(RandomRacePlugin plugin) {
        this.plugin = plugin;
    }

    public void assign(Player p, Race race) {
        RaceManager.setRace(p, race);
        if (plugin != null) {
            plugin.getPlayerDataManager().setRace(p.getUniqueId(), race == null ? null : race.getName(), p.getName());
        }
    }

    public void clear(Player p) {
        RaceManager.setRace(p, null);
        if (plugin != null) {
            plugin.getPlayerDataManager().setRace(p.getUniqueId(), null, p.getName());
        }
    }

    public boolean hasRace(Player p) {
        return RaceManager.getRace(p) != null;
    }
}

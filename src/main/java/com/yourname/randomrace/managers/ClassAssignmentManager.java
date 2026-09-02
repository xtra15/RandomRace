package com.yourname.randomrace.managers;

import com.yourname.randomrace.RandomRacePlugin;
import me.athlaeos.valhallaraces.Class;
import me.athlaeos.valhallaraces.ClassManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClassAssignmentManager {
    private final RandomRacePlugin plugin;

    public ClassAssignmentManager() {
        this.plugin = null;
    }

    public ClassAssignmentManager(RandomRacePlugin plugin) {
        this.plugin = plugin;
    }

    public void assign(Player p, Collection<Class> classes) {
        ClassManager.setClasses(p, classes);
        if (plugin != null) {
            plugin.getPlayerDataManager().setClasses(p.getUniqueId(), keysOf(classes), p.getName());
        }
    }

    public void replaceAll(Player p, Collection<Class> classes) {
        assign(p, classes);
    }

    public void clear(Player p) {
        ClassManager.setClasses(p, Collections.emptyList());
        if (plugin != null) {
            plugin.getPlayerDataManager().setClasses(p.getUniqueId(), Collections.emptyList(), p.getName());
        }
    }

    public int count(Player p) {
        return ClassManager.getClasses(p).size();
    }

    private List<String> keysOf(Collection<Class> classes) {
        List<String> keys = new ArrayList<>();
        if (classes != null) {
            for (Class c : classes) keys.add(c.getName());
        }
        return keys;
    }
}

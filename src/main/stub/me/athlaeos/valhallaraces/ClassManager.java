package me.athlaeos.valhallaraces;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClassManager {
    private static Map<String, Class> registeredClasses = new HashMap<>();

    public static Map<String, Class> getRegisteredClasses() { return registeredClasses; }
    public static Map<Integer, Class> getClasses(Player p) { return new HashMap<>(); }
    public static void setClasses(Player p, Collection<Class> classes) { }
}

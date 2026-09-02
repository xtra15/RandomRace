package me.athlaeos.valhallaraces;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RaceManager {
    private static Map<String, Race> registeredRaces = new HashMap<>();

    public static Map<String, Race> getRegisteredRaces() { return registeredRaces; }
    public static Race getRace(Player p) { return null; }
    public static void setRace(Player p, Race race) { }
}

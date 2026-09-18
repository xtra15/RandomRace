package com.yourname.randomrace.managers;

import com.yourname.randomrace.RandomRacePlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataManager {
    private final RandomRacePlugin plugin;
    private final File file;
    private YamlConfiguration data;

    public PlayerDataManager(RandomRacePlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!file.exists()) {
            plugin.saveResource("playerdata.yml", false);
        }
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public PlayerDataManager(File file) {
        this.plugin = null;
        this.file = file;
        this.data = (file.exists() && file.length() > 0L)
                ? YamlConfiguration.loadConfiguration(file)
                : new YamlConfiguration();
    }

    public boolean hasClaimed(UUID uuid) {
        return data.getLong(uuid + ".claimed", 0L) > 0L;
    }

    public void markClaimed(UUID uuid) {
        data.set(uuid + ".claimed", System.currentTimeMillis());
        save();
    }

    public void setRace(UUID uuid, String raceKey, String playerName) {
        data.set(uuid + ".race", raceKey);
        data.set(uuid + ".name", playerName);
        save();
    }

    public String getRace(UUID uuid) {
        return data.getString(uuid + ".race");
    }

    public void setClasses(UUID uuid, java.util.Collection<String> classKeys, String playerName) {
        data.set(uuid + ".classes", new java.util.ArrayList<>(classKeys));
        data.set(uuid + ".name", playerName);
        save();
    }

    public java.util.List<String> getClasses(UUID uuid) {
        java.util.List<String> list = data.getStringList(uuid + ".classes");
        return list == null ? new java.util.ArrayList<>() : list;
    }

    public int getRaceRerolls(UUID uuid) {
        return data.getInt(uuid + ".race-rerolls", 0);
    }

    public void addRaceRerolls(UUID uuid, int amount) {
        data.set(uuid + ".race-rerolls", getRaceRerolls(uuid) + amount);
        save();
    }

    public void setRaceRerolls(UUID uuid, int amount) {
        data.set(uuid + ".race-rerolls", amount);
        save();
    }

    public int getClassRerolls(UUID uuid) {
        return data.getInt(uuid + ".class-rerolls", 0);
    }

    public void addClassRerolls(UUID uuid, int amount) {
        data.set(uuid + ".class-rerolls", getClassRerolls(uuid) + amount);
        save();
    }

    public void setClassRerolls(UUID uuid, int amount) {
        data.set(uuid + ".class-rerolls", amount);
        save();
    }

    public int getClassSlots(UUID uuid) {
        return data.getInt(uuid + ".class-slots", -1);
    }

    public void setClassSlots(UUID uuid, int slots) {
        data.set(uuid + ".class-slots", slots);
        save();
    }

    public boolean hasRecord(UUID uuid) {
        return data.contains(uuid.toString());
    }

    public void markJoined(UUID uuid) {
        data.set(uuid + ".joined", System.currentTimeMillis());
        save();
    }

    public void migrateByName(Player p) {
        migrateByName(p.getUniqueId(), p.getName());
    }

    void migrateByName(UUID uuid, String name) {
        if (hasRecord(uuid)) return;
        boolean migrated = false;
        for (String key : data.getKeys(false)) {
            if (key.equals(uuid.toString())) continue;
            String stored = data.getString(key + ".name");
            if (stored == null || !stored.equalsIgnoreCase(name)) continue;
            int rr = data.getInt(key + ".race-rerolls", 0);
            int cr = data.getInt(key + ".class-rerolls", 0);
            int slots = data.getInt(key + ".class-slots", -1);
            if (rr > 0) data.set(uuid + ".race-rerolls", rr);
            if (cr > 0) data.set(uuid + ".class-rerolls", cr);
            if (slots >= 1) data.set(uuid + ".class-slots", slots);
            data.set(uuid + ".joined", data.getLong(key + ".joined", System.currentTimeMillis()));
            data.set(key, null);
            migrated = true;
            break;
        }
        if (migrated) save();
    }

    public void clearClaim(UUID uuid) {
        data.set(uuid + ".claimed", null);
        data.set(uuid + ".race", null);
        data.set(uuid + ".classes", null);
        save();
    }

    public String getRaceSlot(UUID uuid, int slot) {
        return data.getString(uuid + ".raceslots." + slot);
    }

    public void setRaceSlot(UUID uuid, int slot, String raceKey) {
        data.set(uuid + ".raceslots." + slot, raceKey);
        save();
    }

    public java.util.Map<Integer, String> getRaceSlots(UUID uuid) {
        java.util.Map<Integer, String> map = new java.util.LinkedHashMap<>();
        ConfigurationSection section = data.getConfigurationSection(uuid + ".raceslots");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                try {
                    map.put(Integer.parseInt(key), section.getString(key));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return map;
    }

    public long getLoadCooldown(UUID uuid) {
        return data.getLong(uuid + ".race-slot-cooldown", 0L);
    }

    public void setLoadCooldown(UUID uuid, long endEpochMillis) {
        data.set(uuid + ".race-slot-cooldown", endEpochMillis);
        save();
    }

    public UUID resolveOffline(String name) {
        for (String key : data.getKeys(false)) {
            String stored = data.getString(key + ".name");
            if (stored != null && stored.equalsIgnoreCase(name)) {
                try {
                    return UUID.fromString(key);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return null;
    }

    private void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            if (plugin != null) {
                plugin.getLogger().warning("Could not save playerdata.yml: " + e.getMessage());
            }
        }
    }
}
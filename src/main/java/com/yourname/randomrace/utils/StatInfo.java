package com.yourname.randomrace.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class StatInfo {
    private StatInfo() {}

    private static final Set<String> FLAT_STATS = Set.of(
        "HEALTH_BONUS", "HEALTH_MULTIPLIER_BONUS", "LUCK_BONUS", "BLOCK_REACH",
        "STEP_HEIGHT", "SAFE_FALLING_DISTANCE", "FISHING_LUCK", "FARMING_LUCK",
        "MINING_LUCK", "BLASTING_LUCK", "DIGGING_LUCK", "DIGGING_ARCHAEOLOGY_LUCK",
        "WOODCUTTING_LUCK", "ATTACK_DAMAGE_BONUS", "ATTACK_REACH_BONUS",
        "RANGED_INACCURACY", "CROSSBOW_MAGAZINE", "IMMUNITY_FRAME_BONUS",
        "BLEED_DURATION", "STUN_DURATION_BONUS", "FOOD_BONUS_VEGETABLE",
        "FOOD_BONUS_SEASONING", "FOOD_BONUS_ALCOHOLIC", "FOOD_BONUS_BEVERAGE",
        "FOOD_BONUS_SPOILED", "FOOD_BONUS_SEAFOOD", "FOOD_BONUS_MAGICAL",
        "FOOD_BONUS_SWEET", "FOOD_BONUS_GRAIN", "FOOD_BONUS_FRUIT",
        "FOOD_BONUS_NUTS", "FOOD_BONUS_DAIRY", "FOOD_BONUS_MEAT",
        "FOOD_BONUS_FATS", "SMITHING_QUALITY_GENERAL", "SMITHING_QUALITY_WOOD",
        "SMITHING_QUALITY_LEATHER", "SMITHING_QUALITY_STONE",
        "SMITHING_QUALITY_CHAINMAIL", "SMITHING_QUALITY_GOLD",
        "SMITHING_QUALITY_IRON", "SMITHING_QUALITY_DIAMOND",
        "SMITHING_QUALITY_NETHERITE", "SMITHING_QUALITY_BOW",
        "SMITHING_QUALITY_CROSSBOW", "SMITHING_QUALITY_PRISMARINE",
        "SMITHING_QUALITY_ENDERIC", "ALCHEMY_QUALITY_GENERAL",
        "ALCHEMY_QUALITY_DEBUFF", "ALCHEMY_QUALITY_BUFF",
        "ENCHANTING_QUALITY", "ENCHANTING_QUALITY_ANVIL",
        "ENCHANTING_REFUND_AMOUNT", "TOUGHNESS_BONUS", "PARRY_COOLDOWN",
        "TOUGHNESS", "ARMOR_TOTAL", "TOTAL_LIGHT_ARMOR", "LIGHT_ARMOR",
        "TOTAL_HEAVY_ARMOR", "HEAVY_ARMOR", "TOTAL_WEIGHTLESS_ARMOR",
        "WEIGHTLESS_ARMOR"
    );

    private static final Set<String> INVERTED_STATS = Set.of(
        "RANGED_INACCURACY", "PARRY_VULNERABLE_DURATION", "PARRY_SELF_DEBUFF_DURATION"
    );

    private static final Map<String, String> STAT_LABELS = buildLabels();

    private static Map<String, String> buildLabels() {
        Map<String, String> map = new HashMap<>();
        map.put("MINING_EXP_GAIN", "Mining Experience");
        map.put("SMITHING_EXP_GAIN_GENERAL", "Smithing Experience");
        map.put("HEAVY_WEAPONS_EXP_GAIN", "Heavy Weapons Experience");
        map.put("EXPLOSION_RESISTANCE", "Explosion Resistance");
        map.put("MELEE_RESISTANCE", "Melee Resistance");
        map.put("HEALTH_BONUS", "Maximum Health");
        map.put("MOVEMENT_SPEED_BONUS", "Movement Speed");
        map.put("SCALE", "Size");
        map.put("GLOBAL_EXP_GAIN", "Skill Experience");
        map.put("DAMAGE_RESISTANCE", "Damage Resistance");
        map.put("COOLDOWN_REDUCTION", "Cooldown Reduction");
        map.put("ENCHANTING_EXP_GAIN", "Enchanting Experience");
        map.put("ARCHERY_EXP_GAIN", "Archery Experience");
        map.put("LIGHT_WEAPONS_EXP_GAIN", "Light Weapons Experience");
        map.put("MAGIC_RESISTANCE", "Magic Resistance");
        map.put("FALLING_RESISTANCE", "Fall Resistance");
        map.put("UNARMED_DAMAGE_DEALT", "Unarmed Damage");
        map.put("MELEE_DAMAGE_DEALT", "Melee Damage");
        map.put("FIRE_DAMAGE_DEALT", "Fire Damage");
        map.put("FIRE_RESISTANCE", "Fire Resistance");
        map.put("KNOCKBACK_RESISTANCE", "Knockback Resistance");
        map.put("HEALING_BONUS", "Healing");
        map.put("ARMOR_MULTIPLIER_BONUS", "Armor Effectiveness");
        map.put("ARMOR_TOTAL", "Armor");
        map.put("ATTACK_DAMAGE_BONUS", "Attack Damage");
        map.put("ATTACK_SPEED_BONUS", "Attack Speed");
        map.put("LUCK_BONUS", "Luck");
        map.put("BLOCK_REACH", "Block Reach");
        map.put("STEP_HEIGHT", "Step Height");
        map.put("GRAVITY", "Gravity");
        map.put("SAFE_FALLING_DISTANCE", "Safe Fall Distance");
        map.put("FALL_DAMAGE_MULTIPLIER", "Fall Damage");
        map.put("DAMAGE_DEALT", "Damage Dealt");
        map.put("RANGED_DAMAGE_DEALT", "Ranged Damage");
        map.put("VELOCITY_DAMAGE_BONUS", "Velocity Damage");
        map.put("LIGHT_ARMOR_DAMAGE_BONUS", "Light Armor Damage");
        map.put("HEAVY_ARMOR_DAMAGE_BONUS", "Heavy Armor Damage");
        map.put("FIRE_DAMAGE_BONUS", "Fire Damage");
        map.put("EXPLOSION_DAMAGE_BONUS", "Explosion Damage");
        map.put("POISON_DAMAGE_BONUS", "Poison Damage");
        map.put("MAGIC_DAMAGE_BONUS", "Magic Damage");
        map.put("LIGHTNING_DAMAGE_BONUS", "Lightning Damage");
        map.put("FREEZING_DAMAGE_BONUS", "Freezing Damage");
        map.put("RADIANT_DAMAGE_BONUS", "Radiant Damage");
        map.put("NECROTIC_DAMAGE_BONUS", "Necrotic Damage");
        map.put("BLUDGEONING_DAMAGE_BONUS", "Bludgeoning Damage");
        map.put("EXPLOSION_DAMAGE_DEALT", "Explosion Damage");
        map.put("POISON_DAMAGE_DEALT", "Poison Damage");
        map.put("BLUDGEONING_DAMAGE_DEALT", "Bludgeoning Damage");
        map.put("MAGIC_DAMAGE_DEALT", "Magic Damage");
        map.put("LIGHTNING_DAMAGE_DEALT", "Lightning Damage");
        map.put("FREEZING_DAMAGE_DEALT", "Freezing Damage");
        map.put("RADIANT_DAMAGE_DEALT", "Radiant Damage");
        map.put("NECROTIC_DAMAGE_DEALT", "Necrotic Damage");
        map.put("POWER_ATTACK_DAMAGE_MULTIPLIER", "Power Attack Damage");
        map.put("POWER_ATTACK_RADIUS", "Power Attack Radius");
        map.put("POWER_ATTACK_DAMAGE_FRACTION", "Power Attack Fraction");
        map.put("ATTACK_REACH_BONUS", "Attack Reach");
        map.put("ATTACK_REACH_MULTIPLIER", "Attack Reach");
        map.put("RANGED_INACCURACY", "Ranged Accuracy");
        map.put("RANGED_VELOCITY_BONUS", "Ranged Velocity");
        map.put("KNOCKBACK_BONUS", "Knockback");
        map.put("IMMUNITY_FRAME_BONUS", "Immunity Frames");
        map.put("IMMUNITY_FRAME_MULTIPLIER", "Immunity Frames");
        map.put("BLEED_CHANCE", "Bleed Chance");
        map.put("BLEED_DAMAGE", "Bleed Damage");
        map.put("BLEED_DURATION", "Bleed Duration");
        map.put("DODGE_CHANCE", "Dodge Chance");
        map.put("REFLECT_CHANCE", "Reflect Chance");
        map.put("REFLECT_FRACTION", "Reflect Fraction");
        map.put("DISMOUNT_CHANCE", "Dismount Chance");
        map.put("STUN_CHANCE", "Stun Chance");
        map.put("STUN_DURATION_BONUS", "Stun Duration");
        map.put("CRIT_CHANCE", "Critical Chance");
        map.put("CRIT_DAMAGE", "Critical Damage");
        map.put("CROSSBOW_MAGAZINE", "Crossbow Magazine");
        map.put("DAMAGE_RESISTANCE", "Damage Resistance");
        map.put("PROJECTILE_RESISTANCE", "Projectile Resistance");
        map.put("BLUDGEONING_RESISTANCE", "Bludgeoning Resistance");
        map.put("POISON_RESISTANCE", "Poison Resistance");
        map.put("FREEZING_RESISTANCE", "Freezing Resistance");
        map.put("LIGHTNING_RESISTANCE", "Lightning Resistance");
        map.put("RADIANT_RESISTANCE", "Radiant Resistance");
        map.put("NECROTIC_RESISTANCE", "Necrotic Resistance");
        map.put("STUN_RESISTANCE", "Stun Resistance");
        map.put("BLEED_RESISTANCE", "Bleed Resistance");
        map.put("CRIT_CHANCE_RESISTANCE", "Crit Chance Resistance");
        map.put("CRIT_DAMAGE_RESISTANCE", "Crit Damage Resistance");
        map.put("HUNGER_SAVE_CHANCE", "Hunger Save Chance");
        map.put("CRAFTING_TIME_REDUCTION", "Crafting Time");
        map.put("COOKING_SPEED_BONUS", "Cooking Speed");
        map.put("AMMO_SAVE_CHANCE", "Ammo Save Chance");
        map.put("DURABILITY_BONUS", "Durability");
        map.put("ENTITY_DROPS", "Entity Drops");
        map.put("ENTITY_DROP_LUCK", "Entity Drop Luck");
        map.put("JUMP_HEIGHT_MULTIPLIER", "Jump Height");
        map.put("JUMPS_BONUS", "Extra Jumps");
        map.put("SNEAK_MOVEMENT_SPEED_BONUS", "Sneak Speed");
        map.put("SPRINT_MOVEMENT_SPEED_BONUS", "Sprint Speed");
        map.put("DIG_SPEED", "Dig Speed");
        map.put("BLOCK_SPECIFIC_DIG_SPEED", "Block Dig Speed");
        map.put("FISHING_LUCK", "Fishing Luck");
        map.put("FISHING_SPEED_MULTIPLIER", "Fishing Speed");
        map.put("EXPLOSION_RADIUS_MULTIPLIER", "Explosion Radius");
        map.put("TOUGHNESS", "Toughness");
        map.put("ALCHEMY_EXP_GAIN", "Alchemy Experience");
        map.put("BREWING_SPEED_BONUS", "Brewing Speed");
        map.put("POTION_SAVE_CHANCE", "Potion Save Chance");
        map.put("THROW_VELOCITY_BONUS", "Throw Velocity");
        map.put("ENCHANTING_QUALITY", "Enchanting Quality");
        map.put("ENCHANTING_QUALITY_ANVIL", "Anvil Quality");
        map.put("ENCHANTING_AMPLIFY_CHANCE", "Amplify Chance");
        map.put("ENCHANTING_LAPIS_SAVE_CHANCE", "Lapis Save Chance");
        map.put("ENCHANTING_VANILLA_EXP_GAIN", "Vanilla Enchanting Experience");
        map.put("ENCHANTING_REFUND_CHANCE", "Enchant Refund Chance");
        map.put("ENCHANTING_REFUND_AMOUNT", "Enchant Refund Amount");
        map.put("ENCHANTING_EXP_GAIN", "Enchanting Experience");
        map.put("BUTCHERY_DROP_MULTIPLIER", "Butchery Drops");
        map.put("FARMING_DROP_MULTIPLIER", "Farming Drops");
        map.put("FARMING_LUCK", "Farming Luck");
        map.put("FARMING_EXP_GAIN", "Farming Experience");
        map.put("MINING_DROP_MULTIPLIER", "Mining Drops");
        map.put("MINING_LUCK", "Mining Luck");
        map.put("BLASTING_DROP_MULTIPLIER", "Blasting Drops");
        map.put("BLASTING_LUCK", "Blasting Luck");
        map.put("MINING_EXP_GAIN", "Mining Experience");
        map.put("DIGGING_DROP_MULTIPLIER", "Digging Drops");
        map.put("DIGGING_LUCK", "Digging Luck");
        map.put("DIGGING_ARCHAEOLOGY_LUCK", "Archaeology Luck");
        map.put("DIGGING_EXP_GAIN", "Digging Experience");
        map.put("WOODCUTTING_DROP_MULTIPLIER", "Woodcutting Drops");
        map.put("WOODCUTTING_LUCK", "Woodcutting Luck");
        map.put("WOODCUTTING_EXP_GAIN", "Woodcutting Experience");
        map.put("FISHING_EXP_GAIN", "Fishing Experience");
        map.put("LIGHT_ARMOR_EXP_GAIN", "Light Armor Experience");
        map.put("HEAVY_ARMOR_EXP_GAIN", "Heavy Armor Experience");
        map.put("LIGHT_WEAPONS_EXP_GAIN", "Light Weapons Experience");
        map.put("HEAVY_WEAPONS_EXP_GAIN", "Heavy Weapons Experience");
        return map;
    }

    public static List<String> formatRaceStats(String raceKey, String name, File valhallaRacesFolder) {
        YamlConfiguration config = load("races.yml", valhallaRacesFolder);
        if (config == null) return empty(name);
        return format("races", raceKey, name, config);
    }

    public static List<String> formatClassStats(String classKey, String name, File valhallaRacesFolder) {
        YamlConfiguration config = load("classes.yml", valhallaRacesFolder);
        if (config == null) return empty(name);
        return format("classes", classKey, name, config);
    }

    public static List<String> formatCombinedStats(String raceKey, Collection<String> classKeys, File valhallaRacesFolder) {
        List<String> out = new ArrayList<>();
        out.add(MessageUtil.color("&e&lTotal Attributes"));
        Map<String, Double> totals = new HashMap<>();
        YamlConfiguration races = load("races.yml", valhallaRacesFolder);
        YamlConfiguration classes = load("classes.yml", valhallaRacesFolder);
        if (raceKey != null && races != null) {
            accumulate(races, "races." + raceKey + ".stat_buffs", totals);
        }
        if (classes != null && classKeys != null) {
            for (String ck : classKeys) {
                accumulate(classes, "classes." + ck + ".stat_buffs", totals);
            }
        }
        if (totals.isEmpty()) {
            out.add(MessageUtil.color("&7No stat data found."));
            return out;
        }
        out.addAll(formatMap(totals));
        return out;
    }

    private static void accumulate(YamlConfiguration config, String path, Map<String, Double> totals) {
        ConfigurationSection s = config.getConfigurationSection(path);
        if (s == null) return;
        for (String statKey : s.getKeys(false)) {
            Object raw = s.get(statKey);
            if (!(raw instanceof Number)) continue;
            totals.merge(statKey, ((Number) raw).doubleValue(), Double::sum);
        }
    }

    private static List<String> formatMap(Map<String, Double> totals) {
        List<String> out = new ArrayList<>();
        List<String> buffs = new ArrayList<>();
        List<String> debuffs = new ArrayList<>();
        for (Map.Entry<String, Double> e : totals.entrySet()) {
            if (e.getValue() == 0) continue;
            String line = lineFor(e.getKey(), e.getValue());
            if (isBuff(e.getKey(), e.getValue())) buffs.add(line);
            else debuffs.add(line);
        }
        if (buffs.isEmpty() && debuffs.isEmpty()) {
            out.add(MessageUtil.color("&7No stat data found."));
            return out;
        }
        if (!buffs.isEmpty()) {
            out.add(MessageUtil.color("&a&lBlessings"));
            out.addAll(buffs);
        }
        if (!debuffs.isEmpty()) {
            out.add(MessageUtil.color("&c&lCurses"));
            out.addAll(debuffs);
        }
        return out;
    }

    private static YamlConfiguration load(String file, File folder) {
        File f = new File(folder, file);
        if (!f.exists()) return null;
        return YamlConfiguration.loadConfiguration(f);
    }

    private static List<String> empty(String name) {
        List<String> out = new ArrayList<>();
        out.add(MessageUtil.color("&e&l" + name));
        out.add(MessageUtil.color("&7No stat data found."));
        return out;
    }

    private static List<String> format(String section, String key, String name, YamlConfiguration config) {
        List<String> out = new ArrayList<>();
        out.add(MessageUtil.color("&e&l" + name));
        ConfigurationSection s = config.getConfigurationSection(section + "." + key + ".stat_buffs");
        if (s == null || s.getKeys(false).isEmpty()) {
            out.add(MessageUtil.color("&7No stat data found."));
            return out;
        }
        List<String> buffs = new ArrayList<>();
        List<String> debuffs = new ArrayList<>();
        for (String statKey : s.getKeys(false)) {
            Object raw = s.get(statKey);
            if (!(raw instanceof Number)) continue;
            double value = ((Number) raw).doubleValue();
            String line = lineFor(statKey, value);
            if (isBuff(statKey, value)) buffs.add(line);
            else debuffs.add(line);
        }
        if (!buffs.isEmpty()) {
            out.add(MessageUtil.color("&a&lBlessings"));
            out.addAll(buffs);
        }
        if (!debuffs.isEmpty()) {
            out.add(MessageUtil.color("&c&lCurses"));
            out.addAll(debuffs);
        }
        return out;
    }

    private static String lineFor(String statKey, double value) {
        boolean flat = FLAT_STATS.contains(statKey);
        String label = labelFor(statKey);
        double mag = flat ? Math.abs(value) : Math.abs(value) * 100;
        String magStr = flat ? formatNumber(mag, 1) : formatNumber(mag, 0) + "%";
        boolean buff = isBuff(statKey, value);
        String sign = value >= 0 ? "+" : "-";
        String color = buff ? "&a" : "&c";
        return MessageUtil.color("&f- " + color + sign + magStr + " &f" + label);
    }

    private static String formatNumber(double v, int decimals) {
        if (decimals <= 0) return String.valueOf((long) Math.round(v));
        String s = String.format(java.util.Locale.ROOT, "%." + decimals + "f", v);
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    private static boolean isBuff(String statKey, double value) {
        boolean buff = value > 0;
        if (INVERTED_STATS.contains(statKey)) buff = !buff;
        return buff;
    }

    private static String labelFor(String statKey) {
        return STAT_LABELS.getOrDefault(statKey, statKey.replace("_", " ").toLowerCase());
    }
}

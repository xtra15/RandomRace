package com.yourname.randomrace.gui;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.managers.ClassAssignmentManager;
import com.yourname.randomrace.managers.ClassPoolManager;
import com.yourname.randomrace.utils.MessageUtil;
import com.yourname.randomrace.utils.RerollMessages;
import com.yourname.randomrace.utils.SoundUtil;
import com.yourname.randomrace.utils.StatInfo;
import me.athlaeos.valhallaraces.Class;
import me.athlaeos.valhallaraces.Race;
import me.athlaeos.valhallaraces.RaceManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassSpinAnimation implements Listener {
    private static final int INV_SIZE = 27;

    private static final int TOP_CELL = 4;
    private static final int MID_CELL = 13;
    private static final int BOTTOM_CELL = 22;

    private static final int MARKER_TOP = 3;
    private static final int MARKER_MID = 12;
    private static final int MARKER_BOTTOM = 21;

    private static final int[][] PHASES = {{2, 5}, {4, 3}, {8, 2}};
    private static final int TOTAL_TICKS;
    static {
        int t = 0;
        for (int[] p : PHASES) t += p[1];
        TOTAL_TICKS = t;
    }
    private static final int WINNER_INDEX = TOTAL_TICKS + 1;

    private final RandomRacePlugin plugin;
    private final Player player;
    private final Map<Integer, Class> winners;
    private final Map<Integer, Class> keep;
    private final ClassPoolManager poolManager;
    private final ClassAssignmentManager assignmentManager;
    private final Map<Integer, String> groupNames = new LinkedHashMap<>();
    private Inventory inventory;
    private BukkitTask task;
    private boolean completed = false;

    public ClassSpinAnimation(RandomRacePlugin plugin, Player player, Map<Integer, Class> winners) {
        this(plugin, player, winners, java.util.Collections.emptyMap());
    }

    public ClassSpinAnimation(RandomRacePlugin plugin, Player player, Map<Integer, Class> winners, Map<Integer, Class> keep) {
        this.plugin = plugin;
        this.player = player;
        this.winners = winners;
        this.keep = keep == null ? java.util.Collections.emptyMap() : keep;
        this.assignmentManager = new ClassAssignmentManager(plugin);
        this.poolManager = plugin.getClassPoolManager();
        loadGroupNames();
    }

    private void loadGroupNames() {
        for (int g = 1; g <= 10; g++) {
            groupNames.put(g, plugin.getConfig().getString("groups." + g, "Group " + g));
        }
    }

    public void start() {
        inventory = plugin.getServer().createInventory(null, INV_SIZE, MessageUtil.color("&8Random Classes"));
        buildBackground();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        player.openInventory(inventory);
        SoundUtil.play(player, Sound.BLOCK_CHEST_OPEN);
        rollNext(winners.keySet().iterator());
    }
    private void buildBackground() {
        ItemStack black = pane(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack red = pane(Material.RED_STAINED_GLASS_PANE, " ");
        ItemStack green = pane(Material.LIME_STAINED_GLASS_PANE, " ");
        for (int slot = 0; slot < INV_SIZE; slot++) {
            inventory.setItem(slot, black);
        }
        inventory.setItem(MARKER_TOP, red);
        inventory.setItem(MARKER_BOTTOM, red);
        inventory.setItem(MARKER_MID, green);
    }

    private ItemStack pane(Material material, String name) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtil.color(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    private void rollNext(final java.util.Iterator<Integer> groups) {
        if (!groups.hasNext()) {
            finish();
            return;
        }
        final int group = groups.next();
        final Class winner = winners.get(group);
        final List<Class> candidates = buildCandidates(winner, group);
        setTitle(group);
        SoundUtil.play(player, Sound.BLOCK_CHEST_OPEN);
        final int[] tick = {0};
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            inventory.setItem(TOP_CELL, classItem(candidates, tick[0], winner));
            inventory.setItem(MID_CELL, classItem(candidates, tick[0] + 1, winner));
            inventory.setItem(BOTTOM_CELL, classItem(candidates, tick[0] + 2, winner));
            float progress = Math.min(1f, (float) tick[0] / TOTAL_TICKS);
            float pitch = 0.5f + progress * 1.5f;
            SoundUtil.play(player, Sound.BLOCK_NOTE_BLOCK_HARP, pitch);
            tick[0]++;
            if (tick[0] > TOTAL_TICKS) {
                task.cancel();
                inventory.setItem(TOP_CELL, classItem(candidates, tick[0], winner));
                inventory.setItem(MID_CELL, classItem(candidates, tick[0] + 1, winner));
                inventory.setItem(BOTTOM_CELL, classItem(candidates, tick[0] + 2, winner));
                SoundUtil.play(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
                sendGroupMessage(group, winner);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> rollNext(groups), 12L);
            }
        }, 10L, 2L);
    }

    private List<Class> buildCandidates(Class winner, int group) {
        List<Class> candidates = poolManager.candidatesFor(group, player, playerRace());
        if (candidates.isEmpty()) candidates.add(winner);
        return candidates;
    }

    private ItemStack classItem(List<Class> candidates, int lane, Class winner) {
        if (lane == WINNER_INDEX) {
            return namedClassItem(winner);
        }
        return namedClassItem(candidates.get(lane % candidates.size()));
    }

    private ItemStack namedClassItem(Class c) {
        ItemStack item = new ItemStack(configuredMaterial(c), 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtil.color(c.getDisplayName()));
            item.setItemMeta(meta);
        }
        return item;
    }

    private String playerRace() {
        Race r = RaceManager.getRace(player);
        return r == null ? null : r.getName();
    }

    private Material configuredMaterial(Class c) {
        String name = c != null ? plugin.getConfig().getString("class-materials." + c.getName()) : null;
        if (name != null) {
            Material m = Material.matchMaterial(name);
            if (m != null) return m;
        }
        if (c != null && c.getIcon() != null && c.getIcon().getType() != Material.AIR) return c.getIcon().getType();
        return Material.PAPER;
    }

    private void setTitle(int group) {
        String gn = groupNames.getOrDefault(group, "Group " + group);
        String template = plugin.getConfig().getString("classes-gui-title", "&8{group} Class");
        String title = MessageUtil.replace("{group}", gn, template);
        player.getOpenInventory().setTitle(MessageUtil.color(title));
    }

    private void sendGroupMessage(int group, Class winner) {
        String gn = groupNames.getOrDefault(group, "Group " + group);
        String msg = MessageUtil.replace("{class}", stripColor(winner.getDisplayName()),
                MessageUtil.replace("{group}", gn,
                        plugin.getConfig().getString("messages.class-assigned", "&eYou are now a &b{group} {class}&e!")));
        player.sendMessage(MessageUtil.color(msg));
    }

    private void finish() {
        completed = true;
        HandlerList.unregisterAll(this);
        Map<Integer, Class> all = new LinkedHashMap<>();
        all.putAll(keep);
        for (Map.Entry<Integer, Class> e : winners.entrySet()) {
            all.put(e.getKey(), e.getValue());
        }
        assignmentManager.assign(player, new ArrayList<>(all.values()));
        StringBuilder title = new StringBuilder();
        String subtitle = MessageUtil.color(plugin.getConfig().getString("messages.class-subtitle", "&7You have been destined with your classes!"));
        for (Map.Entry<Integer, Class> e : winners.entrySet()) {
            if (title.length() > 0) title.append(" &7• ");
            title.append(e.getValue().getDisplayName());
        }
        player.sendTitle(MessageUtil.color(title.toString()), subtitle, 10, 70, 20);
        List<String> parts = new ArrayList<>();
        for (Map.Entry<Integer, Class> e : winners.entrySet()) {
            parts.add(groupNames.getOrDefault(e.getKey(), "Group " + e.getKey()) + " " + stripColor(e.getValue().getDisplayName()));
        }
        String summary = MessageUtil.replace("{classes}", String.join(", ", parts),
                plugin.getConfig().getString("messages.class-summary", "&aYour classes are now: &e{classes}&a!"));
        player.sendMessage(MessageUtil.color(summary));
        sendStatsAndLink(parts);
        RerollMessages.sendRerollsLeft(player, plugin);
        SoundUtil.play(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST);
        if (plugin.getConfig().getBoolean("broadcast-class", true)) {
            String bc = MessageUtil.replace("{player}", player.getName(),
                    plugin.getConfig().getString("messages.class-broadcast", "&e{player} &ahas been destined with their classes!"));
            Bukkit.broadcastMessage(MessageUtil.color(bc));
        }
    }

    private void sendStatsAndLink(List<String> parts) {
        if (plugin.getConfig().getBoolean("stats-message-enabled", true)) {
            File folder = valhallaRacesFolder();
            for (Map.Entry<Integer, Class> e : winners.entrySet()) {
                List<String> stats = StatInfo.formatClassStats(e.getValue().getName(), stripColor(e.getValue().getDisplayName()), folder);
                for (String line : stats) {
                    player.sendMessage(line);
                }
            }
        }
        if (plugin.getConfig().getBoolean("link-enabled", true)) {
            String base = plugin.getConfig().getString("link-url", "");
            if (base != null && !base.isEmpty()) {
                String tmpl = plugin.getConfig().getString("link-class-template", base);
                String url = MessageUtil.replace("{classes}", String.join(", ", parts), tmpl);
                player.sendMessage(MessageUtil.color(
                        MessageUtil.replace("{url}", url,
                                plugin.getConfig().getString("messages.class-link", "&7Full details: &f{url}"))));
            }
        }
    }

    private File valhallaRacesFolder() {
        org.bukkit.plugin.Plugin vr = Bukkit.getPluginManager().getPlugin("ValhallaRaces");
        return vr != null ? vr.getDataFolder() : new File(Bukkit.getPluginManager().getPlugin("RandomRace").getDataFolder().getParentFile(), "ValhallaRaces");
    }

    private String stripColor(String s) {
        if (s == null) return "";
        return s.replaceAll("(?i)\\u00A7[0-9a-fk-or]", "");
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getPlayer().getUniqueId().equals(player.getUniqueId()) && !completed) {
            if (task != null) task.cancel();
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked().getUniqueId().equals(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }
}

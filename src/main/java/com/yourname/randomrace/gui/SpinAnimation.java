package com.yourname.randomrace.gui;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.managers.AssignmentManager;
import com.yourname.randomrace.utils.MessageUtil;
import com.yourname.randomrace.utils.RerollMessages;
import com.yourname.randomrace.utils.SoundUtil;
import com.yourname.randomrace.utils.StatInfo;
import me.athlaeos.valhallaraces.Race;
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
import java.util.List;

public class SpinAnimation implements Listener {
    private static final int INV_SIZE = 27;

    private static final int TOP_CELL = 4;
    private static final int MID_CELL = 13;
    private static final int BOTTOM_CELL = 22;

    private static final int MARKER_TOP = 3;
    private static final int MARKER_MID = 12;
    private static final int MARKER_BOTTOM = 21;

    private static final int[][] PHASES = {
        {2, 20},
        {4, 10},
        {8, 5}
    };
    private static final int TOTAL_CYCLES;
    static {
        int t = 0;
        for (int[] p : PHASES) t += p[1];
        TOTAL_CYCLES = t;
    }

    private final RandomRacePlugin plugin;
    private final Player player;
    private final Race winner;
    private final AssignmentManager assignmentManager;
    private final List<Race> reel;
    private Inventory inventory;
    private BukkitTask task;
    private int tickCount = 0;

    public SpinAnimation(RandomRacePlugin plugin, Player player, Race winner) {
        this.plugin = plugin;
        this.player = player;
        this.winner = winner;
        this.assignmentManager = new AssignmentManager(plugin);
        this.reel = buildReel();
    }

    private static final int WINNER_INDEX = TOTAL_CYCLES + 1;
    private static final int REEL_SIZE = TOTAL_CYCLES + 3;

    private List<Race> buildReel() {
        List<Race> candidates = new ArrayList<>(plugin.getRacePoolManager().getAvailableRaces(player));
        if (candidates.isEmpty()) candidates.add(winner);
        List<Race> reel = new ArrayList<>(REEL_SIZE);
        for (int i = 0; i < REEL_SIZE; i++) {
            if (i == WINNER_INDEX) {
                reel.add(winner);
            } else {
                reel.add(candidates.get(i % candidates.size()));
            }
        }
        return reel;
    }

    public void start() {
        inventory = plugin.getServer().createInventory(null, INV_SIZE, MessageUtil.color("&8Random Race"));
        buildBackground();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        player.openInventory(inventory);
        SoundUtil.play(player, Sound.BLOCK_CHEST_OPEN);
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            step();
            tickCount++;
            if (tickCount > TOTAL_CYCLES) {
                task.cancel();
                finish();
            }
        }, 10L, 2L);
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

    private void step() {
        inventory.setItem(TOP_CELL, raceItem(tickCount));
        inventory.setItem(MID_CELL, raceItem(tickCount + 1));
        inventory.setItem(BOTTOM_CELL, raceItem(tickCount + 2));
        float progress = Math.min(1f, (float) tickCount / TOTAL_CYCLES);
        float pitch = 0.5f + progress * 1.5f;
        SoundUtil.play(player, Sound.BLOCK_NOTE_BLOCK_HARP, pitch);
    }

    private ItemStack raceItem(int idx) {
        Race r = reel.get(idx % reel.size());
        ItemStack item = new ItemStack(plugin.getRacePoolManager().materialFor(r), 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtil.color(r.getDisplayName()));
            item.setItemMeta(meta);
        }
        return item;
    }

    private void finish() {
        HandlerList.unregisterAll(this);
        inventory.setItem(MID_CELL, raceItem(TOTAL_CYCLES + 1));
        SoundUtil.play(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.closeInventory();
            assignmentManager.assign(player, winner);
            plugin.getPlayerDataManager().markClaimed(player.getUniqueId());
            String display = winner.getDisplayName();
            String name = stripColor(display);
            String title = MessageUtil.replace("{race}", display,
                    plugin.getConfig().getString("messages.title", "&e&l{race}&r"));
            String subtitle = MessageUtil.replace("{race}", name,
                    plugin.getConfig().getString("messages.subtitle", "&7You have been destined to be a &e{race}&7!"));
            player.sendTitle(MessageUtil.color(title), MessageUtil.color(subtitle), 10, 70, 20);
            String assigned = MessageUtil.replace("{race}", name,
                    plugin.getConfig().getString("messages.race-assigned", "&aYou have been chosen as a &e{race}&a!"));
            player.sendMessage(MessageUtil.color(assigned));
            sendStatsAndLink(name);
            RerollMessages.sendRerollsLeft(player, plugin);
            SoundUtil.play(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST);
            if (plugin.getConfig().getBoolean("broadcast-enabled", true)) {
                String bc = MessageUtil.replace("{player}", player.getName(),
                        MessageUtil.replace("{race}", name,
                                plugin.getConfig().getString("messages.broadcast", "&e{player} &ahas been destined to be a &e{race}&a!")));
                Bukkit.broadcastMessage(MessageUtil.color(bc));
            }
        }, 40L);
    }

    private void sendStatsAndLink(String plainName) {
        if (plugin.getConfig().getBoolean("stats-message-enabled", true)) {
            File folder = valhallaRacesFolder();
            List<String> stats = StatInfo.formatRaceStats(winner.getName(), plainName, folder);
            for (String line : stats) {
                player.sendMessage(line);
            }
        }
        if (plugin.getConfig().getBoolean("link-enabled", true)) {
            String base = plugin.getConfig().getString("link-url", "");
            if (base != null && !base.isEmpty()) {
                String tmpl = plugin.getConfig().getString("link-race-template", base);
                String url = MessageUtil.replace("{race}", plainName, tmpl);
                player.sendMessage(MessageUtil.color(
                        MessageUtil.replace("{url}", url,
                                plugin.getConfig().getString("messages.race-link", "&7Full details: &f{url}"))));
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
        if (e.getPlayer().getUniqueId().equals(player.getUniqueId())) {
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

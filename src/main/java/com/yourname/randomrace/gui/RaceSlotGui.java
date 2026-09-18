package com.yourname.randomrace.gui;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.managers.AssignmentManager;
import com.yourname.randomrace.managers.PlayerDataManager;
import com.yourname.randomrace.utils.MessageUtil;
import me.athlaeos.valhallaraces.Race;
import me.athlaeos.valhallaraces.RaceManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class RaceSlotGui implements Listener {
    private static final int[] MAIN_POSITIONS = {9, 11, 13, 15, 17};
    private static final int CONFIRM_RED = 11;
    private static final int CONFIRM_CENTER = 13;
    private static final int CONFIRM_GREEN = 15;
    private static final int SUBMENU_SAVE = 11;
    private static final int SUBMENU_LOAD = 15;

    private enum Mode { MAIN, SUBMENU, SAVE_CONFIRM, REPLACE_CONFIRM, LOAD_CONFIRM }

    private static final class State {
        final Mode mode;
        final int slot;

        State(Mode mode, int slot) {
            this.mode = mode;
            this.slot = slot;
        }
    }

    private final RandomRacePlugin plugin;
    private final AssignmentManager assignmentManager;
    private final Map<Player, State> states = new HashMap<>();

    public RaceSlotGui(RandomRacePlugin plugin) {
        this.plugin = plugin;
        this.assignmentManager = new AssignmentManager(plugin);
    }

    public void openMain(Player p) {
        Inventory inv = plugin.getServer().createInventory(null, 27, MessageUtil.color("&8Race Slots"));
        fillBackground(inv);
        String current = currentRaceName(p);
        inv.setItem(4, named(Material.BOOK, "&eCurrent race: &f" + (current == null ? "&7None" : current)));
        PlayerDataManager pdm = plugin.getPlayerDataManager();
        for (int i = 0; i < MAIN_POSITIONS.length; i++) {
            int slot = i + 1;
            String key = pdm.getRaceSlot(p.getUniqueId(), slot);
            if (key == null) {
                inv.setItem(MAIN_POSITIONS[i], named(Material.GREEN_STAINED_GLASS_PANE, "&aSlot &f#" + slot,
                        "&7Click to save your current race here"));
            } else {
                Race race = raceByKey(key);
                String name = race == null ? key : stripColor(race.getDisplayName());
                Material mat = race == null ? Material.PAPER : plugin.getRacePoolManager().materialFor(race);
                inv.setItem(MAIN_POSITIONS[i], named(mat, "&f" + name,
                        "&7Slot #" + slot, "&7Click to manage"));
            }
        }
        p.openInventory(inv);
        states.put(p, new State(Mode.MAIN, 0));
    }

    private void openSubmenu(Player p, int slot) {
        Inventory inv = plugin.getServer().createInventory(null, 27, MessageUtil.color("&8Slot #" + slot));
        fillBackground(inv);
        inv.setItem(SUBMENU_SAVE, named(Material.WRITABLE_BOOK, "&eSave new race here",
                "&7Overwrite slot #" + slot + " with your current race"));
        inv.setItem(SUBMENU_LOAD, named(Material.DIAMOND, "&bLoad this race",
                "&7Make this your active race (cooldown applies)"));
        p.openInventory(inv);
        states.put(p, new State(Mode.SUBMENU, slot));
    }

    private void openConfirm(Player p, Mode mode, int slot) {
        String title = mode == Mode.SAVE_CONFIRM ? "&aSave this race?"
                : mode == Mode.REPLACE_CONFIRM ? "&cReplace race?"
                : "&eLoad saved race?";
        Inventory inv = plugin.getServer().createInventory(null, 27, MessageUtil.color(title));
        fillBackground(inv);
        String centerKey = mode == Mode.SAVE_CONFIRM
                ? currentRaceKey(p)
                : plugin.getPlayerDataManager().getRaceSlot(p.getUniqueId(), slot);
        Race centerRace = raceByKey(centerKey);
        String centerName = centerRace == null ? stripColor(centerKey) : stripColor(centerRace.getDisplayName());
        String lore = mode == Mode.SAVE_CONFIRM ? "&7Save into slot #" + slot + "?"
                : mode == Mode.REPLACE_CONFIRM ? "&7This race will be overwritten"
                : "&7Switch to this race?";
        Material mat = centerRace == null ? Material.PAPER : plugin.getRacePoolManager().materialFor(centerRace);
        inv.setItem(CONFIRM_CENTER, named(mat, "&f" + centerName, lore));
        inv.setItem(CONFIRM_RED, named(Material.RED_STAINED_GLASS_PANE, "&cCancel"));
        inv.setItem(CONFIRM_GREEN, named(Material.GREEN_STAINED_GLASS_PANE, "&aConfirm"));
        p.openInventory(inv);
        states.put(p, new State(mode, slot));
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        State st = states.get(p);
        if (st == null) return;
        e.setCancelled(true);
        int slot = e.getSlot();
        switch (st.mode) {
            case MAIN:
                for (int i = 0; i < MAIN_POSITIONS.length; i++) {
                    if (slot != MAIN_POSITIONS[i]) continue;
                    int n = i + 1;
                    if (plugin.getPlayerDataManager().getRaceSlot(p.getUniqueId(), n) == null) {
                        if (currentRaceKey(p) == null) {
                            p.sendMessage(MessageUtil.color("&cYou have no race to save."));
                            return;
                        }
                        openConfirm(p, Mode.SAVE_CONFIRM, n);
                    } else {
                        openSubmenu(p, n);
                    }
                    return;
                }
                return;
            case SUBMENU:
                if (slot == SUBMENU_SAVE) {
                    if (currentRaceKey(p) == null) {
                        p.sendMessage(MessageUtil.color("&cYou have no race to save."));
                        return;
                    }
                    openConfirm(p, Mode.REPLACE_CONFIRM, st.slot);
                } else if (slot == SUBMENU_LOAD) {
                    long remaining = secondsRemaining(plugin.getPlayerDataManager().getLoadCooldown(p.getUniqueId()),
                            System.currentTimeMillis());
                    if (remaining > 0) {
                        p.sendMessage(MessageUtil.color("&cWait " + remaining + "s before switching races."));
                        openMain(p);
                    } else {
                        openConfirm(p, Mode.LOAD_CONFIRM, st.slot);
                    }
                }
                return;
            case SAVE_CONFIRM:
            case REPLACE_CONFIRM:
                if (slot == CONFIRM_GREEN) {
                    String key = currentRaceKey(p);
                    if (key == null) {
                        p.sendMessage(MessageUtil.color("&cYou have no race to save."));
                        return;
                    }
                    plugin.getPlayerDataManager().setRaceSlot(p.getUniqueId(), st.slot, key);
                    openMain(p);
                } else if (slot == CONFIRM_RED) {
                    openMain(p);
                }
                return;
            case LOAD_CONFIRM:
                if (slot == CONFIRM_GREEN) {
                    load(p, st.slot);
                    openMain(p);
                } else if (slot == CONFIRM_RED) {
                    openMain(p);
                }
                return;
        }
    }

    private void load(Player p, int slot) {
        String key = plugin.getPlayerDataManager().getRaceSlot(p.getUniqueId(), slot);
        Race race = raceByKey(key);
        if (race == null) return;
        long end = System.currentTimeMillis() + plugin.getConfig().getInt("race-slot-cooldown-seconds", 30) * 1000L;
        plugin.getPlayerDataManager().setLoadCooldown(p.getUniqueId(), end);
        assignmentManager.assign(p, race);
        plugin.getPlayerDataManager().markClaimed(p.getUniqueId());
        p.sendMessage(MessageUtil.color("&aLoaded saved race &e" + stripColor(race.getDisplayName()) + "&a."));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        states.remove((Player) e.getPlayer());
    }

    private String currentRaceKey(Player p) {
        Race r = RaceManager.getRace(p);
        return r == null ? null : r.getName();
    }

    private String currentRaceName(Player p) {
        Race r = RaceManager.getRace(p);
        return r == null ? null : stripColor(r.getDisplayName());
    }

    private Race raceByKey(String key) {
        if (key == null) return null;
        Map<String, Race> races = RaceManager.getRegisteredRaces();
        return races == null ? null : races.get(key);
    }

    public static long secondsRemaining(long endEpochMillis, long nowMillis) {
        long ms = endEpochMillis - nowMillis;
        if (ms <= 0) return 0;
        return (ms + 999L) / 1000L;
    }

    private void fillBackground(Inventory inv) {
        ItemStack black = named(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int slot = 0; slot < inv.getSize(); slot++) {
            inv.setItem(slot, black);
        }
    }

    private ItemStack named(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtil.color(name));
            if (lore.length > 0) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                for (String s : lore) lines.add(MessageUtil.color(s));
                meta.setLore(lines);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private String stripColor(String s) {
        if (s == null) return "";
        return s.replaceAll("(?i)\\u00A7[0-9a-fk-or]", "");
    }
}
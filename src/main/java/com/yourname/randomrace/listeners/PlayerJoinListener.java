package com.yourname.randomrace.listeners;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.utils.MessageUtil;
import com.yourname.randomrace.utils.RerollMessages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final RandomRacePlugin plugin;

    public PlayerJoinListener(RandomRacePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        boolean first = !plugin.getPlayerDataManager().hasRecord(uuid);
        plugin.getPlayerDataManager().migrateByName(p);
        if (first) {
            plugin.getPlayerDataManager().markJoined(uuid);
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline()) return;
            if (first) {
                String intro = MessageUtil.replace("{player}", p.getName(),
                        plugin.getConfig().getString("messages.first-join",
                                "&eWelcome, &6{player}&e! Claim your race with &c/claimrace&e and your classes with &c/claimclass&e."));
                p.sendMessage(MessageUtil.color(intro));
            }
            if (plugin.getRerollService().hasAnyRerolls(uuid)) {
                RerollMessages.sendReminder(p, plugin);
            }
        }, 20L);
    }
}
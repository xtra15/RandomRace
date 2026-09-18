package com.yourname.randomrace.commands;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RaceSlotCommand implements CommandExecutor {
    private final RandomRacePlugin plugin;

    public RaceSlotCommand(RandomRacePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&cOnly players can use this."));
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("randomrace.claim")) {
            p.sendMessage(MessageUtil.color("&cYou don't have permission to use this."));
            return true;
        }
        plugin.getRaceSlotGui().openMain(p);
        return true;
    }
}
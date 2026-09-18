package com.yourname.randomrace.commands;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.gui.SpinAnimation;
import com.yourname.randomrace.managers.RerollService;
import com.yourname.randomrace.utils.MessageUtil;
import com.yourname.randomrace.utils.RerollMessages;
import com.yourname.randomrace.utils.SoundUtil;
import me.athlaeos.valhallaraces.Race;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class RerollRaceCommand implements CommandExecutor {
    private final RandomRacePlugin plugin;
    private final RerollService rerollService;
    private final Random random = new Random();

    public RerollRaceCommand(RandomRacePlugin plugin) {
        this.plugin = plugin;
        this.rerollService = plugin.getRerollService();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&cOnly players can reroll a race."));
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("randomrace.claim")) {
            p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.no-permission", "&cYou don't have permission to use this.")));
            return true;
        }
        if (!rerollService.trySpendRaceReroll(p.getUniqueId())) {
            p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.reroll-race-no-points", "&cYou have no race rerolls left. Ask an admin to give you some.")));
            SoundUtil.play(p, Sound.ENTITY_VILLAGER_NO);
            RerollMessages.sendRerollsLeft(p, plugin);
            return true;
        }
        plugin.getRacePoolManager().refresh();
        List<Race> available = plugin.getRacePoolManager().getAvailableRaces(p);
        if (available.isEmpty()) {
            p.sendMessage(MessageUtil.color("&cThere are no races available to you right now."));
            return true;
        }
        Race winner = plugin.getRacePoolManager().pickWeighted(random, available);
        p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.reroll-race-spin", "&eThe fates are rerolling your race...")));
        new SpinAnimation(plugin, p, winner).start();
        return true;
    }
}
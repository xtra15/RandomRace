package com.yourname.randomrace.commands;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.gui.SpinAnimation;
import com.yourname.randomrace.managers.AssignmentManager;
import com.yourname.randomrace.managers.RacePoolManager;
import com.yourname.randomrace.utils.MessageUtil;
import com.yourname.randomrace.utils.SoundUtil;
import me.athlaeos.valhallaraces.Race;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class ClaimRaceCommand implements CommandExecutor {
    private final RandomRacePlugin plugin;
    private final AssignmentManager assignmentManager = new AssignmentManager();
    private final Random random = new Random();

    public ClaimRaceCommand(RandomRacePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&cOnly players can claim a race."));
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("randomrace.claim")) {
            p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.no-permission", "&cYou don't have permission to use this.")));
            return true;
        }
        if (plugin.getConfig().getBoolean("one-time-only", true) && assignmentManager.hasRace(p)) {
            p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.already-claimed", "&cYou have already claimed your race!")));
            SoundUtil.play(p, Sound.ENTITY_VILLAGER_NO);
            return true;
        }
        plugin.getRacePoolManager().refresh();
        RacePoolManager rpm = plugin.getRacePoolManager();
        List<Race> available = rpm.getAvailableRaces(p);
        if (available.isEmpty()) {
            p.sendMessage(MessageUtil.color("&cThere are no races available to you right now."));
            return true;
        }
        Race winner = rpm.pickWeighted(random, available);
        p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.spin-start", "&eThe fates are deciding your race...")));
        new SpinAnimation(plugin, p, winner).start();
        return true;
    }
}

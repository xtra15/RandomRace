package com.yourname.randomrace.commands;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.gui.ClassSpinAnimation;
import com.yourname.randomrace.managers.ClassAssignmentManager;
import com.yourname.randomrace.utils.MessageUtil;
import com.yourname.randomrace.utils.SoundUtil;
import me.athlaeos.valhallaraces.Class;
import me.athlaeos.valhallaraces.ClassManager;
import me.athlaeos.valhallaraces.Race;
import me.athlaeos.valhallaraces.RaceManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ClaimClassCommand implements CommandExecutor {
    private final RandomRacePlugin plugin;
    private final ClassAssignmentManager assignmentManager;
    private final Random random = new Random();

    public ClaimClassCommand(RandomRacePlugin plugin) {
        this.plugin = plugin;
        this.assignmentManager = new ClassAssignmentManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&cOnly players can claim classes."));
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("randomrace.class")) {
            p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.class-no-permission", "&cYou don't have permission to use this.")));
            return true;
        }
        int count = plugin.getConfig().getInt("classes-count", 3);
        Map<Integer, Class> existing = ClassManager.getClasses(p);
        if (plugin.getConfig().getBoolean("class-one-time-only", true)
                && existing != null && existing.size() >= count) {
            p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.class-already-claimed", "&cYou already have all your classes!")));
            SoundUtil.play(p, Sound.ENTITY_VILLAGER_NO);
            return true;
        }
        plugin.getClassPoolManager().refresh();
        String race = raceName(p);
        Set<Integer> skip = existing == null ? new java.util.HashSet<>() : existing.keySet();
        List<Integer> groups = plugin.getClassPoolManager().pickRandomGroups(random, count, p, race, skip);
        Map<Integer, Class> winners = new LinkedHashMap<>();
        for (Integer g : groups) {
            Class c = plugin.getClassPoolManager().pickForGroup(random, g, p, race);
            if (c != null) winners.put(g, c);
        }
        if (winners.isEmpty()) {
            p.sendMessage(MessageUtil.color("&cNo classes are available to you right now."));
            return true;
        }
        p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.class-spin-start", "&eThe fates are choosing your classes...")));
        new ClassSpinAnimation(plugin, p, winners).start();
        return true;
    }

    private String raceName(Player p) {
        Race r = RaceManager.getRace(p);
        return r == null ? null : r.getName();
    }
}

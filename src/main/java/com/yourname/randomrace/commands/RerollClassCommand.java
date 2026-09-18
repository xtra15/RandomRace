package com.yourname.randomrace.commands;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.gui.ClassSpinAnimation;
import com.yourname.randomrace.managers.ClassAssignmentManager;
import com.yourname.randomrace.managers.RerollService;
import com.yourname.randomrace.utils.MessageUtil;
import com.yourname.randomrace.utils.RerollMessages;
import com.yourname.randomrace.utils.SoundUtil;
import me.athlaeos.valhallaraces.Class;
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

public class RerollClassCommand implements CommandExecutor {
    private final RandomRacePlugin plugin;
    private final ClassAssignmentManager assignmentManager;
    private final RerollService rerollService;
    private final Random random = new Random();

    public RerollClassCommand(RandomRacePlugin plugin) {
        this.plugin = plugin;
        this.assignmentManager = new ClassAssignmentManager(plugin);
        this.rerollService = plugin.getRerollService();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&cOnly players can reroll classes."));
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("randomrace.class")) {
            p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.class-no-permission", "&cYou don't have permission to use this.")));
            return true;
        }
        if (!rerollService.trySpendClassReroll(p.getUniqueId())) {
            p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.reroll-class-no-points", "&cYou have no class rerolls left. Ask an admin to give you some.")));
            SoundUtil.play(p, Sound.ENTITY_VILLAGER_NO);
            RerollMessages.sendRerollsLeft(p, plugin);
            return true;
        }
        int cap = rerollService.classSlots(p.getUniqueId());
        assignmentManager.clear(p);
        plugin.getClassPoolManager().refresh();
        String race = raceName(p);
        List<Integer> groups = plugin.getClassPoolManager().pickRandomGroups(random, cap, p, race, null);
        Map<Integer, Class> winners = new LinkedHashMap<>();
        for (Integer g : groups) {
            Class c = plugin.getClassPoolManager().pickForGroup(random, g, p, race);
            if (c != null) winners.put(g, c);
        }
        if (winners.isEmpty()) {
            p.sendMessage(MessageUtil.color("&cNo classes are available to you right now."));
            return true;
        }
        p.sendMessage(MessageUtil.color(plugin.getConfig().getString("messages.reroll-class-spin", "&eThe fates are rerolling your classes...")));
        new ClassSpinAnimation(plugin, p, winners).start();
        return true;
    }

    private String raceName(Player p) {
        Race r = RaceManager.getRace(p);
        return r == null ? null : r.getName();
    }
}
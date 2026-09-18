package com.yourname.randomrace.commands;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.managers.RerollService;
import com.yourname.randomrace.utils.MessageUtil;
import com.yourname.randomrace.utils.StatInfo;
import me.athlaeos.valhallaraces.Class;
import me.athlaeos.valhallaraces.ClassManager;
import me.athlaeos.valhallaraces.Race;
import me.athlaeos.valhallaraces.RaceManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ValhallaStatsCommand implements CommandExecutor, TabCompleter {
    private final RandomRacePlugin plugin;

    public ValhallaStatsCommand(RandomRacePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                sender.sendMessage(MessageUtil.color(summary(p)));
            }
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target != null) {
            sendDetail(sender, target.getName());
            return true;
        }
        UUID offline = plugin.getPlayerDataManager().resolveOffline(args[0]);
        if (offline == null) {
            sender.sendMessage(MessageUtil.color("&cPlayer not found."));
            return true;
        }
        sendDetail(sender, args[0]);
        return true;
    }

    private String summary(Player p) {
        StringBuilder sb = new StringBuilder();
        sb.append("&e").append(p.getName()).append("&7:");
        Race race = RaceManager.getRace(p);
        sb.append(" &8Race: ").append(race == null ? "&7None" : "&f" + strip(race.getDisplayName()));
        Map<Integer, Class> classes = ClassManager.getClasses(p);
        if (classes != null && !classes.isEmpty()) {
            List<String> names = classes.values().stream().map(c -> strip(c.getDisplayName())).collect(Collectors.toList());
            sb.append(" &8Classes: &f").append(String.join("&7, &f", names));
        } else {
            sb.append(" &8Classes: &7None");
        }
        return sb.toString();
    }

    private void sendDetail(CommandSender sender, String name) {
        Player online = Bukkit.getPlayerExact(name);
        String raceKey = null;
        Collection<String> classKeys = new ArrayList<>();
        Map<Integer, Class> classesLive = null;

        if (online != null) {
            Race race = RaceManager.getRace(online);
            raceKey = race == null ? null : race.getName();
            classesLive = ClassManager.getClasses(online);
            if (classesLive != null) {
                for (Class c : classesLive.values()) classKeys.add(c.getName());
            }
        } else {
            UUID uuid = plugin.getPlayerDataManager().resolveOffline(name);
            if (uuid != null) {
                raceKey = plugin.getPlayerDataManager().getRace(uuid);
                classKeys = plugin.getPlayerDataManager().getClasses(uuid);
            }
        }

        sender.sendMessage(MessageUtil.color("&8&m----------------------------"));
        sender.sendMessage(MessageUtil.color("&e&l" + name + (online == null ? " &8(offline)" : "")));
        sender.sendMessage(MessageUtil.color("&8Race: " + (raceKey == null ? "&7None" : "&f" + displayRace(raceKey))));
        if (online != null && classesLive != null && !classesLive.isEmpty()) {
            for (Map.Entry<Integer, Class> e : classesLive.entrySet()) {
                sender.sendMessage(MessageUtil.color("&8" + groupName(e.getKey()) + ": &f" + strip(e.getValue().getDisplayName())));
            }
        } else if (classKeys == null || classKeys.isEmpty()) {
            sender.sendMessage(MessageUtil.color("&8Classes: &7None"));
        } else {
            for (String ck : classKeys) {
                Class c = ClassManager.getRegisteredClasses() == null ? null : ClassManager.getRegisteredClasses().get(ck);
                if (c == null) {
                    sender.sendMessage(MessageUtil.color("&8  &f" + ck));
                } else {
                    sender.sendMessage(MessageUtil.color("&8" + groupName(c.getGroup()) + ": &f" + strip(c.getDisplayName())));
                }
            }
        }
        UUID targetUuid = online != null ? online.getUniqueId() : plugin.getPlayerDataManager().resolveOffline(name);
        if (targetUuid != null) {
            RerollService rs = plugin.getRerollService();
            sender.sendMessage(MessageUtil.color("&8Rerolls: &f" + rs.raceRerolls(targetUuid) + " race / "
                    + rs.classRerolls(targetUuid) + " class &8- Slots: &f" + rs.classSlots(targetUuid)));
        }
        for (String line : StatInfo.formatCombinedStats(raceKey, classKeys, valhallaRacesFolder())) {
            sender.sendMessage(line);
        }
        sender.sendMessage(MessageUtil.color("&8&m----------------------------"));
    }

    private String displayRace(String raceKey) {
        Race r = RaceManager.getRegisteredRaces() == null ? null : RaceManager.getRegisteredRaces().get(raceKey);
        return r == null ? raceKey : strip(r.getDisplayName());
    }

    private String groupName(int g) {
        return plugin.getConfig().getString("groups." + g, "Group " + g);
    }

    private File valhallaRacesFolder() {
        org.bukkit.plugin.Plugin vr = Bukkit.getPluginManager().getPlugin("ValhallaRaces");
        return vr != null ? vr.getDataFolder() : new File(plugin.getDataFolder().getParentFile(), "ValhallaRaces");
    }

    private String strip(String s) {
        if (s == null) return "";
        return s.replaceAll("(?i)\\u00A7[0-9a-fk-or]", "");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            String a = args[0].toLowerCase();
            out.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(a))
                    .collect(Collectors.toList()));
        }
        return out;
    }
}

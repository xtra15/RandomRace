package com.yourname.randomrace.commands;

import com.yourname.randomrace.RandomRacePlugin;
import com.yourname.randomrace.gui.ClassSpinAnimation;
import com.yourname.randomrace.gui.SpinAnimation;
import com.yourname.randomrace.managers.AssignmentManager;
import com.yourname.randomrace.managers.ClassAssignmentManager;
import com.yourname.randomrace.managers.PlayerDataManager;
import com.yourname.randomrace.managers.RerollService;
import com.yourname.randomrace.utils.MessageUtil;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomRaceAdminCommand implements CommandExecutor, TabCompleter {
    private static final List<String> SUBCOMMANDS = Arrays.asList(
        "reload", "listrace", "reset", "reroll", "setrace",
        "resetclass", "rerollclass", "setclass", "setclasscount", "listclass",
        "give", "set", "setslots", "check"
    );

    private final RandomRacePlugin plugin;
    private final AssignmentManager assignmentManager;
    private final ClassAssignmentManager classAssignmentManager;
    private final Random random = new Random();

    public RandomRaceAdminCommand(RandomRacePlugin plugin) {
        this.plugin = plugin;
        this.assignmentManager = new AssignmentManager(plugin);
        this.classAssignmentManager = new ClassAssignmentManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("randomrace.admin")) {
            sender.sendMessage(MessageUtil.color("&cYou don't have permission to use this."));
            return true;
        }
        if (args.length == 0) {
                sender.sendMessage(MessageUtil.color("&cUsage: /randomrace <reset|reroll|setrace|resetclass|rerollclass|setclass|setclasscount|listclass|reload|listrace>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                sender.sendMessage(MessageUtil.color("&aRandomRace reload complete."));
                return true;
            case "listrace":
                plugin.getRacePoolManager().refresh();
                Map<String, Race> races = RaceManager.getRegisteredRaces();
                if (races == null || races.isEmpty()) {
                    sender.sendMessage(MessageUtil.color("&cNo races loaded from ValhallaRaces."));
                } else {
                    sender.sendMessage(MessageUtil.color("&aLoaded " + races.size() + " races:"));
                    for (Race r : races.values()) {
                        sender.sendMessage(MessageUtil.color("  &7" + r.getName() + " &8- &f" + stripColor(r.getDisplayName())));
                    }
                }
                return true;
            case "reset":
                if (args.length < 2) { sender.sendMessage(MessageUtil.color("&cUsage: /randomrace reset <player>")); return true; }
                Player rp = Bukkit.getPlayerExact(args[1]);
                if (rp == null) { sender.sendMessage(MessageUtil.color("&cPlayer not found.")); return true; }
                assignmentManager.clear(rp);
                plugin.getPlayerDataManager().clearClaim(rp.getUniqueId());
                sender.sendMessage(MessageUtil.color("&aReset " + rp.getName() + "'s race."));
                return true;
            case "reroll":
                if (args.length < 2) { sender.sendMessage(MessageUtil.color("&cUsage: /randomrace reroll <player>")); return true; }
                Player rr = Bukkit.getPlayerExact(args[1]);
                if (rr == null) { sender.sendMessage(MessageUtil.color("&cPlayer not found.")); return true; }
                assignmentManager.clear(rr);
                plugin.getPlayerDataManager().clearClaim(rr.getUniqueId());
                plugin.getRacePoolManager().refresh();
                List<Race> available = plugin.getRacePoolManager().getAvailableRaces(rr);
                if (available.isEmpty()) { sender.sendMessage(MessageUtil.color("&cNo races available.")); return true; }
                Race winner = plugin.getRacePoolManager().pickWeighted(random, available);
                new SpinAnimation(plugin, rr, winner).start();
                return true;
            case "setrace":
                if (args.length < 3) { sender.sendMessage(MessageUtil.color("&cUsage: /randomrace setrace <player> <race>")); return true; }
                Player sp = Bukkit.getPlayerExact(args[1]);
                if (sp == null) { sender.sendMessage(MessageUtil.color("&cPlayer not found.")); return true; }
                Race race = RaceManager.getRegisteredRaces().get(args[2]);
                if (race == null) { sender.sendMessage(MessageUtil.color("&cRace '" + args[2] + "' not found.")); return true; }
                assignmentManager.assign(sp, race);
                plugin.getPlayerDataManager().markClaimed(sp.getUniqueId());
                sender.sendMessage(MessageUtil.color("&aSet " + sp.getName() + "'s race to " + stripColor(race.getDisplayName()) + "&a."));
                return true;
            case "resetclass":
                if (args.length < 2) { sender.sendMessage(MessageUtil.color("&cUsage: /randomrace resetclass <player>")); return true; }
                Player cp = Bukkit.getPlayerExact(args[1]);
                if (cp == null) { sender.sendMessage(MessageUtil.color("&cPlayer not found.")); return true; }
                classAssignmentManager.clear(cp);
                sender.sendMessage(MessageUtil.color("&aReset " + cp.getName() + "'s classes."));
                return true;
            case "rerollclass":
                if (args.length < 2) { sender.sendMessage(MessageUtil.color("&cUsage: /randomrace rerollclass <player>")); return true; }
                Player rrp = Bukkit.getPlayerExact(args[1]);
                if (rrp == null) { sender.sendMessage(MessageUtil.color("&cPlayer not found.")); return true; }
                classAssignmentManager.clear(rrp);
                plugin.getClassPoolManager().refresh();
                String raceName = raceName(rrp);
                int count = plugin.getConfig().getInt("classes-count", 3);
                List<Integer> groups = plugin.getClassPoolManager().pickRandomGroups(random, count, rrp, raceName, null);
                Map<Integer, Class> winners = new LinkedHashMap<>();
                for (Integer g : groups) {
                    Class c = plugin.getClassPoolManager().pickForGroup(random, g, rrp, raceName);
                    if (c != null) winners.put(g, c);
                }
                if (winners.isEmpty()) { sender.sendMessage(MessageUtil.color("&cNo classes available.")); return true; }
                new ClassSpinAnimation(plugin, rrp, winners).start();
                return true;
            case "setclasscount":
                if (args.length < 2) { sender.sendMessage(MessageUtil.color("&cUsage: /randomrace setclasscount <1-10>")); return true; }
                int newCount;
                try {
                    newCount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(MessageUtil.color("&cInvalid number."));
                    return true;
                }
                if (newCount < 1 || newCount > 10) {
                    sender.sendMessage(MessageUtil.color("&cCount must be between 1 and 10."));
                    return true;
                }
                plugin.getConfig().set("classes-count", newCount);
                plugin.saveConfig();
                plugin.reloadConfig();
                sender.sendMessage(MessageUtil.color("&aClasses per roll set to &e" + newCount + "&a."));
                return true;
            case "setclass":
                if (args.length < 4) { sender.sendMessage(MessageUtil.color("&cUsage: /randomrace setclass <player> <group> <class>")); return true; }
                Player scp = Bukkit.getPlayerExact(args[1]);
                if (scp == null) { sender.sendMessage(MessageUtil.color("&cPlayer not found.")); return true; }
                int group;
                try {
                    group = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(MessageUtil.color("&cInvalid group."));
                    return true;
                }
                Class sc = ClassManager.getRegisteredClasses().get(args[3]);
                if (sc == null || sc.getGroup() != group) {
                    sender.sendMessage(MessageUtil.color("&cClass '" + args[3] + "' not found or not in that group."));
                    return true;
                }
                Map<Integer, Class> cur = new LinkedHashMap<>(ClassManager.getClasses(scp));
                cur.put(group, sc);
                classAssignmentManager.replaceAll(scp, new ArrayList<>(cur.values()));
                sender.sendMessage(MessageUtil.color("&aSet " + scp.getName() + "'s " + groupName(group) + " class to " + stripColor(sc.getDisplayName()) + "&a."));
                return true;
            case "listclass":
                plugin.getClassPoolManager().refresh();
                Map<String, Class> classes = ClassManager.getRegisteredClasses();
                if (classes == null || classes.isEmpty()) {
                    sender.sendMessage(MessageUtil.color("&cNo classes loaded from ValhallaRaces."));
                } else {
                    sender.sendMessage(MessageUtil.color("&aLoaded " + classes.size() + " classes:"));
                    for (Class c : classes.values()) {
                        sender.sendMessage(MessageUtil.color("  [&b" + c.getGroup() + "&f] &7" + c.getName() + " &8- &f" + stripColor(c.getDisplayName())));
                    }
                }
                return true;
            case "give":
                if (args.length < 4) { sender.sendMessage(MessageUtil.color("&cUsage: /randomrace give <player> <race|class> <amount>")); return true; }
                {
                    UUID tu = uuidFor(args[1]);
                    if (tu == null) { sender.sendMessage(MessageUtil.color("&cPlayer not found.")); return true; }
                    int n;
                    try {
                        n = Integer.parseInt(args[3]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(MessageUtil.color("&cInvalid amount."));
                        return true;
                    }
                    if (n < 1) { sender.sendMessage(MessageUtil.color("&cAmount must be 1 or more.")); return true; }
                    if (args[2].equalsIgnoreCase("class")) {
                        plugin.getRerollService().addClassRerolls(tu, n);
                        sender.sendMessage(MessageUtil.color("&aGave &e" + n + "&a class reroll(s) to &e" + args[1] + "&a."));
                    } else if (args[2].equalsIgnoreCase("race")) {
                        plugin.getRerollService().addRaceRerolls(tu, n);
                        sender.sendMessage(MessageUtil.color("&aGave &e" + n + "&a race reroll(s) to &e" + args[1] + "&a."));
                    } else {
                        sender.sendMessage(MessageUtil.color("&cType must be 'race' or 'class'."));
                        return true;
                    }
                }
                return true;
            case "set":
                if (args.length < 4) { sender.sendMessage(MessageUtil.color("&cUsage: /randomrace set <player> <race|class> <amount>")); return true; }
                {
                    UUID tu = uuidFor(args[1]);
                    if (tu == null) { sender.sendMessage(MessageUtil.color("&cPlayer not found.")); return true; }
                    int n;
                    try {
                        n = Integer.parseInt(args[3]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(MessageUtil.color("&cInvalid amount."));
                        return true;
                    }
                    if (n < 0) { sender.sendMessage(MessageUtil.color("&cAmount cannot be negative.")); return true; }
                    if (args[2].equalsIgnoreCase("class")) {
                        plugin.getRerollService().setClassRerolls(tu, n);
                    } else if (args[2].equalsIgnoreCase("race")) {
                        plugin.getRerollService().setRaceRerolls(tu, n);
                    } else {
                        sender.sendMessage(MessageUtil.color("&cType must be 'race' or 'class'."));
                        return true;
                    }
                    sender.sendMessage(MessageUtil.color("&aSet " + args[1] + "'s " + args[2].toLowerCase() + " rerolls to &e" + n + "&a."));
                }
                return true;
            case "setslots":
                if (args.length < 3) { sender.sendMessage(MessageUtil.color("&cUsage: /randomrace setslots <player> <1-10>")); return true; }
                {
                    UUID tu = uuidFor(args[1]);
                    if (tu == null) { sender.sendMessage(MessageUtil.color("&cPlayer not found.")); return true; }
                    int n;
                    try {
                        n = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(MessageUtil.color("&cInvalid number."));
                        return true;
                    }
                    if (n < 1 || n > 10) { sender.sendMessage(MessageUtil.color("&cSlots must be between 1 and 10.")); return true; }
                    plugin.getRerollService().setClassSlots(tu, n);
                    sender.sendMessage(MessageUtil.color("&aSet " + args[1] + "'s max class slots to &e" + n + "&a."));
                    Player online = Bukkit.getPlayerExact(args[1]);
                    if (online != null && ClassManager.getClasses(online).size() > n) {
                        sender.sendMessage(MessageUtil.color("&7(They currently have more classes; the next class re-roll will shrink to " + n + ".)"));
                    }
                }
                return true;
            case "check":
                if (args.length < 2) { sender.sendMessage(MessageUtil.color("&cUsage: /randomrace check <player>")); return true; }
                {
                    UUID tu = uuidFor(args[1]);
                    if (tu == null) { sender.sendMessage(MessageUtil.color("&cPlayer not found.")); return true; }
                    PlayerDataManager pdm = plugin.getPlayerDataManager();
                    RerollService rs = plugin.getRerollService();
                    String storedRace = pdm.getRace(tu);
                    int have = pdm.getClasses(tu).size();
                    sender.sendMessage(MessageUtil.color("&8&m------------------------"));
                    sender.sendMessage(MessageUtil.color("&e&l" + args[1]));
                    sender.sendMessage(MessageUtil.color("&8Race: &f" + (storedRace == null ? "&7None" : storedRace)));
                    sender.sendMessage(MessageUtil.color("&8Classes: &f" + have + "&7/&f" + rs.classSlots(tu)));
                    sender.sendMessage(MessageUtil.color("&8Race rerolls: &f" + rs.raceRerolls(tu)));
                    sender.sendMessage(MessageUtil.color("&8Class rerolls: &f" + rs.classRerolls(tu)));
                    sender.sendMessage(MessageUtil.color("&8&m------------------------"));
                }
                return true;
            default:
            sender.sendMessage(MessageUtil.color("&cUsage: /randomrace <reset|reroll|setrace|resetclass|rerollclass|setclass|setclasscount|listclass|reload|listrace>"));
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            return filter(SUBCOMMANDS, args[0]);
        }
        String sub = args[0].toLowerCase();
        if (args.length == 2) {
            switch (sub) {
                case "reset":
                case "reroll":
                case "setrace":
                case "resetclass":
                case "rerollclass":
                case "setclass":
                case "give":
                case "set":
                case "setslots":
                case "check":
                    return filter(playerNames(), args[1]);
                default:
                    return out;
            }
        }
        if (args.length == 3) {
            switch (sub) {
                case "setrace":
                    return filter(raceNames(), args[2]);
                case "setclass":
                    return filter(IntStream.rangeClosed(1, 10).mapToObj(String::valueOf).collect(Collectors.toList()), args[2]);
                case "give":
                case "set":
                    return filter(Arrays.asList("race", "class"), args[2]);
                default:
                    return out;
            }
        }
        if (args.length == 4 && sub.equals("setclass")) {
            int group;
            try {
                group = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                return out;
            }
            int fg = group;
            return filter(classNamesInGroup(fg), args[3]);
        }
        return out;
    }

    private List<String> filter(List<String> options, String arg) {
        String a = arg.toLowerCase();
        return options.stream().filter(o -> o.toLowerCase().startsWith(a)).collect(Collectors.toList());
    }

    private List<String> playerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    private UUID uuidFor(String name) {
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) return online.getUniqueId();
        UUID stored = plugin.getPlayerDataManager().resolveOffline(name);
        if (stored != null) return stored;
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    private List<String> raceNames() {
        Map<String, Race> races = RaceManager.getRegisteredRaces();
        if (races == null) return new ArrayList<>();
        return new ArrayList<>(races.keySet());
    }

    private List<String> classNamesInGroup(int group) {
        Map<String, Class> classes = ClassManager.getRegisteredClasses();
        if (classes == null) return new ArrayList<>();
        return classes.values().stream()
                .filter(c -> c.getGroup() == group)
                .map(Class::getName)
                .collect(Collectors.toList());
    }

    private String raceName(Player p) {
        Race r = RaceManager.getRace(p);
        return r == null ? null : r.getName();
    }

    private String groupName(int g) {
        return plugin.getConfig().getString("groups." + g, "Group " + g);
    }

    private String stripColor(String s) {
        if (s == null) return "";
        return s.replaceAll("(?i)\\u00A7[0-9a-fk-or]", "");
    }
}

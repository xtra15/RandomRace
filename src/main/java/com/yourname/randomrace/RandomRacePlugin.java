package com.yourname.randomrace;

import com.yourname.randomrace.commands.ClaimClassCommand;
import com.yourname.randomrace.commands.ClaimRaceCommand;
import com.yourname.randomrace.commands.RandomRaceAdminCommand;
import com.yourname.randomrace.commands.ValhallaStatsCommand;
import com.yourname.randomrace.managers.ClassPoolManager;
import com.yourname.randomrace.managers.PlayerDataManager;
import com.yourname.randomrace.managers.RacePoolManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RandomRacePlugin extends JavaPlugin {
    private static RandomRacePlugin instance;
    private PlayerDataManager playerDataManager;
    private RacePoolManager racePoolManager;
    private ClassPoolManager classPoolManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        playerDataManager = new PlayerDataManager(this);
        racePoolManager = new RacePoolManager(this);
        racePoolManager.refresh();
        classPoolManager = new ClassPoolManager(this);
        classPoolManager.refresh();

        Objects.requireNonNull(getCommand("claimrace")).setExecutor(new ClaimRaceCommand(this));
        Objects.requireNonNull(getCommand("claimclass")).setExecutor(new ClaimClassCommand(this));
        RandomRaceAdminCommand admin = new RandomRaceAdminCommand(this);
        Objects.requireNonNull(getCommand("randomrace")).setExecutor(admin);
        Objects.requireNonNull(getCommand("randomrace")).setTabCompleter(admin);
        ValhallaStatsCommand stats = new ValhallaStatsCommand(this);
        Objects.requireNonNull(getCommand("valracestats")).setExecutor(stats);
        Objects.requireNonNull(getCommand("valracestats")).setTabCompleter(stats);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public void reload() {
        reloadConfig();
        racePoolManager.refresh();
        classPoolManager.refresh();
    }

    public static RandomRacePlugin getInstance() { return instance; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public RacePoolManager getRacePoolManager() { return racePoolManager; }
    public ClassPoolManager getClassPoolManager() { return classPoolManager; }
}

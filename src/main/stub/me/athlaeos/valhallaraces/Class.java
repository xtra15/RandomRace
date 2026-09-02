package me.athlaeos.valhallaraces;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class Class {
    private final String name;
    private final String displayName;
    private final ItemStack icon;
    private final int group;
    private final String permissionRequired;
    private final Collection<String> limitedToRaces;

    public Class(String name, String displayName, ItemStack icon, int group, String permissionRequired, Collection<String> limitedToRaces) {
        this.name = name;
        this.displayName = displayName;
        this.icon = icon;
        this.group = group;
        this.permissionRequired = permissionRequired;
        this.limitedToRaces = limitedToRaces == null ? new ArrayList<>() : new ArrayList<>(limitedToRaces);
    }

    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public ItemStack getIcon() { return icon; }
    public int getGroup() { return group; }
    public String getPermissionRequired() { return permissionRequired; }
    public Collection<String> getLimitedToRaces() { return limitedToRaces; }
}

package me.athlaeos.valhallaraces;

import org.bukkit.inventory.ItemStack;

public class Race {
    private final String name;
    private final String displayName;
    private final ItemStack icon;
    private final String permissionRequired;

    public Race(String name, String displayName, ItemStack icon, String permissionRequired) {
        this.name = name;
        this.displayName = displayName;
        this.icon = icon;
        this.permissionRequired = permissionRequired;
    }

    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public ItemStack getIcon() { return icon; }
    public String getPermissionRequired() { return permissionRequired; }
}

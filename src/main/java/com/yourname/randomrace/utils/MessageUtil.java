package com.yourname.randomrace.utils;

import org.bukkit.ChatColor;

public final class MessageUtil {
    private MessageUtil() {}

    public static String color(String s) {
        if (s == null) return null;
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String replace(String token, String value, String template) {
        if (template == null) return null;
        return template.replace(token, value == null ? "" : value);
    }
}

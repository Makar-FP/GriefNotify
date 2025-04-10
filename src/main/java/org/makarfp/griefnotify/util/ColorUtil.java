package org.makarfp.griefnotify.util;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Pattern;

public class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String color(String message) {

        String processed = HEX_PATTERN.matcher(message).replaceAll(match ->
                ChatColor.of("#" + match.group(1)).toString()
        );

        return ChatColor.translateAlternateColorCodes('&', processed);
    }
}

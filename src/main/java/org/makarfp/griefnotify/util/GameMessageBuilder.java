package org.makarfp.griefnotify.util;

import org.bukkit.ChatColor;

public class GameMessageBuilder {

    public static String buildInGameMessage(String attackerName, String regionName) {
        return ChatColor.RED + "[Важно!] " + ChatColor.GRAY + "Ваш регион '" +
                ChatColor.YELLOW + regionName + ChatColor.GRAY +
                "' прямо сейчас пытаются загриферить. Возможные ники гриферов: " + ChatColor.GOLD + attackerName
                + ChatColor.GRAY + ". Поспешите, чтобы успеть защитить свой регион!";
    }
}

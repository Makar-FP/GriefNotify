package org.makarfp.griefnotify.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.OfflinePlayer;
import org.makarfp.griefnotify.GriefNotify;
import org.makarfp.griefnotify.config.ConfigManager;

public class GriefNotifyPlaceholder extends PlaceholderExpansion {

    private final GriefNotify plugin;

    public GriefNotifyPlaceholder(GriefNotify plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "griefnotify";
    }

    @Override
    public @NotNull String getAuthor() {
        return "YourName";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (identifier.equalsIgnoreCase("info")) {
            ConfigManager configManager = plugin.getConfigManager();
            if (player == null || player.getName() == null) return "Выключены";

            boolean enabled = configManager.isGriefNotifyEnabled(player.getName());
            return enabled ? "Включены" : "Выключены";
        }

        return null;
    }
}
package org.makarfp.griefnotify;

import org.bukkit.plugin.java.JavaPlugin;
import org.makarfp.griefnotify.command.GriefNotifyCommand;
import org.makarfp.griefnotify.config.ConfigManager;
import org.makarfp.griefnotify.data.DatabaseManager;
import org.makarfp.griefnotify.listener.GriefListener;
import org.makarfp.griefnotify.util.TelegramUtil;

public final class GriefNotify extends JavaPlugin {

    private static GriefNotify instance;

    private ConfigManager configManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configManager = new ConfigManager(this);

        TelegramUtil.initialize(configManager);

        databaseManager = new DatabaseManager(this);
        if (!databaseManager.testConnection()) {
            getLogger().warning("Не удалось подключиться к базе данных!");
        }

        getCommand("griefnotify").setExecutor(new GriefNotifyCommand(this));

        getServer().getPluginManager().registerEvents(new GriefListener(this), this);

        getLogger().info("GriefNotify успешно включён.");
    }

    @Override
    public void onDisable() {
        getLogger().info("GriefNotify отключён.");
    }

    public static GriefNotify getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
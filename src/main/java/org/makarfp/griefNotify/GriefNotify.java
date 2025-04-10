package org.makarfp.griefNotify;

import org.bukkit.plugin.java.JavaPlugin;
import org.makarfp.griefNotify.commands.GriefNotifyCommand;
import org.makarfp.griefNotify.config.ConfigManager;
import org.makarfp.griefNotify.data.DatabaseManager;
import org.makarfp.griefNotify.listeners.GriefListener;
import org.makarfp.griefNotify.utils.TelegramUtil;

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
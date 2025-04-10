package org.makarfp.griefNotify.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.makarfp.griefNotify.GriefNotify;
import org.makarfp.griefNotify.utils.ColorUtil;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final GriefNotify plugin;

    private FileConfiguration messagesConfig;
    private File messagesFile;

    private FileConfiguration dataConfig;
    private File dataFile;

    private FileConfiguration telegramConfig;
    private File telegramFile;

    public ConfigManager(GriefNotify plugin) {
        this.plugin = plugin;
        setupConfigs();
    }

    private void setupConfigs() {
        plugin.saveDefaultConfig();

        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            saveEmpty(dataFile);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        telegramFile = new File(plugin.getDataFolder(), "telegram.yml");
        if (!telegramFile.exists()) {
            saveEmpty(telegramFile);
        }
        telegramConfig = YamlConfiguration.loadConfiguration(telegramFile);
    }

    private void saveEmpty(File file) {
        try {
            if (file.createNewFile()) {
                plugin.getLogger().info(file.getName() + " был создан успешно.");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось создать " + file.getName());
            e.printStackTrace();
        }
    }

    public void saveData() {
        saveConfig(dataConfig, dataFile, "data.yml");
    }

    public void saveTelegram() {
        saveConfig(telegramConfig, telegramFile, "telegram.yml");
    }

    public void saveMessages() {
        saveConfig(messagesConfig, messagesFile, "messages.yml");
    }

    private void saveConfig(FileConfiguration config, File file, String name) {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить " + name);
            e.printStackTrace();
        }
    }

    public void reloadAll() {
        plugin.reloadConfig();
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        telegramConfig = YamlConfiguration.loadConfiguration(telegramFile);
    }

    public FileConfiguration getMessages() {
        return messagesConfig;
    }

    public String getColoredMessage(String path) {
        String message = messagesConfig.getString(path);
        return (message == null || message.isEmpty()) ? null : ColorUtil.color(message);
    }

    public FileConfiguration getData() {
        return dataConfig;
    }

    public FileConfiguration getTelegram() {
        return telegramConfig;
    }

    // Утилиты
    public boolean isGriefNotifyEnabled(String playerName) {
        return dataConfig.getBoolean(playerName, false);
    }

    public void setGriefNotifyEnabled(String playerName, boolean enabled) {
        dataConfig.set(playerName, enabled);
        saveData();
    }

    public boolean hasPlayer(String playerName) {
        return dataConfig.contains(playerName);
    }

    public void saveTelegramId(String playerName, long id) {
        telegramConfig.set(playerName, id);
        saveTelegram();
    }

    public Long getTelegramId(String playerName) {
        return telegramConfig.contains(playerName) ? telegramConfig.getLong(playerName) : null;
    }

    public String getMySQLHost() {
        return plugin.getConfig().getString("mysql.host", "localhost");
    }

    public int getMySQLPort() {
        return plugin.getConfig().getInt("mysql.port", 3306);
    }

    public String getMySQLDatabase() {
        return plugin.getConfig().getString("mysql.database");
    }

    public String getMySQLUser() {
        return plugin.getConfig().getString("mysql.user");
    }

    public String getMySQLPassword() {
        return plugin.getConfig().getString("mysql.password");
    }

    public String getBotToken() {
        return plugin.getConfig().getString("telegram.bot_token");
    }
}
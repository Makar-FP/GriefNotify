package org.makarfp.griefnotify.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.makarfp.griefnotify.GriefNotify;
import org.makarfp.griefnotify.check.TelegramCheck;
import org.makarfp.griefnotify.config.ConfigManager;

public class GriefNotifyCommand implements CommandExecutor {

    private final GriefNotify plugin;
    private final ConfigManager configManager;
    private final TelegramCheck telegramCheck;

    public GriefNotifyCommand(GriefNotify plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.telegramCheck = new TelegramCheck();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        if (configManager.hasPlayer(playerName)) {
            boolean currentState = configManager.isGriefNotifyEnabled(playerName);
            configManager.setGriefNotifyEnabled(playerName, !currentState);

            String msgKey = currentState ? "griefnotify.disabled" : "griefnotify.enabled";
            player.sendMessage(configManager.getColoredMessage(msgKey));
            return true;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean linked = telegramCheck.isTelegramLinkHave(playerName);
            if (linked) {
                configManager.setGriefNotifyEnabled(playerName, true);

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.sendMessage(configManager.getColoredMessage("griefnotify.enabled"));
                });
            } else {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.sendMessage(configManager.getColoredMessage("griefnotify.notLinked"));
                });
            }
        });

        return true;
    }
}
package org.makarfp.griefnotify.check;

import org.bukkit.Bukkit;
import org.makarfp.griefnotify.GriefNotify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TelegramCheck {

    public static boolean isTelegramLinkHave(String playerName) {
        try (Connection connection = GriefNotify.getInstance().getDatabaseManager().getConnection()) {

            PreparedStatement accountStmt = connection.prepareStatement(
                    "SELECT id FROM mc_auth_accounts WHERE player_id = ?"
            );
            accountStmt.setString(1, playerName);
            ResultSet accountRs = accountStmt.executeQuery();

            if (!accountRs.next()) {
                Bukkit.getLogger().info("Игрок " + playerName + " не найден в mc_auth_account.");
                return false;
            }

            int accountId = accountRs.getInt("id");

            PreparedStatement linkStmt = connection.prepareStatement(
                    "SELECT link_user_id FROM auth_links WHERE account_id = ? AND link_type = 'TELEGRAM'"
            );
            linkStmt.setInt(1, accountId);
            ResultSet linkRs = linkStmt.executeQuery();

            if (linkRs.next()) {
                long telegramId = linkRs.getLong("link_user_id");
                GriefNotify.getInstance().getConfigManager().saveTelegramId(playerName, telegramId);
                return true;
            }

        } catch (Exception e) {
            Bukkit.getLogger().severe("Ошибка при выполнении запроса к БД: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}

package org.makarfp.griefnotify.data;

import org.makarfp.griefnotify.GriefNotify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private final GriefNotify plugin;

    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    private String jdbcUrl;

    public DatabaseManager(GriefNotify plugin) {
        this.plugin = plugin;
        loadCredentials();
        buildJdbcUrl();
    }

    private void loadCredentials() {
        host = plugin.getConfigManager().getMySQLHost();
        port = plugin.getConfigManager().getMySQLPort();
        database = plugin.getConfigManager().getMySQLDatabase();
        user = plugin.getConfigManager().getMySQLUser();
        password = plugin.getConfigManager().getMySQLPassword();
    }

    private void buildJdbcUrl() {
        jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, user, password);
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            plugin.getLogger().severe("Не удалось подключиться к MySQL: " + e.getMessage());
            return false;
        }
    }
}

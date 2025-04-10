package org.makarfp.griefnotify.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.makarfp.griefnotify.config.ConfigManager;

public class TelegramUtil {

    private static String BOT_TOKEN;

    public static void initialize(ConfigManager configManager) {
        BOT_TOKEN = configManager.getBotToken();
    }

    public static void sendTelegramMessage(long chatId, String message) {
        try {
            if (BOT_TOKEN == null) {
                System.err.println("[Telegram] BOT_TOKEN не инициализирован!");
                return;
            }

            String apiUrl = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            String data = "chat_id=" + chatId + "&text=" + URLEncoder.encode(message, "UTF-8");

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();
            System.out.println("[Telegram] Response: " + responseCode + " - " + responseMessage);

            if (responseCode != 200) {
                System.err.println("[Telegram] Ошибка при отправке сообщения. Код: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
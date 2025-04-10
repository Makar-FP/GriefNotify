package org.makarfp.griefnotify.util;


public class TelegramMessageBuilder {

    public static String build(String attackerName, String regionName) {
        return "⚠️ Оповещение о гриферстве!\nВаш регион '" + regionName
                + "' прямо сейчас пытаются загриферить.\n"
                + "Предположительный ник грифера: " + attackerName + ".\n"
                + "Поспешите, чтобы успеть защитить свой регион.";
    }
}

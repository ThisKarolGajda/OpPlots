package me.opkarol.opplots.utils;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;

import java.time.Duration;
import java.time.Instant;

public class PlayerLastPlayed {
    public static final String PLAYER_LAST_PLAYED_NOW = "teraz";
    public static final String PLAYER_LAST_PLAYED_DAYS_AGO = " dni temu";
    public static final String PLAYER_LAST_PLAYED_DAY_AGO = " dzień temu";
    public static final String PLAYER_LAST_PLAYED_HOURS_AGO = " godzin temu";
    public static final String PLAYER_LAST_PLAYED_HOUR_AGO = " godzinę temu";
    public static final String PLAYER_LAST_PLAYED_MINUTES_AGO = " minut temu";
    public static final String PLAYER_LAST_PLAYED_MINUTE_AGO = " minutę temu";

    public static String getPlayerLastPlayed(OfflinePlayer player) {
        if (player.isOnline()) {
            return PLAYER_LAST_PLAYED_NOW;
        }
        long timestampMillis = player.getLastPlayed();
        String timeAgo = getTimeAgo(timestampMillis);
        String hex = getHexColor(timestampMillis);

        return hex + timeAgo;
    }

    private static String getTimeAgo(long timestampMillis) {
        Instant lastPlayedInstant = Instant.ofEpochMilli(timestampMillis);
        Instant now = Instant.now();
        Duration duration = Duration.between(lastPlayedInstant, now);

        if (duration.toDays() > 0) {
            if (duration.toDays() == 1) {
                return duration.toDays() + PLAYER_LAST_PLAYED_DAY_AGO;
            }
            return duration.toDays() + PLAYER_LAST_PLAYED_DAYS_AGO;
        } else if (duration.toHours() > 0) {
            if (duration.toHours() == 1) {
                return duration.toHours() + PLAYER_LAST_PLAYED_HOUR_AGO;
            }
            return duration.toHours() + PLAYER_LAST_PLAYED_HOURS_AGO;
        } else if (duration.toMinutes() > 0) {
            if (duration.toMinutes() == 1) {
                return duration.toMinutes() + PLAYER_LAST_PLAYED_MINUTE_AGO;
            }
            return duration.toMinutes() + PLAYER_LAST_PLAYED_MINUTES_AGO;
        } else {
            return PLAYER_LAST_PLAYED_NOW;
        }
    }

    private static String getHexColor(long timestampMillis) {
        Instant lastPlayedInstant = Instant.ofEpochMilli(timestampMillis);
        Instant now = Instant.now();
        Duration duration = Duration.between(lastPlayedInstant, now);

        double fraction = Math.min(1.0, (double) duration.toMinutes() / (24 * 60)); // Normalize to [0, 1]
        Color startColor = Color.fromRGB(0x00FF00); // Green
        Color endColor = Color.fromRGB(0xFF0000);   // Red

        int r = (int) (startColor.getRed() + fraction * (endColor.getRed() - startColor.getRed()));
        int g = (int) (startColor.getGreen() + fraction * (endColor.getGreen() - startColor.getGreen()));
        int b = (int) (startColor.getBlue() + fraction * (endColor.getBlue() - startColor.getBlue()));

        return String.format("#<%02X%02X%02X>", r, g, b);
    }
}

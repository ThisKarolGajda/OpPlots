package me.opkarol.opplots.utils;

import java.util.concurrent.TimeUnit;

public class ExpirationConverter {

    public static String getTimeLeftString(long expirationTimestamp) {
        long currentTimeMillis = System.currentTimeMillis();
        long timeDifferenceMillis = expirationTimestamp - currentTimeMillis;

        if (timeDifferenceMillis <= 0) {
            return "Wygasło";
        }

        long days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) % 60;

        StringBuilder builder = new StringBuilder("Za ");
        if (days > 0) {
            builder.append(days).append(" dni ");
        }
        if (hours > 0) {
            builder.append(hours).append(" godzin ");
        }
        if (minutes > 0) {
            builder.append(minutes).append(" minut");
        }

        return builder.toString();
    }
}
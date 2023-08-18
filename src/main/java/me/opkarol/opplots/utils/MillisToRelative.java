package me.opkarol.opplots.utils;

import java.util.concurrent.TimeUnit;

public class MillisToRelative {
    public static String getPolishForm(long value, String singular, String plural, String pluralGenitive) {
        if (value == 1) {
            return "1 " + singular;
        } else if (value % 10 >= 2 && value % 10 <= 4 && (value % 100 < 10 || value % 100 >= 20)) {
            return value + " " + plural;
        } else {
            return value + " " + pluralGenitive;
        }
    }

    public static String millisToRelativeTime(long millis) {
        millis = System.currentTimeMillis() - millis;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long days = TimeUnit.MILLISECONDS.toDays(millis);

        if (days > 0) {
            return String.format("%s %s %s %s",
                    getPolishForm(days, "dzień", "dni", "dni"),
                    getPolishForm(hours % 24, "godzinę", "godziny", "godzin"),
                    getPolishForm(minutes % 60, "minutę", "minuty", "minut"),
                    "temu");
        } else if (hours > 0) {
            return String.format("%s %s %s",
                    getPolishForm(hours, "godzinę", "godziny", "godzin"),
                    getPolishForm(minutes % 60, "minutę", "minuty", "minut"),
                    "temu");
        } else if (minutes > 0) {
            if (minutes < 60) {
                return String.format("za %s %s",
                        getPolishForm(minutes, "minutę", "minuty", "minut"),
                        "temu");
            } else {
                return String.format("za %s %s %s",
                        getPolishForm(minutes / 60, "godzinę", "godziny", "godzin"),
                        getPolishForm(minutes % 60, "minutę", "minuty", "minut"),
                        "temu");
            }
        } else if (seconds > 0) {
            return String.format("za %s %s",
                    getPolishForm(seconds, "sekundę", "sekundy", "sekund"),
                    "temu");
        } else {
            return "przed chwilą";
        }
    }

    public static void main(String[] args) {
        long millis = 1692388902000L;
        String relativeTime = millisToRelativeTime(millis);
        System.out.println(relativeTime);
    }
}

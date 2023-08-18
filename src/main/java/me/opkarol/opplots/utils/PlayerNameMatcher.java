package me.opkarol.opplots.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayerNameMatcher {
    public static List<OfflinePlayer> getSortedPlayersByName(String name) {
        return Arrays.stream(Bukkit.getServer().getOfflinePlayers())
                .distinct()
                .sorted(Comparator.comparingInt(player -> calculateLevenshteinDistance(name, player.getName())))
                .collect(Collectors.toList());
    }

    public static List<OfflinePlayer> getSortedPlayersByName(String name, String excluded) {
        return Arrays.stream(Bukkit.getServer().getOfflinePlayers())
                .filter(player -> !Objects.equals(player.getName(), excluded))
                .distinct()
                .sorted(Comparator.comparingInt(player -> calculateLevenshteinDistance(name, player.getName())))
                .collect(Collectors.toList());
    }

    private static int calculateLevenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int substitutionCost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + substitutionCost,
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }

        return dp[a.length()][b.length()];
    }
}


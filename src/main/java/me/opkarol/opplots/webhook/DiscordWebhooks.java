package me.opkarol.opplots.webhook;

import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.plots.Plot;
import org.bukkit.Bukkit;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DiscordWebhooks {

    public static final String WEBHOOK_URL = OpPlots.getInstance().getFilesManager().getConfigFile().getConfiguration().get("webhook");

    public static void sendPlotChangedNameWebhook(Plot plot, String previousPlotName, boolean systemFoundInappropriate) {
        String author = plot.getOwnerName();
        String uuid = String.valueOf(plot.getOwnerUUID());
        String plotName = plot.getName();
        String homeLocation = plot.getHomeLocation().toFamilyString();

        try {
            new DiscordWebhook(WEBHOOK_URL)
                    .addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle("Działka ma nową nazwę!")
                            .setFooter(author + " (" + uuid + ")", "https://icon-library.com/images/name-tag-icon/name-tag-icon-20.jpg")
                            .setDescription("Nazwa działki: " + plotName + "\\nPoprzednia nazwa: " + previousPlotName + "\\nDom działki: " + homeLocation)
                            .setColor(systemFoundInappropriate ? Color.RED : Color.GREEN))
                    .setTts(true)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendPlotCreatedWebhook(Plot plot) {
        String author = plot.getOwnerName();
        String uuid = String.valueOf(plot.getOwnerUUID());
        String homeLocation = plot.getHomeLocation().toFamilyString();

        try {
            new DiscordWebhook(WEBHOOK_URL)
                    .addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle("Nowa działka stworzona!")
                            .setFooter(author + " (" + uuid + ")", "https://icon-library.com/images/name-tag-icon/name-tag-icon-20.jpg")
                            .setDescription("Dom działki: " + homeLocation)
                            .setColor(Color.BLUE))
                    .setTts(true)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendPlotRemovedWebhook(Plot plot) {
        String author = plot.getOwnerName();
        String uuid = String.valueOf(plot.getOwnerUUID());
        String homeLocation = plot.getHomeLocation().toFamilyString();
        String plotName = plot.getName();
        String ignoredNames = plot.getIgnored().stream().map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName()).collect(Collectors.joining(", "));
        String membersNames = plot.getMembers().stream().map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName()).collect(Collectors.joining(", "));

        try {
            new DiscordWebhook(WEBHOOK_URL)
                    .addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle("Działka usunięta!")
                            .setFooter(author + " (" + uuid + ")", "https://icon-library.com/images/name-tag-icon/name-tag-icon-20.jpg")
                            .setDescription("Nazwa działki: " + plotName + "\\nDom działki: " + homeLocation + "\\nNazwy członków: " + membersNames + "\\nNazwy ignorowanych: " + ignoredNames)
                            .setColor(Color.LIGHT_GRAY))
                    .setTts(true)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendPlotChangedHomeWebhook(Plot plot, String previousHomeLocation) {
        String author = plot.getOwnerName();
        String uuid = String.valueOf(plot.getOwnerUUID());
        String plotName = plot.getName();
        String homeLocation = plot.getHomeLocation().toFamilyString();

        try {
            new DiscordWebhook(WEBHOOK_URL)
                    .addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle("Dom działki zmieniony!")
                            .setFooter(author + " (" + uuid + ")", "https://icon-library.com/images/name-tag-icon/name-tag-icon-20.jpg")
                            .setDescription("Nazwa działki: " + plotName + "\\nDom działki: " + homeLocation + "\\nPoprzedni dom działki: " + previousHomeLocation)
                            .setColor(Color.DARK_GRAY))
                    .setTts(true)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendPlotAddedMemberWebhook(Plot plot, List<UUID> previousMembers, UUID addedUUID) {
        String author = plot.getOwnerName();
        String uuid = String.valueOf(plot.getOwnerUUID());
        String plotName = plot.getName();
        String membersNames = plot.getMembers().stream().map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName()).collect(Collectors.joining(", "));
        String previousMembersNames = previousMembers.stream().map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName()).collect(Collectors.joining(", "));

        try {
            new DiscordWebhook(WEBHOOK_URL)
                    .addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle("Członek działki dodany!")
                            .setFooter(author + " (" + uuid + ")", "https://icon-library.com/images/name-tag-icon/name-tag-icon-20.jpg")
                            .setDescription("Nazwa działki: " + plotName + "\\nCzłonkowie: " + Arrays.toString(plot.getMembers().toArray()) + "\\nPoprzedni członkowie: " + Arrays.toString(previousMembers.toArray()) + "\\nNazwy członków: " + membersNames + "\\nNazwy poprzednich członków: " + previousMembersNames +"\\nNowy członek: " + Bukkit.getOfflinePlayer(addedUUID).getName() + " (" + addedUUID + ")")
                            .setColor(Color.ORANGE))
                    .setTts(true)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendPlotRemovedMemberWebhook(Plot plot, List<UUID> previousMembers, UUID removedUUID) {
        String author = plot.getOwnerName();
        String uuid = String.valueOf(plot.getOwnerUUID());
        String plotName = plot.getName();
        String membersNames = plot.getMembers().stream().map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName()).collect(Collectors.joining(", "));
        String previousMembersNames = previousMembers.stream().map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName()).collect(Collectors.joining(", "));

        try {
            new DiscordWebhook(WEBHOOK_URL)
                    .addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle("Członek działki usunięty!")
                            .setFooter(author + " (" + uuid + ")", "https://icon-library.com/images/name-tag-icon/name-tag-icon-20.jpg")
                            .setDescription("Nazwa działki: " + plotName + "\\nCzłonkowie: " + Arrays.toString(plot.getMembers().toArray()) + "\\nPoprzedni członkowie: " + Arrays.toString(previousMembers.toArray()) + "\\nNazwy członków: " + membersNames + "\\nNazwy poprzednich członków: " + previousMembersNames +"\\nNowy członek: " + Bukkit.getOfflinePlayer(removedUUID).getName() + " (" + removedUUID + ")")
                            .setColor(Color.MAGENTA))
                    .setTts(true)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendPlotChangedIgnoredWebhook(Plot plot, List<UUID> previousIgnored) {
        String author = plot.getOwnerName();
        String uuid = String.valueOf(plot.getOwnerUUID());
        String plotName = plot.getName();
        String ignoredNames = plot.getIgnored().stream().map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName()).collect(Collectors.joining(", "));
        String previousIgnoredNames = previousIgnored.stream().map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName()).collect(Collectors.joining(", "));

        try {
            new DiscordWebhook(WEBHOOK_URL)
                    .addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle("Ignorowani działki zmienieni!")
                            .setFooter(author + " (" + uuid + ")", "https://icon-library.com/images/name-tag-icon/name-tag-icon-20.jpg")
                            .setDescription("Nazwa działki: " + plotName + "\\nIgnorowani: " + Arrays.toString(plot.getMembers().toArray()) + "\\nPoprzedni ignorowani: " + Arrays.toString(previousIgnored.toArray()) + "\\nNazwy ignorowanych: " + ignoredNames + "\\nNazwy poprzednich ignorowanych: " + previousIgnoredNames)
                            .setColor(Color.YELLOW))
                    .setTts(true)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendPlotExpiredWebhook(Plot plot) {
        String author = plot.getOwnerName();
        String uuid = String.valueOf(plot.getOwnerUUID());
        String homeLocation = plot.getHomeLocation().toFamilyString();
        String plotName = plot.getName();
        String creationDate = plot.getFormattedCreationDate();
        String ignoredNames = plot.getIgnored().stream().map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName()).collect(Collectors.joining(", "));
        String membersNames = plot.getMembers().stream().map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName()).collect(Collectors.joining(", "));

        try {
            new DiscordWebhook(WEBHOOK_URL)
                    .addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle("Działka wygasła!")
                            .setFooter(author + " (" + uuid + ")", "https://icon-library.com/images/name-tag-icon/name-tag-icon-20.jpg")
                            .setDescription("Nazwa działki: " + plotName + "\\nDom działki: " + homeLocation + "\\nData stworzenia: " + creationDate + "\\nNazwy członków: " + membersNames + "\\nNazwy ignorowanych: " + ignoredNames)
                            .setColor(Color.green))
                    .setTts(true)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

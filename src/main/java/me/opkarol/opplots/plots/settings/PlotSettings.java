package me.opkarol.opplots.plots.settings;

import me.opkarol.opc.api.file.Configuration;
import me.opkarol.opc.api.map.OpMap;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.utils.StringUtil;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.listener.PlotOptionsRegister;
import me.opkarol.opplots.plots.permissions.PlayerPermissions;
import me.opkarol.opplots.plots.settings.events.SettingChangeCurrentEvent;
import me.opkarol.opplots.plots.settings.types.*;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public class PlotSettings {
    private static final double CHEAPER_SETTINGS = OpPlots.getInstance().getFilesManager().getConfigFile().getConfiguration().getDouble("special.cheaperSettings");
    private final OpMap<Type, Object> current = new OpMap<>();
    private final OpMap<Type, List<Object>> owned = new OpMap<>();

    public PlotSettings(Object ignore) {
        // Default owned elements
        owned.set(Type.PVP_CHANGE, new ArrayList<>(List.of(true, false)));
        owned.set(Type.ANIMALS_SPAWN_CHANGE, new ArrayList<>(List.of(true, false)));
        owned.set(Type.WEATHER_CHANGE, new ArrayList<>(Collections.singletonList(null)));
        owned.set(Type.DAY_TIME_CHANGE, new ArrayList<>(Collections.singletonList(null)));

        // Default current elements
        current.set(Type.PVP_CHANGE, true);
        current.set(Type.ANIMALS_SPAWN_CHANGE, true);
        current.set(Type.WEATHER_CHANGE, null);
        current.set(Type.DAY_TIME_CHANGE, null);
    }

    public PlotSettings() {

    }

    public static PlotSettings fromString(String input) {
        PlotSettings plotSettings = new PlotSettings();
        String[] parts = input.split(";");
        String[] visibleParts = parts[0].split("-");
        if (visibleParts.length != 2) {
            return plotSettings;
        }

        String[] currentParts = visibleParts[1].split(",");
        for (String currentPart : currentParts) {
            String[] typeAndValue = currentPart.split(":");
            int typeOrdinal = Integer.parseInt(typeAndValue[0]);
            String value = typeAndValue[1];
            PlotSettings.Type type = PlotSettings.Type.values()[typeOrdinal];
            Object object = type.getFromString().apply(value);
            plotSettings.current.set(type, object);
        }

        String[] ownedParts = parts[1].split("-")[1].split(",");
        for (String ownedPart : ownedParts) {
            String[] typeAndList = ownedPart.split(":");
            int typeOrdinal = Integer.parseInt(typeAndList[0]);
            String[] objectValues = typeAndList[1].split("\\|");
            PlotSettings.Type type = PlotSettings.Type.values()[typeOrdinal];
            List<Object> objectList = new ArrayList<>();
            for (String value : objectValues) {
                Object object = type.getFromString().apply(value);
                objectList.add(object);
            }
            plotSettings.owned.set(type, objectList);
        }

        return plotSettings;
    }

    public double calculateCostForType(Plot plot, Type type) {
        if (plot.getOwner().getPlayer().hasPermission(PlayerPermissions.CHEAPER_SETTINGS_COST)) {
            return type.getCost() * CHEAPER_SETTINGS;
        }
        return type.getCost();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("current-");
        current.getMap().forEach((type, object) -> sb.append(type.ordinal()).append(":").append(type.getToString().apply(object)).append(","));
        sb.append(";owned-");
        owned.getMap().forEach((type, list) -> {
            sb.append(type.ordinal()).append(":");
            list.forEach(object -> sb.append(type.getToString().apply(object)).append("|"));
            sb.append(",");
        });
        return sb.toString();
    }

    public boolean canAddOwned(Plot plot, UUID uuid) {
        return plot.isOwner(uuid);
    }

    public boolean canAddOwned(Plot plot, Player player) {
        return canAddOwned(plot, player.getUniqueId());
    }

    public void addOwned(Plot plot, Type type, Object object) {
        List<Object> list = owned.getOrDefault(type, new ArrayList<>());
        list.add(object);
        owned.set(type, list);
        plot.updateToDatabase();
    }

    public boolean isOwned(Type type, Object object) {
        return owned.getOrDefault(type, new ArrayList<>()).contains(object);
    }

    public boolean isCurrent(Type type, Object object) {
        return Objects.equals(current.getOrDefault(type, null), object);
    }

    public void setCurrent(Plot plot, Type type, Object object) {
        current.set(type, object);
        if (type.register != null) {
            type.register.registerPlot(plot);
        }
        plot.updateToDatabase();
        Bukkit.getPluginManager().callEvent(new SettingChangeCurrentEvent(plot.getRegionIdentifier(), plot, type, object));
    }

    public void removeData(Plot plot) {
        for (Type type : Type.values()) {
            if (type.register != null) {
                type.register.unregisterPlot(plot);
            }
        }
    }

    public Object getCurrent(Type type) {
        return current.getOrDefault(type, null);
    }

    public void registerListeners(Plot plot) {
        for (Type type : Type.values()) {
            if (type.register != null) {
                type.register.registerPlot(plot);
            }
        }
    }

    public enum Type {
        WEATHER_CHANGE(Constants.CONFIGURATION.getDouble("plot.settings.WEATHER_CHANGE"), WeatherChangeSetting.getInstance(), weatherObject -> weatherObject == null ? null : ((WeatherType) weatherObject).name(), string -> string.equals("null") ? null : WeatherType.valueOf(string), new Tuple[]{Tuple.of("Czysta pogoda", WeatherType.CLEAR), Tuple.of("Opady deszczu", WeatherType.DOWNFALL), Tuple.of("Domyślna pogoda", null)}),
        DAY_TIME_CHANGE(Constants.CONFIGURATION.getDouble("plot.settings.DAY_TIME_CHANGE"), DayTimeChange.getInstance(), String::valueOf, Long::parseLong, new Tuple[]{Tuple.of("Świt", 0L), Tuple.of("Rano", 1000L), Tuple.of("Południe", 6000L), Tuple.of("Zachód słońca", 12000L), Tuple.of("Zmierzch", 13000L), Tuple.of("Noc", 14000L), Tuple.of("Północ", 18000L), Tuple.of("Domyślna pora dnia", null)}),
        PVP_CHANGE(Constants.CONFIGURATION.getDouble("plot.settings.PVP_CHANGE"), PvpChange.getInstance(), Object::toString, StringUtil::getBooleanFromObject, new Tuple[]{Tuple.of("PVP Włączone", true), Tuple.of("PVP Wyłączone", false)}),
        ANIMALS_SPAWN_CHANGE(Constants.CONFIGURATION.getDouble("plot.settings.ANIMALS_SPAWN_CHANGE"), AnimalsSpawnChange.getInstance(), Object::toString, StringUtil::getBooleanFromObject, new Tuple[]{Tuple.of("Spawn zwierząt włączony", true), Tuple.of("Spawn zwierząt wyłączony", false)}),
        ;

        private final Tuple<String, Object>[] objects;
        private final Function<String, Object> fromString;
        private final Function<Object, String> toString;
        private final double cost;
        private final PlotOptionsRegister register;

        Type(double cost, PlotOptionsRegister register, Function<Object, String> toString, Function<String, Object> fromString, Tuple<String, Object>[] objects) {
            this.objects = objects;
            this.toString = toString;
            this.fromString = fromString;
            this.cost = cost;
            this.register = register;
        }

        public Tuple<String, Object>[] getObjects() {
            return objects;
        }

        public double getCost() {
            return cost;
        }

        public Function<String, Object> getFromString() {
            return fromString;
        }

        public Function<Object, String> getToString() {
            return toString;
        }

        public PlotOptionsRegister getRegister() {
            return register;
        }

        private static class Constants {
            public static final Configuration CONFIGURATION = OpPlots.getInstance().getFilesManager().getConfigFile().getConfiguration();
        }
    }
}

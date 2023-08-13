package me.opkarol.plots.upgrades;

import me.opkarol.opc.api.map.OpMap;
import me.opkarol.opc.api.utils.StringUtil;

import java.util.Optional;
import java.util.stream.Collectors;

public class PlotUpgrades {
    private final OpMap<Type, Integer> levels;

    public PlotUpgrades(OpMap<Type, Integer> levels) {
        this.levels = levels;
    }

    public PlotUpgrades() {
        OpMap<Type, Integer> levels = new OpMap<>();
        for (Type type : Type.values()) {
            levels.set(type, 0);
        }
        this.levels = levels;
    }

    public static PlotUpgrades fromString(String string) {
        if (string.isEmpty()) {
            return new PlotUpgrades();
        }

        OpMap<Type, Integer> levels = new OpMap<>();

        String[] strings = string.split(";");
        for (String split : strings) {
            String[] upgrade = split.split("-");
            if (upgrade.length != 2) {
                return new PlotUpgrades();
            }

            Optional<Type> optionalType = StringUtil.getEnumValue(upgrade[0], Type.class);
            if (optionalType.isEmpty()) {
                return new PlotUpgrades();
            }

            int level = StringUtil.getIntFromString(upgrade[1]);
            if (level == -1) {
                return new PlotUpgrades();
            }

            levels.set(optionalType.get(), level);
        }

        return new PlotUpgrades(levels);
    }

    @Override
    public String toString() {
        return levels.keySet()
                .stream()
                .map(type -> type.toString() + levels.unsafeGet(type))
                .collect(Collectors.joining(";"));
    }

    enum Type {
        SIZE_UPGRADE,

    }

}

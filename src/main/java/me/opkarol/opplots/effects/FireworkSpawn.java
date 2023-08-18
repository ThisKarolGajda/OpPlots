package me.opkarol.opplots.effects;

import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opc.api.utils.MathUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class FireworkSpawn {

    private static final int NUM_FIREWORKS = 35;
    private static final double DIAMETER = 30;
    private static final long DELAY_BETWEEN_FIREWORKS = 5;

    public void startFireworkShootout(Location playerLocation) {
        for (int i = 0; i < NUM_FIREWORKS; i++) {
            Location spawnLocation = getSpawnLocation(playerLocation);
            spawnFireworkWithDelay(spawnLocation, (long) i * DELAY_BETWEEN_FIREWORKS);
        }
    }

    private void spawnFireworkWithDelay(Location location, long delay) {
        new OpRunnable(() -> spawnFirework(location)).runTaskLater(delay);
    }

    public void spawnRandomFireworksAroundPlayer(Location playerLocation) {
        for (int i = 0; i < NUM_FIREWORKS; i++) {
            Location spawnLocation = getSpawnLocation(playerLocation);
            spawnFirework(spawnLocation);
        }
    }

    private Location getSpawnLocation(Location playerLocation) {
        double randomAngle = Math.random() * Math.PI * 2; // Random angle in radians
        double randomDistance = Math.random() * (DIAMETER / 2); // Random distance within the diameter radius

        double offsetX = randomDistance * Math.cos(randomAngle);
        double offsetZ = randomDistance * Math.sin(randomAngle);

        return playerLocation.clone().add(offsetX, 0, offsetZ);
    }

    private void spawnFirework(Location location) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .with(FireworkEffect.Type.CREEPER)
                .withColor(Color.BLACK, Color.BLUE, Color.GREEN, Color.YELLOW)
                .withFade(Color.fromRGB(0x44, 0x7c, 0xfc))
                .trail(true)
                .flicker(true)
                .build();

        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);

        // Launch the firework upwards for a more visible effect
        firework.setVelocity(new Vector(0, MathUtils.getRandomInt(1, 10), 0));
    }
}
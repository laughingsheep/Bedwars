package com.jostb.bedwars;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EffectManager {
    private static JavaPlugin plugin;

    public EffectManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    private static final Random random = new Random();
    private static final List<Color> colors = Arrays.asList(
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
            Color.FUCHSIA, Color.PURPLE, Color.YELLOW, Color.AQUA);
    public static void playToAll(String soundID) {
        Sound sound = Sound.sound(Key.key(soundID), Sound.Source.MASTER, 1.0F, 1.0F);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(sound);
        }
    }
    public static void playToPlayer(Player player, String soundID) {
        Sound sound = Sound.sound(Key.key(soundID), Sound.Source.MASTER, 1.0F, 1.0F);
        player.playSound(sound);
    }

    public static void launchFireworksForAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location loc = player.getLocation();
            sendRocket(loc);
            for (int i = 0; i < 20; i++) {
                int delay = i * 10;
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> sendRocket(loc), delay);
            }
        }
    }

    public static void sendRocket(Location location) {
        if (location.getWorld() == null) return;
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(colors.get(random.nextInt(colors.size())))
                .withFade(colors.get(random.nextInt(colors.size())))
                .with(FireworkEffect.Type.BURST)
                .trail(true)
                .flicker(true)
                .build();
        meta.addEffect(effect);
        meta.setPower(3);
        firework.setFireworkMeta(meta);
    }
}

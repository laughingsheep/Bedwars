package com.jostb.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ItemSpawner {
    private final JavaPlugin plugin;
    private int ironTaskId;
    private int goldTaskId;
    private int diamondTaskId;
    private int emeraldTaskId;

    public ItemSpawner(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void startSpawning(){
        World world = Bukkit.getWorld("bedwars"); // or use event.getPlayer().getWorld()

        List<Location> ironLocations = new ArrayList<>();
        List<Location> goldLocations = new ArrayList<>();

        for (TeamInfo team : TeamInfo.teams) {
            if (team.spawnerLocation != null) {
                ironLocations.add(team.spawnerLocation.clone().add(0, 1, 0));
                goldLocations.add(team.spawnerLocation.clone().add(0, 1, 0));
            }
        }
        ItemStack iron = new ItemStack(Material.IRON_INGOT, 1);
        ItemStack gold = new ItemStack(Material.GOLD_INGOT, 1);
        ItemStack diamond = new ItemStack(Material.DIAMOND, 1);
        ItemStack emerald = new ItemStack(Material.EMERALD, 1);

        ironTaskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Location loc : ironLocations) {
                world.dropItem(loc, iron);
            }
        }, 0L, 10L).getTaskId(); // iron drop interval  EIGENTLICH SOGAR 3 DREI DIE SEKUNDE!!!

        goldTaskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Location loc : goldLocations) {
                world.dropItem(loc, gold);
            }
        }, 0L, 100L).getTaskId(); // JEDE 5 Sekunden

        List<Location> diamondLocations = new ArrayList<>();
        diamondLocations.add(new Location(world, 0, 73, 44));
        diamondLocations.add(new Location(world, -44, 73, 0));
        diamondLocations.add(new Location(world, 0, 73, -44));
        diamondLocations.add(new Location(world, 44, 73, 0));

        List<Location> emeraldLocations = new ArrayList<>();
        emeraldLocations.add(new Location(world, -25, 82, -25));
        emeraldLocations.add(new Location(world, 25, 82, -25));
        emeraldLocations.add(new Location(world, 25, 82, 25));
        emeraldLocations.add(new Location(world, -25, 82, 25));
        diamondTaskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Location loc : diamondLocations) {
                world.dropItem(loc, diamond);
            }
        }, 0L, 600L).getTaskId(); // diamond drop interval JEDE 30 Sekunden

        emeraldTaskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Location loc : emeraldLocations) {
                world.dropItem(loc, emerald);
            }
        }, 0L, 1200L).getTaskId(); // emerald drop interval EIGENTLICH JEDE 65 Sekunden
    }

    public void stopSpawning() {
        Bukkit.getScheduler().cancelTask(ironTaskId);
        Bukkit.getScheduler().cancelTask(goldTaskId);
        Bukkit.getScheduler().cancelTask(diamondTaskId);
        Bukkit.getScheduler().cancelTask(emeraldTaskId);
    }
}

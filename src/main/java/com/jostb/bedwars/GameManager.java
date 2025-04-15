package com.jostb.bedwars;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.time.Duration;

import static com.jostb.bedwars.EffectManager.launchFireworksForAll;
import static com.jostb.bedwars.EffectManager.playToAll;

public class GameManager {
    private static ItemSpawner spawner;
    private static JavaPlugin plugin;
    private static boolean isLoadingMap = false;

    public GameManager(ItemSpawner spawner, JavaPlugin plugin) {
        GameManager.spawner = spawner;
        this.plugin = plugin;
    }

    public static void resetPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.setHealth(20.0);
        player.clearActivePotionEffects();
        player.getInventory().setArmorContents(null);
        player.setExp(0.0F);
        player.setSaturation(20.0F);
        player.playerListName(player.name());
        player.displayName(player.name());
        player.customName(player.name());
    }

    public static void personalise(Player player) {
        TeamInfo team = TeamInfo.getTeamByPlayer(player);
        if (team == null) return;

        Color color = team.color;
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta meta;

        meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(color);
        helmet.setItemMeta(meta);

        meta = (LeatherArmorMeta) chest.getItemMeta();
        meta.setColor(color);
        chest.setItemMeta(meta);

        meta = (LeatherArmorMeta) legs.getItemMeta();
        meta.setColor(color);
        legs.setItemMeta(meta);

        meta = (LeatherArmorMeta) boots.getItemMeta();
        meta.setColor(color);
        boots.setItemMeta(meta);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chest);
        player.getInventory().setLeggings(legs);
        player.getInventory().setBoots(boots);
        player.playerListName(player.name().color(team.teamColor));
        player.displayName(player.name().color(team.teamColor));
        player.customName(player.name().color(team.teamColor));
        player.setCustomNameVisible(true);
    }

    public static void placeBed(TeamInfo team) {
        if (team.bedLocation == null || team.bedOrientation == null) return;

        Location bedHead = team.bedLocation;
        Location bedFoot = bedHead.clone().add(team.bedOrientation.getDirection());
        Block headBlock = bedHead.getBlock();
        Block footBlock = bedFoot.getBlock();
        String materialName = team.materialPrefix + "_BED";
        Material bedMaterial = Material.getMaterial(materialName);
        System.out.println("Looking for material: " + materialName + " -> " + (bedMaterial != null ? "Found" : "Not Found"));
        footBlock.setType(bedMaterial);
        headBlock.setType(bedMaterial);
        Bed headData = (Bed) headBlock.getBlockData();
        headData.setPart(Bed.Part.FOOT);
        headData.setFacing(team.bedOrientation);
        headBlock.setBlockData(headData);

        Bed footData = (Bed) footBlock.getBlockData();
        footData.setPart(Bed.Part.HEAD);
        footData.setFacing(team.bedOrientation);
        footBlock.setBlockData(footData);
    }

    public static void checkForWin() {
        Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
        long remainingTeams = TeamInfo.teams.stream().filter(t -> !t.eliminated).count();
        System.out.println("Remaining teams: " + remainingTeams);
        for(TeamInfo team : TeamInfo.teams) {
            System.out.println("Team: " + team.teamName + ", Eliminated: " + team.eliminated);
        }
        if (remainingTeams < 2) {
            TeamInfo winningTeam = TeamInfo.teams.stream().filter(t -> !t.eliminated).findFirst().orElse(null);
            if (winningTeam != null) {
                Title winTitle = Title.title(
                        Component.text("Game Over!", NamedTextColor.GREEN, TextDecoration.BOLD),
                        Component.text(winningTeam.teamName + " has won the game!", NamedTextColor.GRAY),
                        times
                );
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.showTitle(winTitle);
                }
            }
            playToAll("ui.toast.challenge_complete");
            launchFireworksForAll();
            plugin.getServer().getScheduler().runTaskLater(plugin, GameManager::endGame, 400L);
        }
    }

    public static void endGame() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            resetPlayer(player);
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
        spawner.stopSpawning();
        WorldManager.deleteMap();
        isLoadingMap = true;
        WorldManager.createMap();
        isLoadingMap = false;
        TeamInfo.resetTeams();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                resetPlayer(player);
                player.sendMessage(Component.text("Ready for next game!", NamedTextColor.GREEN));
            }
        }, 2L);
    }

    public static void startGame() {
        if (isLoadingMap) {
            System.out.println("Map is currently loading. Please wait...");
            return;
        }
        isLoadingMap = true;

        //Assign random single player Teams
        WorldManager.createMap();
        isLoadingMap = false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Component.text("Starting game!", NamedTextColor.GREEN));

            TeamInfo template = TeamInfo.getRandomUnusedTemplateTeam();
            if (template == null) {
                player.sendMessage(Component.text("No more teams available!", NamedTextColor.RED));
                continue;
            }

            TeamInfo team = TeamInfo.addTeamFromTemplate(template.teamName);
            team.players.add(player);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            resetPlayer(player);
            personalise(player);
        }
        //Place beds
        for (TeamInfo team : TeamInfo.teams) {
            placeBed(team);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(TeamInfo.getTeamByPlayer(player).respawnLocation);
        }
        spawner.startSpawning();
    }
}

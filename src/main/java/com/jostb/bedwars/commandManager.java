package com.jostb.bedwars;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.jostb.bedwars.GameManager.endGame;
import static com.jostb.bedwars.GameManager.startGame;

import java.io.File;

public class commandManager implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (sender instanceof Player player) {
            if (command.getName().equalsIgnoreCase("start")) {
              startGame();
              player.sendMessage(ChatColor.GREEN + "Game started!");
            } else if (command.getName().equalsIgnoreCase("end")) {
                endGame();
                player.sendMessage(ChatColor.YELLOW + "Ended game!");
            } else if (command.getName().equalsIgnoreCase("mtp")) {
                if (!player.isOp()) {
                    player.sendMessage(ChatColor.RED + "You must be an operator to use this command.");
                    return true;
                }
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "Usage: /mtp <worldname>");
                    return true;
                }
                World world = Bukkit.getWorld(args[0]);
                if (world == null) {
                    File worldFolder = new File(Bukkit.getWorldContainer(), args[0]);
                    if (worldFolder.exists() && worldFolder.isDirectory()) {
                        WorldCreator creator = new WorldCreator(args[0]);
                        world = Bukkit.createWorld(creator);
                    }
                }
                if (world == null) {
                    player.sendMessage(ChatColor.RED + "World '" + args[0] + "' not found.");
                    return true;
                }
                Location spawn = world.getSpawnLocation();
                player.teleport(spawn);
                player.sendMessage(ChatColor.YELLOW + "Teleported to spawn of world '" + args[0] + "'.");
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Unknown command.");
            }
        } else {
            sender.sendMessage("This command can only be used by players.");
        }
        return true;
    }
}

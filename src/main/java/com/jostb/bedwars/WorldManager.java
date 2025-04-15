package com.jostb.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;

public class WorldManager {
    public static void copyWorld(File source, File target) throws IOException {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdirs();
            }

            String[] files = source.list();
            if (files != null) {
                for (String file : files) {
                    if (file.equals("uid.dat") || file.equals("session.lock")) continue; // skip server-specific files
                    copyWorld(new File(source, file), new File(target, file));
                }
            }
        } else {
            java.nio.file.Files.copy(source.toPath(), target.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }
    public static void createMap(){
        File source = new File(Bukkit.getWorldContainer(), "Bloom");
        File target = new File(Bukkit.getWorldContainer(), "bedwars");

        if (target.exists()) {
            return;
        }

        try {
            WorldManager.copyWorld(source, target);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        WorldCreator creator = new WorldCreator("bedwars");
        World newWorld = creator.createWorld();
        newWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        newWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
    }
    public static void deleteMap() {
        World world = Bukkit.getWorld("bedwars");
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        File worldFolder = new File(Bukkit.getWorldContainer(), "bedwars");
        deleteDirectory(worldFolder);
    }

    private static void deleteDirectory(File path) {
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        try {
            java.nio.file.Files.deleteIfExists(path.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

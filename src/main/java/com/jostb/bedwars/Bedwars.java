package com.jostb.bedwars;

import org.bukkit.plugin.java.JavaPlugin;

public final class Bedwars extends JavaPlugin {

    @Override
    public void onEnable() {
        new Shop(this);
        new BlockProtection(this); // Register block break listener
        new RespawnHandler(this); // Register respawn handler
        new SpecialItemHandler(this);
        new EffectManager(this);
        new GameManager(new ItemSpawner(this), this);
        getCommand("start").setExecutor(new commandManager());
        getCommand("end").setExecutor(new commandManager());
        getCommand("mtp").setExecutor(new commandManager());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}

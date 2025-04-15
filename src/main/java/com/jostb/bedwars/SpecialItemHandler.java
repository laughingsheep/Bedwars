package com.jostb.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.List;

public class SpecialItemHandler implements Listener {
    private final List<String> breakableBlockNames = List.of(
            "WOOL", "CONCRETE", "GLASS", "END_STONE", "LADDER", "ACACIA_PLANKS", "OBSIDIAN", "BED"
    );
    public SpecialItemHandler(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.TNT) {
            event.getBlockPlaced().setType(Material.AIR);
            TNTPrimed tnt = event.getBlockPlaced().getWorld().spawn(event.getBlockPlaced().getLocation().add(0.5, 0, 0.5), TNTPrimed.class);
            tnt.setFuseTicks(80); // Optional: default fuse time (4 seconds)
        }
    }

    @EventHandler
    public void onPlayerUseFireball(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // Only main hand
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.FIRE_CHARGE) {
            // Remove one fireball from inventory
            item.setAmount(item.getAmount() - 1);

            // Launch fireball
            Fireball fireball = player.launchProjectile(Fireball.class);
            fireball.setDirection(player.getLocation().getDirection().normalize());
            fireball.setYield(1.5F); // Explosion power
            fireball.setIsIncendiary(false);
            fireball.setShooter(player);

            event.setCancelled(true); // Prevent default use behavior
        }
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() == org.bukkit.entity.EntityType.SNOWBALL) {
            Location location = event.getEntity().getLocation();
            location.getWorld().spawnEntity(location, org.bukkit.entity.EntityType.SILVERFISH);
        }
    }
}

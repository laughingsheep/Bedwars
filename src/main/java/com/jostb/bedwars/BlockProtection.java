package com.jostb.bedwars;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.entity.Fireball;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BlockProtection implements Listener {

    private final List<String> breakableBlockNames = List.of(
        "WOOL", "CONCRETE", "GLASS", "END_STONE", "LADDER", "ACACIA_PLANKS", "OBSIDIAN", "BED"
    );
    public BlockProtection(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onFireballHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball fireball) {
            ProjectileSource shooter = fireball.getShooter();
            Location location = fireball.getLocation();

            // Create explosion (doesn't break blocks directly)
            TNTPrimed tnt = (TNTPrimed) fireball.getWorld().spawn(location, TNTPrimed.class);
            tnt.setFuseTicks(0);
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

        String blockName = event.getBlock().getType().name();
        boolean allowed = breakableBlockNames.stream().anyMatch(blockName::contains);
        if (!allowed) {
            event.setCancelled(true);
        }

        if (blockName.contains("BED")) {
            event.setDropItems(false);
            TeamInfo team = TeamInfo.getTeamByBedLocation(event.getBlock().getLocation());
            if (team != null && !team.bedDestroyed) {
                team.bedDestroyed = true;
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
                TextComponent titleHeader = Component.text("Bed destroyed by ")
                        .color(NamedTextColor.RED)
                        .append(Component.text(event.getPlayer().getName(), TeamInfo.getTeamByPlayer(event.getPlayer()).teamColor, TextDecoration.BOLD))
                        .append(Component.text("!")
                                .color(NamedTextColor.RED)
                        );
                Title title = Title.title(
                        titleHeader,
                        Component.text("You can no longer respawn", NamedTextColor.GRAY),
                        times
                );
                for (Player p : team.players) {
                    p.showTitle(title);
                }
                ScoreboardHandler.update();
            }
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            String blockName = block.getType().name();
            boolean isAllowed = breakableBlockNames.stream().anyMatch(blockName::contains);
            boolean isBed = blockName.contains("BED");
            return !isAllowed || isBed;
        });
    }
}

package com.jostb.bedwars;

import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import java.time.Duration;

public class RespawnHandler implements Listener {
    private final JavaPlugin plugin;

    public RespawnHandler(JavaPlugin plugin) {
      this.plugin = plugin;
      Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        Player player = event.getPlayer();
        player.setSaturation(19.0f);
        player.setFoodLevel(19);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        TeamInfo team = TeamInfo.getTeamByPlayer(player);
        if (team != null && team.respawnLocation != null) {
            event.setRespawnLocation(team.respawnLocation);
        }
    }

    @EventHandler
    public void onEntityDamage(org.bukkit.event.entity.EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            event.setDamage(event.getDamage() / 2);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
      Player player = event.getPlayer();
      final ItemStack[] savedArmor = player.getInventory().getArmorContents().clone();
      event.getDrops().removeIf(item -> item.getType().name().endsWith("_HELMET")
          || item.getType().name().endsWith("_CHESTPLATE")
          || item.getType().name().endsWith("_LEGGINGS")
          || item.getType().name().endsWith("_BOOTS"));
      player.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
      // Set to spectator mode
      player.setGameMode(GameMode.SPECTATOR);
      TeamInfo team = TeamInfo.getTeamByPlayer(player);
      if (team != null && team.respawnLocation != null) {
        player.teleport(team.respawnLocation);
      }
      Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
      if(team.bedDestroyed) {
          Title title = Title.title(
                  Component.text("Out of the game!", NamedTextColor.RED, TextDecoration.BOLD),
                  Component.text("You've died and your bed is gone", NamedTextColor.GRAY),
                  times
          );
          player.showTitle(title);
          team.deadPlayers.add(player);
          if(team.players.size() == team.deadPlayers.size()){
              Title title2 = Title.title(
                      Component.text("Game lost!", NamedTextColor.RED, TextDecoration.BOLD),
                      Component.text("Everyone in your team is dead", NamedTextColor.GRAY),
                      times
              );
              for(Player p : team.players) {
                  p.showTitle(title2);
              }
              team.eliminated = true;
          }
          GameManager.checkForWin();
          return;
      }
      Title title = Title.title(
          Component.text("You've died!", NamedTextColor.RED, TextDecoration.BOLD),
          Component.text("Respawning in 5 seconds...", NamedTextColor.GRAY),
          times
      );
      player.showTitle(title);

      // Schedule respawn
      new BukkitRunnable() {
          @Override
          public void run() {
              TeamInfo team = TeamInfo.getTeamByPlayer(player);
              //Print the team name to the console
              Bukkit.getLogger().info(team.teamName);
              if (team != null && team.respawnLocation != null) {
                  player.teleport(team.respawnLocation);
              }
              player.setGameMode(GameMode.SURVIVAL);
              Bukkit.getScheduler().runTaskLater(plugin, () -> {
                  player.getInventory().setArmorContents(savedArmor);
              }, 1L);
          }
      }.runTaskLater(plugin, 20 * 5L);
  }

  @EventHandler
  public void onItemDamage(PlayerItemDamageEvent event) {
      event.setCancelled(true);
  }

  @EventHandler
  public void onAdvancement(PlayerAdvancementDoneEvent event) {
      event.getPlayer().getAdvancementProgress(event.getAdvancement()).revokeCriteria(event.getAdvancement().getCriteria().toString());
  }
}

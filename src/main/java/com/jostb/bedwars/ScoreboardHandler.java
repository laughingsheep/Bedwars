package com.jostb.bedwars;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class ScoreboardHandler {
    private static JavaPlugin plugin;
    static ScoreboardLibrary scoreboardLibrary;
    static Sidebar sidebar;
    static ComponentSidebarLayout layout;

    public ScoreboardHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void createSidebar() {
        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
        } catch (NoPacketAdapterAvailableException e) {
            scoreboardLibrary = new NoopScoreboardLibrary();
            plugin.getLogger().warning("No scoreboard packet adapter available!");
        }
        SimpleDateFormat dtf = new SimpleDateFormat("dd.MM.yyyy");
        SidebarComponent.Builder sidebarBuilder = SidebarComponent.builder()
            .addDynamicLine(() -> {
                var time = dtf.format(new Date());
                return Component.text(time, NamedTextColor.GRAY);
            });
        sidebarBuilder.addBlankLine();
        for (var team : TeamInfo.teams) {
            sidebarBuilder.addDynamicLine(() -> {
                char firstLetter = team.teamName.toUpperCase().charAt(0);
                String name = team.players.size() == 1
                        ? team.players.getFirst().getName()
                        : team.teamName;

                TextComponent component = Component.text(firstLetter + " ", team.teamColor)
                        .append(
                                Component.text(name + ":", NamedTextColor.WHITE)
                                        .decoration(TextDecoration.STRIKETHROUGH, team.eliminated)
                        );

                if (team.eliminated) {
                    component = component.append(Component.text(" ✘", NamedTextColor.RED));
                } else if (team.bedDestroyed) {
                    int playersLeft = team.players.size() - team.deadPlayers.size();
                    component = component.append(Component.text(" " + playersLeft, NamedTextColor.YELLOW));
                } else {
                    component = component.append(Component.text(" ✔", NamedTextColor.GREEN));
                }

                return component;
            });
        }
        sidebarBuilder.addBlankLine();
        sidebarBuilder.addStaticLine(
                Component.text("bedwars.party")
                        .color(NamedTextColor.GOLD)
        );
        SidebarComponent lines = sidebarBuilder.build();
        layout = new ComponentSidebarLayout(
                SidebarComponent.staticLine(
                        Component.text("BEDWARS PARTY")
                                .color(NamedTextColor.GOLD)
                                .decoration(TextDecoration.BOLD, true)
                ),
                lines
        );
        sidebar = scoreboardLibrary.createSidebar();
        layout.apply(sidebar);
    }

    public static void update() {
        if (sidebar != null && layout != null) {
            layout.apply(sidebar);
        }
    }

    public static void show() {
        if (sidebar == null) return;
        org.bukkit.Bukkit.getOnlinePlayers().forEach(player -> sidebar.addPlayer(player));
    }

    public static void hide() {
        if (sidebar != null) {
            sidebar.removePlayers((Collection<Player>) Bukkit.getOnlinePlayers());
            sidebar.close();
        }
    }
}

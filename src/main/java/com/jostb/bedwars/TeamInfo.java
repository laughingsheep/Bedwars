package com.jostb.bedwars;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockFace;
import java.util.List;
import java.util.ArrayList;

public class TeamInfo {
    public NamedTextColor teamColor;
    public Color color;
    public Location respawnLocation;
    public Location spawnerLocation;
    public String teamName;
    public List<Player> players;
    public List<Player> deadPlayers;
    public Location bedLocation;
    public BlockFace bedOrientation;
    public Boolean bedDestroyed;
    public String materialPrefix;
    public Boolean eliminated;
    public static List<TeamInfo> teams = new ArrayList<>();
    public static List<TeamInfo> templateTeams = new ArrayList<>();

    static {
        templateTeams.add(createDummyTeam(
                "Aqua",
                NamedTextColor.AQUA,
                Color.AQUA,
                "CYAN",
                new Location(null, -106, 80, 28),
                new Location(null, -88, 81, 26),
                BlockFace.WEST)
        );
        templateTeams.add(createDummyTeam(
                "Red",
                NamedTextColor.RED,
                Color.RED,
                "RED",
                new Location(null, 106, 80, -28),
                new Location(null, 88, 81, -26),
                BlockFace.EAST)
        );
        templateTeams.add(createDummyTeam(
                "Pink",
                NamedTextColor.LIGHT_PURPLE,
                Color.FUCHSIA,
                "PINK",
                new Location(null, -28, 80, -106),
                new Location(null, -26, 81, -88),
                BlockFace.NORTH)
        );
        templateTeams.add(createDummyTeam(
                "Green",
                NamedTextColor.DARK_GREEN,
                Color.GREEN,
                "GREEN",
                new Location(null, 28, 80, 106),
                new Location(null, 26, 81, 88),
                BlockFace.SOUTH)
        );
        //TeamInfo redTeam = teams.get(0);
        //redTeam.players.add(org.bukkit.Bukkit.getPlayer("Lustiges_Bild"));
    }

    private static TeamInfo createDummyTeam(String name, NamedTextColor textColor, Color color, String materialPrefix, Location spawnerLocation, Location bedLocation, BlockFace bedOrientation) {
        TeamInfo team = new TeamInfo();

        team.teamName = name;
        team.color = color;
        team.teamColor = textColor;
        team.materialPrefix = materialPrefix;

        org.bukkit.World world = org.bukkit.Bukkit.getWorld("bedwars");

        team.spawnerLocation = spawnerLocation;
        team.respawnLocation = team.spawnerLocation;
        team.bedLocation = bedLocation;
        team.bedOrientation = bedOrientation;

        team.eliminated = false;
        team.players = new ArrayList<>();
        team.bedDestroyed = false;
        team.deadPlayers = new ArrayList<>();

        return team;
    }
    public static void resetTeams(){
        teams.clear();
    }
    public static TeamInfo getTeamByPlayer(Player player) {
        for (TeamInfo team : teams) {
            for (Player p : team.players) {
                if (p.equals(player)) {
                    return team;
                }
            }
        }
        return null;
    }

    public static TeamInfo getTeamByBedLocation(Location loc) {
        for (TeamInfo team : teams) {
            Location bed1 = team.bedLocation;
            Location bed2 = bed1.clone().add(team.bedOrientation.getModX(), team.bedOrientation.getModY(), team.bedOrientation.getModZ());

            if (loc.equals(bed1) || loc.equals(bed2)) {
                return team;
            }
        }
        return null;
    }

    public static TeamInfo addTeamFromTemplate(String name) {
        for (TeamInfo template : templateTeams) {
            if (template.teamName.equalsIgnoreCase(name)) {
                TeamInfo clone = cloneTeam(template);
                teams.add(clone);
                return clone;
            }
        }
        return null;
    }

    public static TeamInfo getRandomUnusedTemplateTeam() {
        for (TeamInfo template : templateTeams) {
            boolean used = teams.stream().anyMatch(team -> team.teamName.equalsIgnoreCase(template.teamName));
            if (!used) {
                return template;
            }
        }
        return null;
    }

    public static TeamInfo getTeamByName(String name) {
        for (TeamInfo team : teams) {
            if (team.teamName.equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }

    private static TeamInfo cloneTeam(TeamInfo original) {
        TeamInfo clone = new TeamInfo();
        clone.teamColor = original.teamColor;
        clone.color = original.color;
        clone.respawnLocation = original.respawnLocation.clone();
        clone.spawnerLocation = original.spawnerLocation.clone();
        clone.teamName = original.teamName;
        clone.materialPrefix = original.materialPrefix;
        clone.bedLocation = original.bedLocation.clone();
        clone.bedOrientation = original.bedOrientation;
        clone.players = new ArrayList<>();
        clone.deadPlayers = new ArrayList<>();
        clone.eliminated = false;
        clone.bedDestroyed = false;

        org.bukkit.World world = org.bukkit.Bukkit.getWorld("bedwars");
        clone.respawnLocation.setWorld(world);
        clone.spawnerLocation.setWorld(world);
        clone.bedLocation.setWorld(world);

        return clone;
    }
}

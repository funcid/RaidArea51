package ru.func.raidarea.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.func.raidarea.RaidArea;
import ru.func.raidarea.RaidTimeStatus;
import ru.func.raidarea.character.NarutoRunner;
import ru.func.raidarea.player.PlayerBuilder;
import ru.func.raidarea.player.RaidPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionListener implements Listener {

    private final RaidArea PLUGIN;

    public ConnectionListener(RaidArea plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        loadStats(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        saveStats(e.getPlayer(), 0);
    }

    public void loadStats(Player player) {
        if (PLUGIN.getTimeStatus().equals(RaidTimeStatus.WAITING) || PLUGIN.getTimeStatus().equals(RaidTimeStatus.STARTING)) {
            try {
                ResultSet resultSet = PLUGIN.getStatement().executeQuery("SELECT * FROM `RaidPlayers` WHERE uuid = '" + player.getUniqueId() + "';");
                if (resultSet.next()) {
                    PLUGIN.getPlayers().put(player.getUniqueId(), new PlayerBuilder()
                            .clef(resultSet.getInt("clef"))
                            .kills(resultSet.getInt("kills"))
                            .money(resultSet.getInt("money"))
                            .wins(resultSet.getInt("wins"))
                            .level(resultSet.getInt("level"))
                            .characters(resultSet.getString("characters").split(","))
                            .currentCharacter(new NarutoRunner())
                            .defend(false)
                            .build()
                    );
                } else {
                    //Создает новый профиль в базе данных
                    // uuid TEXT, money INT, characters TEXT, clef INT, kills INT, wins INT
                    PLUGIN.getStatement().executeUpdate("INSERT INTO `RaidPlayers` (uuid, money, characters, clef, level, kills, wins) VALUES(" +
                            "'" + player.getUniqueId() + "', " +
                            "0, " +
                            "'Naruto Runner,'," +
                            "0, " +
                            "0, " +
                            "0, " +
                            "0);");
                    loadStats(player);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(PLUGIN.getRaidSpawn());
        }
    }

    private void saveStats(Player player, int i) {
        if (PLUGIN.getPlayers().containsKey(player.getUniqueId())) {
            RaidPlayer raidPlayer = PLUGIN.getPlayers().get(player.getUniqueId());
            try {
                ResultSet resultSet = PLUGIN.getStatement().executeQuery("SELECT * FROM `RaidPlayers` WHERE uuid = '" + player.getUniqueId() + "';");
                if (resultSet.next())
                    PLUGIN.getStatement().executeUpdate("UPDATE `RaidPlayers` SET " +
                            "money = '" + raidPlayer.getMoney() + "', " +
                            "characters = '" + raidPlayer.getStringAllowCharacters() + "', " +
                            "clef = '" + raidPlayer.getClef() + "', " +
                            "level = '" + raidPlayer.getLevel() + "', " +
                            "kills = '" + raidPlayer.getKills() + "', " +
                            "wins = '" + raidPlayer.getWins() + "' " +
                            "WHERE uuid = '" + player.getUniqueId() + "';");
                Bukkit.getLogger().info(player.getName() + " сохранен.");
            } catch (SQLException e) {
                if (i < 3)
                    saveStats(player, ++i);
            }
            PLUGIN.getPlayers().remove(player.getUniqueId());
        }
    }
}

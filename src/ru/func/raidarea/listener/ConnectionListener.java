package ru.func.raidarea.listener;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.scheduler.BukkitRunnable;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.RaidTimeStatus;
import ru.func.raidarea.character.NarutoRunner;
import ru.func.raidarea.player.IPlayer;
import ru.func.raidarea.player.PlayerBuilder;
import ru.func.raidarea.player.RaidPlayer;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionListener implements Listener {

    private final RaidArea PLUGIN;

    public ConnectionListener(final RaidArea plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        e.setJoinMessage(null);
        loadStats(e.getPlayer());
        if (!PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME)) {
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            e.getPlayer().getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        e.getPlayer().getInventory().clear();
        e.setQuitMessage(null);
        saveStats(e.getPlayer(), 0);
    }

    public void loadStats(final Player player) {
        if (PLUGIN.getTimeStatus().equals(RaidTimeStatus.WAITING) || PLUGIN.getTimeStatus().equals(RaidTimeStatus.STARTING)) {
            try {
                ResultSet resultSet = PLUGIN.getStatement().executeQuery("SELECT * FROM `RaidPlayers` WHERE uuid = '" + player.getUniqueId() + "';");
                if (resultSet.next()) {
                    PLUGIN.getPlayers().put(player.getUniqueId(), new PlayerBuilder()
                            .kills(resultSet.getInt("kills"))
                            .money(resultSet.getInt("money"))
                            .wins(resultSet.getInt("wins"))
                            .currentCharacter(new NarutoRunner())
                            .defend(false)
                            .build());
                    enableScoreboard(player);
                } else {
                    //Создает новый профиль в базе данных
                    // uuid TEXT, money INT, characters TEXT, clef INT, kills INT, wins INT
                    PLUGIN.getStatement().executeUpdate("INSERT INTO `RaidPlayers` (uuid, money, kills, wins) VALUES(" +
                            "'" + player.getUniqueId() + "', " +
                            "1000, " +
                            "0, " +
                            "0);");
                    loadStats(player);
                }
            } catch (Exception e) { }
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(PLUGIN.getRaidSpawn());
        }
    }

    public void saveStats(final Player player, int i) {
        if (PLUGIN.getPlayers().containsKey(player.getUniqueId())) {
            RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(player.getUniqueId());
            try {
                ResultSet resultSet = PLUGIN.getStatement().executeQuery("SELECT * FROM `RaidPlayers` WHERE uuid = '" + player.getUniqueId() + "';");
                if (resultSet.next())
                    PLUGIN.getStatement().executeUpdate("UPDATE `RaidPlayers` SET " +
                            "money = '" + raidPlayer.getMoney() + "', " +
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

    private void enableScoreboard(final Player player) {
        /* С самного начала этот код понимали я и Бог,
        прошло 10 дней, отсался только Бог
        (лан шучу, это просто)
        С кем я вообще болтаю? Ахаха
        - Со мной
        - Но.. Кто ты?
        - Ты это я, только я не пишу говнокода
        - Потому что ты пишешь комментарии, а не код, дибил
        */
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        Scoreboard scoreboard = new Scoreboard();

        ScoreboardObjective objective = scoreboard.registerObjective("§lШТУРМ ЗОНЫ 51", IScoreboardCriteria.b);
        PacketPlayOutScoreboardObjective createObj = new PacketPlayOutScoreboardObjective(objective, 0);
        PacketPlayOutScoreboardObjective removeObj = new PacketPlayOutScoreboardObjective(objective, 1);
        PacketPlayOutScoreboardDisplayObjective display = new PacketPlayOutScoreboardDisplayObjective(1, objective);

        ScoreboardScore null1Score = new ScoreboardScore(scoreboard, objective, " ");
        null1Score.setScore(13);
        ScoreboardScore playerScore = new ScoreboardScore(scoreboard, objective, "§c§lИгрок:");
        playerScore.setScore(12);
        ScoreboardScore nameScore = new ScoreboardScore(scoreboard, objective, "§lИмя: §6" + player.getName());
        nameScore.setScore(11);
        ScoreboardScore null2Score = new ScoreboardScore(scoreboard, objective, "  ");
        null2Score.setScore(6);
        ScoreboardScore informationScore = new ScoreboardScore(scoreboard, objective, "§c§lИнформация:");
        informationScore.setScore(5);
        ScoreboardScore null3Score = new ScoreboardScore(scoreboard, objective, "   ");
        null3Score.setScore(0);

        IPlayer raidPlayer = PLUGIN.getPlayers().get(player.getUniqueId());

        playerConnection.sendPacket(removeObj);
        playerConnection.sendPacket(createObj);
        playerConnection.sendPacket(display);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                ScoreboardScore characterScore = new ScoreboardScore(scoreboard, objective, "§lПерсонаж: " + (PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME) ? raidPlayer.getCurrentCharacter().getName() : "§6Не подобран"));
                characterScore.setScore(10);
                ScoreboardScore moneyScore = new ScoreboardScore(scoreboard, objective, "§lETH: §e§l" + raidPlayer.getMoney());
                moneyScore.setScore(9);
                ScoreboardScore killsScore = new ScoreboardScore(scoreboard, objective, "§lУбийств: §6" + raidPlayer.getKills());
                killsScore.setScore(8);
                ScoreboardScore winsScore = new ScoreboardScore(scoreboard, objective, "§lПобед: §6" + raidPlayer.getWins());
                winsScore.setScore(7);
                ScoreboardScore timeStatusScore = new ScoreboardScore(scoreboard, objective, "§lСостояние игры: §6" + PLUGIN.getTimeStatus().getName());
                timeStatusScore.setScore(4);
                ScoreboardScore gameStatusScore = new ScoreboardScore(scoreboard, objective, "§lСтатус: §6" + PLUGIN.getStatus().getName());
                gameStatusScore.setScore(3);
                ScoreboardScore onlineScore = new ScoreboardScore(scoreboard, objective, "§lИгроков: §b§l" + Bukkit.getOnlinePlayers().size());
                onlineScore.setScore(2);
                ScoreboardScore timeScore = new ScoreboardScore(scoreboard, objective, "§lВремя: §b§l" + secondsToString(Math.abs(PLUGIN.getTime() - RaidTimeStatus.STARTING.getTime())));
                timeScore.setScore(1);

                playerConnection.sendPacket(removeObj);
                playerConnection.sendPacket(createObj);
                playerConnection.sendPacket(display);

                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(null1Score));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(playerScore));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(nameScore));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(timeStatusScore));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(gameStatusScore));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(moneyScore));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(null2Score));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(informationScore));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(killsScore));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(onlineScore));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(characterScore));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(timeScore));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(null3Score));
                playerConnection.sendPacket(new PacketPlayOutScoreboardScore(winsScore));
            }
        }.runTaskTimerAsynchronously(PLUGIN, 0, 20L);
    }

    private String secondsToString(final int pTime) {
        return String.format("%02d:%02d", pTime / 60, pTime % 60);
    }
}

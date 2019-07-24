package ru.func.raidarea;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.func.raidarea.character.ElonMusk;
import ru.func.raidarea.character.ICharacter;
import ru.func.raidarea.character.NarutoRunner;
import ru.func.raidarea.character.Soldier;
import ru.func.raidarea.database.MySQL;
import ru.func.raidarea.listener.*;
import ru.func.raidarea.player.RaidPlayer;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RaidArea extends JavaPlugin {

    private Random random = new Random();
    private RaidTimeStatus TIME_STATUS = RaidTimeStatus.WAITING;
    private RaidStatus STATUS = RaidStatus.ACTIVE_STATION;
    private ConnectionListener connectionListener = new ConnectionListener(this);
    private final ConfigurationSection SETTINGS = getConfig().getConfigurationSection("settings");
    private int TIME = 0;
    private final ICharacter soldier = new Soldier();

    private Location defSpawn;
    private Location raidSpawn;
    private Location toggleLocation;

    private boolean attackersWin = false;

    private Map<UUID, RaidPlayer> players = Maps.newHashMap();

    /* SQL переменные */
    private Statement STATEMENT;
    private final ConfigurationSection sqLSettingsConfigurationSection = getConfig().getConfigurationSection("sqlSettings");
    private final MySQL BASE = new MySQL(
            sqLSettingsConfigurationSection.getString("user"),
            sqLSettingsConfigurationSection.getString("password"),
            sqLSettingsConfigurationSection.getString("host"),
            sqLSettingsConfigurationSection.getString("database"),
            sqLSettingsConfigurationSection.getInt("port")
    );

    private boolean STATION = true;
    {
        new NarutoRunner();
        new ElonMusk();
    }

    @Override
    public void onEnable() {

        registerConfig();

        // Подключение к базе данных
        try {
            getLogger().info("[!] Connecting to DataBase.");
            STATEMENT = BASE.openConnection().createStatement();
            STATEMENT.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS `RaidPlayers` (" +
                            "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                            "uuid TEXT, " +
                            "money INT, " +
                            "characters TEXT, " +
                            "clef INT, " +
                            "level INT, " +
                            "kills INT, " +
                            "wins INT" +
                    ");"
            );
            getLogger().info("[!] Connected to DataBase.");
        } catch (ClassNotFoundException | SQLException e) {
            getLogger().info("[!] Connection exception.");
        }
        Bukkit.getPluginManager().registerEvents(new UsualListener(), this);
        Bukkit.getPluginManager().registerEvents(new SneakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(connectionListener, this);
        for (Player player : Bukkit.getOnlinePlayers())
            connectionListener.loadStats(player);

        raidSpawn = getLocationByPath("raidLocation");
        toggleLocation = getLocationByPath("toggleLocation");
        defSpawn = getLocationByPath("defLocation");

        int minPlayers = SETTINGS.getInt("minPlayers");

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(TIME + "");
                switch (TIME_STATUS) {
                    case WAITING:
                        if (Bukkit.getOnlinePlayers().size() < minPlayers)
                            return;
                        TIME++;
                        if (TIME == RaidTimeStatus.WAITING.getTime()) {
                            Bukkit.broadcastMessage("Игра начинается!");
                            TIME_STATUS = RaidTimeStatus.STARTING;
                        }
                        break;
                    case STARTING:
                        TIME++;
                        int dt = RaidTimeStatus.STARTING.getTime() - TIME;
                        if (dt < 5)
                            Bukkit.broadcastMessage("До начала игры осталось " + ++dt + " секунд(ы).");
                        if (TIME == RaidTimeStatus.STARTING.getTime()) {
                            TIME_STATUS = RaidTimeStatus.GAME;
                            Bukkit.broadcastMessage("Игра началась!");
                        }
                        if (TIME == RaidTimeStatus.STARTING.getTime())
                            gameStarter();

                        break;
                    case GAME:
                        TIME++;
                        if (TIME == RaidTimeStatus.GAME.getTime()) {
                            TIME_STATUS = RaidTimeStatus.ENDING;
                            Bukkit.broadcastMessage("Игра закончилась! Защитники Зоны 51 победили и отстояли в этом рейде!");
                        } else if (attackersWin) {
                            TIME_STATUS = RaidTimeStatus.ENDING;
                            TIME = RaidTimeStatus.GAME.getTime();
                            Bukkit.broadcastMessage("Игра закончилась! Атакующие победили и защитили пришельца!");
                        }
                        break;
                    case ENDING:
                        TIME++;
                        int dx = RaidTimeStatus.ENDING.getTime() - TIME;
                        if (dx < 5)
                            Bukkit.broadcastMessage("Игра перезапустится через " + ++dx + " секунд");
                        if (dx == 0)
                            Bukkit.reload();
                        break;
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    private void registerConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public RaidTimeStatus getTimeStatus() {
        return TIME_STATUS;
    }

    public int getTime() {
        return TIME;
    }

    public Statement getStatement() {
        return STATEMENT;
    }

    public Map<UUID, RaidPlayer> getPlayers() {
        return players;
    }

    public boolean isStation() {
        return STATION;
    }

    public void setStation(final boolean STATION) {
        this.STATION = STATION;
    }

    public Location getToggleLocation() {
        return toggleLocation;
    }

    public Location getRaidSpawn() {
        return raidSpawn;
    }

    public RaidStatus getStatus() {
        return STATUS;
    }

    public void setStatus(final RaidStatus STATUS) {
        this.STATUS = STATUS;
    }

    public void setAttackersWin(boolean attackersWin) {
        this.attackersWin = attackersWin;
    }

    private Location getLocationByPath(final String path) {
        String[] coord = SETTINGS.getString(path).split("\\s+");
        Location location = new Location(
                Bukkit.getWorld(SETTINGS.getString("world")),
                Double.parseDouble(coord[0]),
                Double.parseDouble(coord[1]),
                Double.parseDouble(coord[2])
        );
        return location;
    }

    private void gameStarter() {
        int defenders = SETTINGS.getInt("defenders");

        for (Player player : Bukkit.getOnlinePlayers()) {

            Enderman enderman = (Enderman) player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDERMAN);
            enderman.setAI(false);
            enderman.setCustomName("§lЗема");
            enderman.setPortalCooldown(99999999);
            enderman.setCustomNameVisible(true);
            enderman.setGravity(true);

            defenders--;
            RaidPlayer raidPlayer = players.get(player.getUniqueId());
            player.getInventory().clear();
            Location location;
            if (defenders <= 0) {
                location = defSpawn.subtract(random.nextInt(5), 0, random.nextInt(5));
                raidPlayer.setCurrentCharacter(soldier);
                raidPlayer.setDefend(true);
            } else
                location = raidSpawn.subtract(random.nextInt(5), 0, random.nextInt(5));

            player.teleport(location);
            player.performCommand("spawnpoint");

            player.sendMessage("Выбранный класс: " + raidPlayer.getCurrentCharacter().getName());
            raidPlayer.getCurrentCharacter().giveAmmunition(player);
        }
    }
}

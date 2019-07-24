package ru.func.raidarea;

import com.google.common.collect.Maps;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.func.raidarea.character.*;
import ru.func.raidarea.database.MySQL;
import ru.func.raidarea.listener.*;
import ru.func.raidarea.player.IPlayer;
import ru.func.raidarea.player.RaidPlayer;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RaidArea extends JavaPlugin {

    private Random              random = new Random();

    private int                   TIME = 0;
    private RaidTimeStatus TIME_STATUS = RaidTimeStatus.WAITING;
    private RaidStatus          STATUS = RaidStatus.ACTIVE_STATION;

    private ConnectionListener connectionListener = new ConnectionListener(this);
    private final ConfigurationSection SETTINGS = getConfig().getConfigurationSection("settings");

    private Location       defSpawn;
    private Location      raidSpawn;
    private Location toggleLocation;

    private final ICharacter soldier = new Soldier();
    private boolean     attackersWin = false;

    private Map<UUID, IPlayer> players = Maps.newHashMap();

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

    private ICharacter[] characters = {
            new ArnoldSchwarzenegger(),
            new ElonMusk(),
            new KeanuReeves(),
            new NarutoRunner()
    };

    private boolean STATION = true;

    private ItemStack     TNT;
    private ItemStack    HEAL;
    private ItemStack   SPEED;
    private ItemStack BARRIER;

    @Override
    public void onEnable() {

        World world = Bukkit.getWorld(SETTINGS.getString("world"));
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setDifficulty(Difficulty.HARD);
        world.setMonsterSpawnLimit(0);
        world.setAnimalSpawnLimit(0);
        world.setAutoSave(false);
        world.setTime(12000);

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
                            "kills INT, " +
                            "wins INT" +
                    ");"
            );
            getLogger().info("[!] Connected to DataBase.");
        } catch (ClassNotFoundException | SQLException e) {
            getLogger().info("[!] Connection exception.");
        }
        Bukkit.getPluginManager().registerEvents(new UsualListener(), this);
        Bukkit.getPluginManager().registerEvents(new RespawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SneakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(connectionListener, this);

        for (Player player : Bukkit.getOnlinePlayers())
            connectionListener.loadStats(player);

        /*      ITEMS *************** */
        HEAL = new Potion(PotionType.INSTANT_HEAL, 1, true).toItemStack(1);
        ItemMeta meta = HEAL.getItemMeta();
        meta.setDisplayName("§f§l[ §cВосстановления здоровья §f§l] | 100 §e§lETH");
        HEAL.setItemMeta(meta);

        SPEED = new Potion(PotionType.SPEED, 1, true).toItemStack(1);
        meta = SPEED.getItemMeta();
        meta.setDisplayName("§f§l[ §bУскорение тела §f§l] | 500 §e§lETH");
        SPEED.setItemMeta(meta);

        BARRIER = new ItemStack(Material.FENCE);
        meta = BARRIER.getItemMeta();
        meta.setDisplayName("§f§l[ §7Преграда §f§l] | 50 §e§lETH");
        BARRIER.setItemMeta(meta);

        TNT = new ItemStack(Material.TNT);
        meta = TNT.getItemMeta();
        meta.setDisplayName("§f§l[ §7Взрывчатка §f§l] | 500 §e§lETH");
        TNT.setItemMeta(meta);
        /*      END ******************* */

        raidSpawn = getLocationByPath("raidLocation");
        toggleLocation = getLocationByPath("toggleLocation");
        defSpawn = getLocationByPath("defLocation");

        int minPlayers = SETTINGS.getInt("minPlayers");

        new BukkitRunnable() {
            @Override
            public void run() {
                switch (TIME_STATUS) {
                    case WAITING:
                        if (Bukkit.getOnlinePlayers().size() < minPlayers)
                            return;
                        TIME++;
                        if (TIME == RaidTimeStatus.WAITING.getTime()) {
                            Bukkit.broadcastMessage("Игра начанается!");
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
                            Bukkit.getOnlinePlayers().forEach(player -> connectionListener.saveStats(player, 0));
                            TIME_STATUS = RaidTimeStatus.ENDING;
                            Bukkit.broadcastMessage("Игра закончилась! Защитники Зоны 51 победили и отстояли в этом рейде! (Все данный сохранены)");
                        } else if (attackersWin) {
                            TIME_STATUS = RaidTimeStatus.ENDING;
                            TIME = RaidTimeStatus.GAME.getTime();
                            Bukkit.broadcastMessage("Игра закончилась! Атакующие победили и защитили пришельца! (Все данный сохранены)");
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

    @Override
    public void onDisable() {
        Bukkit.getWorld(SETTINGS.getString("world"))
                .getEntities()
                .stream()
                .filter(entity -> ! (entity instanceof Player))
                .forEach(Entity::remove);
    }

    private void registerConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public RaidTimeStatus getTimeStatus() {
        return TIME_STATUS;
    }

    public Statement getStatement() {
        return STATEMENT;
    }

    public Map<UUID, IPlayer> getPlayers() {
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

    public Location getDefSpawn() {
        return defSpawn;
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

        Enderman enderman = (Enderman) Bukkit.getWorld(SETTINGS.getString("world")).spawnEntity(getLocationByPath("endermanLocation"), EntityType.ENDERMAN);
        enderman.setAI(false);
        enderman.setCustomName("§lПришелец Артемилиан");
        enderman.setCustomNameVisible(true);
        enderman.setGravity(true);

        for (Player player : Bukkit.getOnlinePlayers()) {
            RaidPlayer raidPlayer = (RaidPlayer) players.get(player.getUniqueId());
            player.getInventory().clear();

            Location location;
            if (characters.length > 0) {
                raidPlayer.setCurrentCharacter(characters[characters.length - 1]);
                characters = Arrays.copyOf(characters, characters.length - 1);

                location = raidSpawn.subtract(random.nextInt(5), 0, random.nextInt(5));
                player.sendMessage("Ваш класс: " + raidPlayer.getCurrentCharacter().getName() + ", отключите питание на базе, и вызволите испытуемого пришельца, вся надежда на вас!");
            } else {
                location = defSpawn.subtract(random.nextInt(5), 0, random.nextInt(5));
                raidPlayer.setCurrentCharacter(soldier);
                raidPlayer.setDefend(true);
                player.sendMessage("Вы - защита этой легендарной Зоны 51, любой ценой защитите базу!");
            }
            player.teleport(location);

            raidPlayer.getCurrentCharacter().giveAmmunition(player);
            giveItems(player);
        }
    }

    public void giveItems(Player player) {
        Inventory inventory = player.getInventory();
        inventory.setItem(4, HEAL);
        inventory.setItem(5, SPEED);
        inventory.setItem(6, players.get(player.getUniqueId()).isDefend() ? BARRIER : TNT);
    }
}

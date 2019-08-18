package ru.func.raidarea;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import ru.func.raidarea.character.attack.ArnoldSchwarzenegger;
import ru.func.raidarea.character.attack.Attacker;
import ru.func.raidarea.character.attack.KeanuReeves;
import ru.func.raidarea.character.defend.Defender;
import ru.func.raidarea.character.defend.Soldier;
import ru.func.raidarea.database.MySQL;
import ru.func.raidarea.listener.*;
import ru.func.raidarea.player.Shuffler;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Getter
public class RaidArea extends JavaPlugin {

    private Random random = new Random();

    private RaidClock raidClock = new RaidClock(this);
    private ConnectionListener connectionListener = new ConnectionListener(this, raidClock);
    private final ConfigurationSection settings = getConfig().getConfigurationSection("settings");

    private Location defSpawn;
    private Location raidSpawn;
    private Location toggleLocation;

    @Setter
    private boolean attackersWin = false;

    private Map<UUID, Shuffler> players = Maps.newHashMap();
    private int minPlayers = settings.getInt("minPlayers");
    @Setter
    private int endermanAmount = 0;

    private Statement statement;

    @Setter
    private Attacker[] attackCharacters = {
            new KeanuReeves(),
            new ArnoldSchwarzenegger()
    };
    @Setter
    private Defender[] defendCharacters = {
            new Soldier()
    };

    @Setter
    private boolean station = true;

    private ItemStack heal = new Potion(PotionType.INSTANT_HEAL, 1, true).toItemStack(1);
    private ItemStack arrow = new ItemStack(Material.ARROW);
    private ItemStack speed = new Potion(PotionType.SPEED, 1, true).toItemStack(1);
    private ItemStack barrier = new ItemStack(Material.FENCE);

    @Override
    public void onEnable() {

        World world = Bukkit.getWorld(settings.getString("world"));
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setDifficulty(Difficulty.HARD);
        world.setMonsterSpawnLimit(0);
        world.setAnimalSpawnLimit(0);
        world.setAutoSave(false);
        world.setTime(7000);

        registerConfig();

        // Подключение к базе данных
        try {
            getLogger().info("[!] Connecting to DataBase.");
            ConfigurationSection sqlSettingsConfigurationSection = getConfig().getConfigurationSection("sqlSettings");
            statement = new MySQL(
                    sqlSettingsConfigurationSection.getString("user"),
                    sqlSettingsConfigurationSection.getString("password"),
                    sqlSettingsConfigurationSection.getString("host"),
                    sqlSettingsConfigurationSection.getString("database"),
                    sqlSettingsConfigurationSection.getInt("port")
            ).openConnection().createStatement();

            statement.executeUpdate(
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

        Bukkit.getPluginManager().registerEvents(new BlockEventListener(this), this);
        Bukkit.getPluginManager().registerEvents(new UsualListener(), this);
        Bukkit.getPluginManager().registerEvents(new RespawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SneakListener(this, raidClock), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(this, raidClock), this);
        Bukkit.getPluginManager().registerEvents(new MoveListener(this, raidClock), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(this, raidClock), this);
        Bukkit.getPluginManager().registerEvents(connectionListener, this);

        Bukkit.getOnlinePlayers().forEach(connectionListener::loadStats);

        initExtraItem(heal, "§f§l[ §cВосстановления здоровья §f§l] | 100 §e§lETH");
        initExtraItem(speed, "§f§l[ §bУскорение тела §f§l] | 300 §e§lETH");
        initExtraItem(barrier, "§f§l[ §7Преграда §f§l] | 75 §e§lETH");
        initExtraItem(arrow, "§f§l[ §7Взрывная стрела §f§l] | 150 §e§lETH");

        raidSpawn = raidClock.getLocationByPath("raidLocation");
        toggleLocation = raidClock.getLocationByPath("toggleLocation");
        defSpawn = raidClock.getLocationByPath("defLocation");

        raidClock.runGameClock();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            connectionListener.saveStats(player, 0);
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
        });
        Bukkit.getWorld(settings.getString("world"))
                .getEntities()
                .stream()
                .filter(entity -> !(entity instanceof Player))
                .forEach(Entity::remove);
    }

    private void registerConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void initExtraItem(final ItemStack itemStack, final String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
    }

    public void giveItems(final Player player) {
        Inventory inventory = player.getInventory();
        inventory.setItem(4, heal);
        inventory.setItem(5, speed);
        inventory.setItem(6, players.get(player.getUniqueId()).isDefend() ? barrier : arrow);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
    }
}

package ru.func.raidarea;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
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

    private Random random = new Random();

    @Getter
    @Setter
    private int time = 0;
    @Getter
    @Setter
    private RaidTimeStatus timeStatus = RaidTimeStatus.WAITING;
    @Getter
    @Setter
    private RaidStatus gameStatus = RaidStatus.ACTIVE_STATION;

    private ConnectionListener connectionListener = new ConnectionListener(this);
    @Getter
    private final ConfigurationSection settings = getConfig().getConfigurationSection("settings");

    @Getter
    private Location defSpawn;
    @Getter
    private Location raidSpawn;
    @Getter
    private Location toggleLocation;

    private final ICharacter soldier = new Soldier();
    @Getter
    @Setter
    private boolean attackersWin = false;

    @Getter
    private Map<UUID, IPlayer> players = Maps.newHashMap();
    @Getter
    private int minPlayers = settings.getInt("minPlayers");
    @Setter
    @Getter
    private int endermanAmount = 0;

    /* SQL переменные */
    @Getter
    private Statement statement;
    private final ConfigurationSection sqlSettingsConfigurationSection = getConfig().getConfigurationSection("sqlSettings");
    private final MySQL BASE = new MySQL(
            sqlSettingsConfigurationSection.getString("user"),
            sqlSettingsConfigurationSection.getString("password"),
            sqlSettingsConfigurationSection.getString("host"),
            sqlSettingsConfigurationSection.getString("database"),
            sqlSettingsConfigurationSection.getInt("port")
    );

    private ICharacter[] characters = {
            new KeanuReeves(),
            new ArnoldSchwarzenegger()
    };

    @Getter
    @Setter
    private boolean station = true;

    private ItemStack HEAL;
    private ItemStack ARROW;
    private ItemStack SPEED;
    private ItemStack BARRIER;

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
            statement = BASE.openConnection().createStatement();
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
        Bukkit.getPluginManager().registerEvents(new SneakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(connectionListener, this);

        Bukkit.getOnlinePlayers().forEach(connectionListener::loadStats);

        /*      ITEMS *************** */
        HEAL = new Potion(PotionType.INSTANT_HEAL, 1, true).toItemStack(1);
        ItemMeta meta = HEAL.getItemMeta();
        meta.setDisplayName("§f§l[ §cВосстановления здоровья §f§l] | 100 §e§lETH");
        HEAL.setItemMeta(meta);

        SPEED = new Potion(PotionType.SPEED, 1, true).toItemStack(1);
        meta = SPEED.getItemMeta();
        meta.setDisplayName("§f§l[ §bУскорение тела §f§l] | 300 §e§lETH");
        SPEED.setItemMeta(meta);

        BARRIER = new ItemStack(Material.FENCE);
        meta = BARRIER.getItemMeta();
        meta.setDisplayName("§f§l[ §7Преграда §f§l] | 75 §e§lETH");
        BARRIER.setItemMeta(meta);

        ARROW = new ItemStack(Material.ARROW);
        meta = ARROW.getItemMeta();
        meta.setDisplayName("§f§l[ §7Взрывная стрела §f§l] | 150 §e§lETH");
        ARROW.setItemMeta(meta);
        /*      END ******************* */

        raidSpawn = getLocationByPath("raidLocation");
        toggleLocation = getLocationByPath("toggleLocation");
        defSpawn = getLocationByPath("defLocation");

        new BukkitRunnable() {
            @Override
            public void run() {
                switch (timeStatus) {
                    case WAITING:
                        if (Bukkit.getOnlinePlayers().size() < minPlayers)
                            return;
                        time++;
                        if (time == RaidTimeStatus.WAITING.getTime()) {
                            Bukkit.broadcastMessage("[§b!§f] §7Игра начинается!");
                            timeStatus = RaidTimeStatus.STARTING;
                        }
                        break;
                    case STARTING:
                        time++;
                        int dt = RaidTimeStatus.STARTING.getTime() - time;
                        if (dt <= 5 && dt != 0)
                            Bukkit.broadcastMessage("[§b!§f] §7До начала игры осталось " + dt + " секунд(ы).");
                        if (time == RaidTimeStatus.STARTING.getTime()) {
                            timeStatus = RaidTimeStatus.GAME;
                            Bukkit.broadcastMessage("[§b!§f] §7Игра началась!");
                        }
                        if (time == RaidTimeStatus.STARTING.getTime())
                            gameStarter();

                        break;
                    case GAME:
                        time++;
                        if (time == RaidTimeStatus.GAME.getTime()) {
                            timeStatus = RaidTimeStatus.ENDING;
                            Bukkit.broadcastMessage("[§b!§f] §7Игра закончилась! Защитники Зоны 51 победили и отстояли в этом рейде!");
                        } else if (attackersWin) {
                            timeStatus = RaidTimeStatus.ENDING;
                            time = RaidTimeStatus.GAME.getTime();
                            Bukkit.broadcastMessage("[§b!§f] §7Игра закончилась! Атакующие победили и защитили пришельца!");
                        }
                        break;
                    case ENDING:
                        time++;
                        int dx = RaidTimeStatus.ENDING.getTime() - time;
                        if (dx == 5) {
                            Bukkit.getOnlinePlayers()
                                    .stream()
                                    .map(Player::getUniqueId)
                                    .map(players::get)
                                    .filter(player -> !player.isDefend() == attackersWin)
                                    .forEach(player -> player.setWins(player.getWins() + 1));

                            Bukkit.getOnlinePlayers().forEach(player -> connectionListener.saveStats(player, 0));
                            Bukkit.broadcastMessage("[§b!§f] §7Статистика сохранена.");
                        } else if (dx < 5)
                            Bukkit.broadcastMessage("[§b!§f] §7Игра перезапустится через " + ++dx + " секунд");
                        if (dx == 0)
                            Bukkit.reload();
                        break;
                }
            }
        }.runTaskTimer(this, 20L, 20L);
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

    private Location getLocationByPath(final String path) {
        String[] coordinates = settings.getString(path).split("\\s+");
        return new Location(
                Bukkit.getWorld(settings.getString("world")),
                Double.parseDouble(coordinates[0]),
                Double.parseDouble(coordinates[1]),
                Double.parseDouble(coordinates[2])
        );
    }

    private void gameStarter() {

        spawnEnderman();

        for (Player player : Bukkit.getOnlinePlayers()) {
            RaidPlayer raidPlayer = (RaidPlayer) players.get(player.getUniqueId());
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.getActivePotionEffects()
                    .stream()
                    .map(PotionEffect::getType)
                    .forEach(player::removePotionEffect);

            Location location;
            player.sendMessage("[§b!§f] #§lИНФОРМАЦИЯ.");
            if (characters.length > 0) {
                raidPlayer.setCurrentCharacter(characters[characters.length - 1]);
                characters = Arrays.copyOf(characters, characters.length - 1);

                location = raidSpawn.subtract(random.nextInt(5), 0, random.nextInt(5));
                player.sendMessage("[§b!§f] §7Ваш персонаж: §f§l" + raidPlayer.getCurrentCharacter().getName() +
                        "§7, первым делом §f§lотключете питание базы §7нажав на рычаг, который защищают солдаты," +
                        " следом §f§lунесите§7 с собой §f§lпришельца, §7вся надежда на вас!"
                );
                player.sendMessage("[§b!§f] §7Вы можете купить взрывную стрелу, которая ломает преграды.");
                player.sendMessage("[§b!§f] §lУникальная способность - [§eSHIFT§f§l] [§cPVP §f§l1.8]");
            } else {
                location = defSpawn.subtract(random.nextInt(5), 0, random.nextInt(5));
                raidPlayer.setCurrentCharacter(soldier);
                raidPlayer.setDefend(true);
                player.sendMessage("[§b!§f] §7Вы - защита легендарной Зоны 51, §f§lнападение продлится 10 минут, закрывайте §7пришельца и выключатель §f§lпреградами, §7любой ценой сохраните секреты базы!");
                player.sendMessage("[§b!§f] §7Вы можете купить дополнительные преграды.");
                player.sendMessage("[§b!§f] §lПоставить преграду - [§eSHIFT§f§l] [§cPVP §f§l1.8]");
            }
            player.teleport(location);

            raidPlayer.getCurrentCharacter().giveAmmunition(player);
            giveItems(player);
        }
    }

    public void giveItems(final Player player) {
        Inventory inventory = player.getInventory();
        inventory.setItem(4, HEAL);
        inventory.setItem(5, SPEED);
        inventory.setItem(6, players.get(player.getUniqueId()).isDefend() ? BARRIER : ARROW);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
    }

    private void spawnEnderman() {

        /* Создаю N эндерманов, которых необходимо принести на базу что бы победить */
        getConfig().getConfigurationSection("settings.enderman_locations").getKeys(false).forEach(key -> {
            Enderman enderman = (Enderman) Bukkit.getWorld(settings.getString("world"))
                    .spawnEntity(getLocationByPath("enderman_locations." + key), EntityType.ENDERMAN);
            enderman.setAI(false);
            enderman.setCustomName("§lПришелец");
            enderman.setMaxHealth(1024);
            enderman.setHealth(1024);
            enderman.setGlowing(true);
            enderman.setCustomNameVisible(true);
            enderman.setGravity(true);
            endermanAmount++;
        });
    }
}

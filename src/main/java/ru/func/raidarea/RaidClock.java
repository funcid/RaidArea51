package ru.func.raidarea;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.func.raidarea.character.ICharacter;
import ru.func.raidarea.character.Soldier;
import ru.func.raidarea.player.IPlayer;
import ru.func.raidarea.player.RaidPlayer;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
public class RaidClock {

    @Getter
    @Setter
    private int time = 0;
    @Getter
    @Setter
    private RaidTimeStatus timeStatus = RaidTimeStatus.WAITING;
    @Getter
    @Setter
    private RaidStatus gameStatus = RaidStatus.ACTIVE_STATION;

    private final ICharacter soldier = new Soldier();

    private final RaidArea plugin;
    private final Map<UUID, IPlayer> players;

    RaidClock(final RaidArea plugin) {
        this.plugin = plugin;
        players = plugin.getPlayers();
    }

    void runGameClock() {
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (timeStatus) {
                    case WAITING:
                        if (Bukkit.getOnlinePlayers().size() < plugin.getMinPlayers())
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
                        } else if (plugin.isAttackersWin()) {
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
                                    .filter(player -> !player.isDefend() == plugin.isAttackersWin())
                                    .forEach(player -> player.setWins(player.getWins() + 1));

                            Bukkit.getOnlinePlayers().forEach(player -> plugin.getConnectionListener().saveStats(player, 0));
                            Bukkit.broadcastMessage("[§b!§f] §7Статистика сохранена.");
                        } else if (dx < 5)
                            Bukkit.broadcastMessage("[§b!§f] §7Игра перезапустится через " + ++dx + " секунд");
                        if (dx == 0)
                            Bukkit.reload();
                        break;
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void gameStarter() {

        spawnEnderman();

        Bukkit.getOnlinePlayers().forEach(player -> {
            RaidPlayer raidPlayer = (RaidPlayer) players.get(player.getUniqueId());
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.getActivePotionEffects().clear();

            Location location;
            player.sendMessage("[§b!§f] #§lИНФОРМАЦИЯ.");
            Random random = plugin.getRandom();
            ICharacter[] characters = plugin.getCharacters();
            if (characters.length > 0) {
                raidPlayer.setCurrentCharacter(characters[characters.length - 1]);
                plugin.setCharacters(Arrays.copyOf(characters, characters.length - 1));

                location = plugin.getRaidSpawn().subtract(random.nextInt(5), 0, random.nextInt(5));
                player.sendMessage("[§b!§f] §7Ваш персонаж: §f§l" + raidPlayer.getCurrentCharacter().getName() +
                        "§7, первым делом §f§lотключете питание базы §7нажав на рычаг, который защищают солдаты," +
                        " следом §f§lунесите§7 с собой §f§lпришельца, §7вся надежда на вас!"
                );
                player.sendMessage("[§b!§f] §7Вы можете купить взрывную стрелу, которая ломает преграды.");
                player.sendMessage("[§b!§f] §lУникальная способность - [§eSHIFT§f§l] [§cPVP §f§l1.8]");
            } else {
                location = plugin.getDefSpawn().subtract(random.nextInt(5), 0, random.nextInt(5));
                raidPlayer.setCurrentCharacter(soldier);
                raidPlayer.setDefend(true);
                player.sendMessage("[§b!§f] §7Вы - защита легендарной Зоны 51, §f§lнападение продлится 10 минут, закрывайте §7пришельца и выключатель §f§lпреградами, §7любой ценой сохраните секреты базы!");
                player.sendMessage("[§b!§f] §7Вы можете купить дополнительные преграды.");
                player.sendMessage("[§b!§f] §lПоставить преграду - [§eSHIFT§f§l] [§cPVP §f§l1.8]");
            }
            player.teleport(location);

            raidPlayer.getCurrentCharacter().giveAmmunition(player);
            plugin.giveItems(player);
        });
    }

    private void spawnEnderman() {

        /* Создаю N эндерманов, которых необходимо принести на базу что бы победить */
        plugin.getConfig().getConfigurationSection("settings.enderman_locations").getKeys(false).forEach(key -> {
            Enderman enderman = (Enderman) Bukkit.getWorld(plugin.getSettings().getString("world"))
                    .spawnEntity(getLocationByPath("enderman_locations." + key), EntityType.ENDERMAN);
            enderman.setAI(false);
            enderman.setCustomName("§lПришелец");
            enderman.setMaxHealth(1024);
            enderman.setHealth(1024);
            enderman.setGlowing(true);
            enderman.setCustomNameVisible(true);
            enderman.setGravity(true);
            plugin.setEndermanAmount(plugin.getEndermanAmount() + 1);
        });
    }

    Location getLocationByPath(final String path) {
        String[] coordinates = plugin.getSettings().getString(path).split("\\s+");
        return new Location(
                Bukkit.getWorld(plugin.getSettings().getString("world")),
                Double.parseDouble(coordinates[0]),
                Double.parseDouble(coordinates[1]),
                Double.parseDouble(coordinates[2])
        );
    }
}

package ru.func.raidarea.listener;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.RaidClock;
import ru.func.raidarea.RaidStatus;
import ru.func.raidarea.RaidTimeStatus;
import ru.func.raidarea.player.IPlayer;

public class InteractListener implements Listener {

    private final RaidArea plugin;
    private final PotionEffect slowness;
    private final PotionEffect blindness;
    private final RaidClock raidClock;

    public InteractListener(final RaidArea plugin, final RaidClock raidClock) {
        this.plugin = plugin;
        this.raidClock = raidClock;

        slowness = new PotionEffect(PotionEffectType.SLOW, 10000, 4);
        blindness = new PotionEffect(PotionEffectType.BLINDNESS, 10000, 1);
    }

    @EventHandler
    public void onInteractEvent(final PlayerInteractEvent e) {
        e.setCancelled(!raidClock.getTimeStatus().equals(RaidTimeStatus.GAME));

        if (raidClock.getTimeStatus().equals(RaidTimeStatus.GAME)) {
            Player player = e.getPlayer();
            IPlayer raidPlayer = plugin.getPlayers().get(player.getUniqueId());

            // OFF LEVER
            if (e.getClickedBlock() != null && !raidPlayer.isDefend() && plugin.isStation() && player.getGameMode().equals(GameMode.SURVIVAL)) {
                if (e.getClickedBlock().getType().equals(Material.LEVER) && e.getClickedBlock().getLocation().equals(plugin.getToggleLocation())) {
                    plugin.setStation(false);
                    raidPlayer.depositMoney(200);
                    player.sendMessage("§l+ 200 ETH §eЗа выключение питания всей зоны 51.");
                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("[§b!§f] Станция была обесточена! Теперь вы можете помочь инопланетянам сбежать!");
                    Bukkit.broadcastMessage("");
                    raidClock.setGameStatus(RaidStatus.DEACTIVATED_STATION);
                }
            }

            // STRIKE
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                raidPlayer.getCurrentCharacter().getGunWeapon().strike(player);

            // EXTRA ITEMS
            boolean cancel = true;
            if (e.getItem() != null && e.getItem().getItemMeta() != null && e.getItem().getItemMeta().hasDisplayName()) {
                switch (e.getItem().getItemMeta().getDisplayName()) {
                    case "§f§l[ §cВосстановления здоровья §f§l] | 100 §e§lETH":
                        if (raidPlayer.getMoney() >= 100) {
                            raidPlayer.depositMoney(-100);
                            cancel = false;
                        }
                        break;
                    case "§f§l[ §bУскорение тела §f§l] | 300 §e§lETH":
                        if (raidPlayer.getMoney() >= 300) {
                            raidPlayer.depositMoney(-300);
                            cancel = false;
                        }
                        break;
                    case "§f§l[ §7Взрывная стрела §f§l] | 150 §e§lETH":
                        if (raidPlayer.getMoney() >= 150) {
                            player.getWorld().spawnArrow(player.getEyeLocation().subtract(0, -3, 0), player.getEyeLocation().getDirection(), 1, 0);
                            raidPlayer.depositMoney(-150);
                        }
                        break;
                    default:
                        cancel = false;
                        break;
                }
                e.setCancelled(cancel);
                if (!cancel)
                    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> plugin.giveItems(player), 1);
            }
        }
    }

    @EventHandler
    public void onInteractEventOnEntity(final PlayerInteractAtEntityEvent e) {
        if (!plugin.isStation() && e.getHand().equals(EquipmentSlot.HAND) && e.getRightClicked() instanceof Enderman) {
            Player player = e.getPlayer();
            if (player.getGameMode().equals(GameMode.SURVIVAL) && player.getPassengers().size() == 0) {
                Bukkit.broadcastMessage(String.format("[§b! %s §a§lперехватил §fпришельца!", plugin.getPlayers().get(player.getUniqueId()).getCurrentCharacter().getName() + "§f] §f§l" + player.getName()));

                player.addPassenger(e.getRightClicked());
                player.addPotionEffect(slowness);
                raidClock.setGameStatus(RaidStatus.SEARCH);
                if (plugin.getPlayers().get(player.getUniqueId()).isDefend())
                    player.addPotionEffect(blindness);
            }
        }
    }

    @EventHandler
    public void onHook(final PlayerFishEvent e) {
        if (e.getHook().isOnGround())
            e.getPlayer().setVelocity(e.getHook().getLocation().toVector().subtract(e.getPlayer().getLocation().toVector()).setY(-0.5));
    }
}

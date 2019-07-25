package ru.func.raidarea.listener;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.RaidStatus;
import ru.func.raidarea.RaidTimeStatus;
import ru.func.raidarea.player.RaidPlayer;

public class InteractListener implements Listener {

    private final RaidArea PLUGIN;
    private final PotionEffect SLOWNESS;
    private final PotionEffect BLINDNESS;

    public InteractListener(final RaidArea plugin) {
        PLUGIN = plugin;
        SLOWNESS = new PotionEffect(PotionEffectType.SLOW, 10000, 4);
        BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 10000, 1);
    }

    @EventHandler
    public void onInteractEvent(final PlayerInteractEvent e) {
        if (!PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME))
            return;

        Player player = e.getPlayer();

        if (PLUGIN.isStation()) {
            if (e.getClickedBlock() != null) {
                if (e.getClickedBlock().getType().equals(Material.LEVER)) {
                    if (e.getClickedBlock().getLocation().equals(PLUGIN.getToggleLocation())) {
                        PLUGIN.setStation(false);
                        PLUGIN.getPlayers().get(player.getUniqueId()).depositMoney(200);
                        player.sendMessage("§l+ 200 ETH §eЗа выключение питание всей зоны 51.");
                        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage("§l[§b!§f] Станция была обесточена! Теперь вы можете помочь инопланетянам сбежать!");
                        Bukkit.broadcastMessage("");
                        PLUGIN.setStatus(RaidStatus.DIACTIVATED_STATION);
                    }
                }
            }
        }

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            PLUGIN.getPlayers().get(e.getPlayer().getUniqueId()).getCurrentCharacter().getGunWeapon().strike(player);

        RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(player.getUniqueId());
        if (e.getItem() != null) {
            if (e.getMaterial().equals(Material.SPLASH_POTION)) {
                switch (e.getItem().getItemMeta().getDisplayName()) {
                    case "§f§l[ §cВосстановления здоровья §f§l] | 100 §e§lETH":
                        if (raidPlayer.getMoney() >= 100) raidPlayer.depositMoney(-100);
                        else e.setCancelled(true);
                        break;
                    case "§f§l[ §bУскорение тела §f§l] | 500 §e§lETH":
                        if (raidPlayer.getMoney() >= 500) raidPlayer.depositMoney(-500);
                        else e.setCancelled(true);
                }
            }
            if (e.getItem().getItemMeta().getDisplayName().equals("§f§l[ §7Взрывная стрела §f§l] | 200 §e§lETH")) {
                if (raidPlayer.getMoney() >= 200) {
                    player.getWorld().spawnArrow(player.getEyeLocation().subtract(0, -3, 0), player.getEyeLocation().getDirection(), 1, 0);
                    raidPlayer.depositMoney(-200);
                }
            }
            PLUGIN.giveItems(player);
        }

    }

    @EventHandler
    public void onInteractEventOnEntity(final PlayerInteractAtEntityEvent e) {
        if (!PLUGIN.isStation()) {
            if (e.getHand().equals(EquipmentSlot.HAND)) {
                if (e.getRightClicked() instanceof Enderman) {
                    Player player = e.getPlayer();
                    if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                        if (player.getPassengers().size() == 0)
                            Bukkit.broadcastMessage(String.format("[§b! %s §a§lперехватил §fпришельца!", PLUGIN.getPlayers().get(player.getUniqueId()).getCurrentCharacter().getName() + "§f] §f§l" + player.getName()));

                        player.addPassenger(e.getRightClicked());
                        player.addPotionEffect(SLOWNESS);
                        PLUGIN.setStatus(RaidStatus.SEARCH);
                        if (PLUGIN.getPlayers().get(player.getUniqueId()).isDefend())
                            player.addPotionEffect(BLINDNESS);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        e.setCancelled(true);

        if (e.getBlock().getType().equals(Material.FENCE)) {
            RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(e.getPlayer().getUniqueId());
            if (raidPlayer.getMoney() > 50) {
                raidPlayer.depositMoney(-50);
                PLUGIN.giveItems(e.getPlayer());
                e.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onHook(PlayerFishEvent e) {
        if (e.getHook().isOnGround())
            e.getPlayer().setVelocity(e.getHook().getLocation().toVector().subtract(e.getPlayer().getLocation().toVector()).setY(-1));
    }
}

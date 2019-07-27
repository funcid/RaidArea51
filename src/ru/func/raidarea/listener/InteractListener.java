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
        e.setCancelled(!PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME));

        if (PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME)) {
            Player player = e.getPlayer();
            RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(player.getUniqueId());
            // OFF LEVER
            if (e.getClickedBlock() != null) {
                if (PLUGIN.isStation()) {
                    if (e.getClickedBlock().getType().equals(Material.LEVER)) {
                        if (e.getClickedBlock().getLocation().equals(PLUGIN.getToggleLocation())) {
                            PLUGIN.setStation(false);
                            raidPlayer.depositMoney(200);
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
            // STRIKE
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                raidPlayer.getCurrentCharacter().getGunWeapon().strike(player);

            // EXTRA ITEMS
            boolean cancel = true;
            if (e.getItem() != null) {
                if (e.getItem().getItemMeta() != null) {
                    if (e.getItem().getItemMeta().hasDisplayName()) {
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
                        if (!cancel) Bukkit.getScheduler().runTaskLaterAsynchronously(PLUGIN, () -> PLUGIN.giveItems(player), 1);
                    }
                }
            }
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
    public void onHook(PlayerFishEvent e) {
        if (e.getHook().isOnGround())
            e.getPlayer().setVelocity(e.getHook().getLocation().toVector().subtract(e.getPlayer().getLocation().toVector()).setY(-0.5));
    }
}

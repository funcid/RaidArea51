package ru.func.raidarea.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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

    private final RaidArea        PLUGIN;
    private final PotionEffect  SLOWNESS;
    private final PotionEffect BLINDNESS;

    public InteractListener(final RaidArea plugin) {
        PLUGIN = plugin;
        SLOWNESS = new PotionEffect(PotionEffectType.SLOW, 10000, 4);
        BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 10000, 1);
    }

    @EventHandler
    public void onInteractEvent(final PlayerInteractEvent e) {
        if (PLUGIN.isStation()) {
            if (e.getClickedBlock() != null) {
                if (e.getClickedBlock().getType().equals(Material.LEVER)) {
                    if (e.getClickedBlock().getLocation().equals(PLUGIN.getToggleLocation())) {
                        PLUGIN.setStation(false);
                        Bukkit.broadcastMessage("Станция была обесточена! Теперь вы можете помочь инопланетянам сбежать!");
                        PLUGIN.setStatus(RaidStatus.DIACTIVATED_STATION);
                    }
                }
            }
        }
        if (PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME)) {
            Player player = e.getPlayer();
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                PLUGIN.getPlayers().get(e.getPlayer().getUniqueId()).getCurrentCharacter().getGunWeapon().strike(player);
            if (e.getItem() != null) {
                if (e.getMaterial().equals(Material.SPLASH_POTION)) {
                    RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(player.getUniqueId());
                    switch (e.getItem().getItemMeta().getDisplayName()) {
                        case "§f§l[ §cВосстановления здоровья §f§l] | 100 §e§lETH":
                            if (raidPlayer.getMoney() >= 100) raidPlayer.setMoney(raidPlayer.getMoney() - 100);
                            else {
                                e.setCancelled(true);
                                return;
                            }
                            break;
                        case "§f§l[ §bУскорение тела §f§l] | 500 §e§lETH":
                            if (raidPlayer.getMoney() >= 500) raidPlayer.setMoney(raidPlayer.getMoney() - 500);
                            else {
                                e.setCancelled(true);
                                return;
                            }
                            break;
                        case "§f§l[ §7Невидимость тела §f§l] | 3000 §e§lETH":
                            if (raidPlayer.getMoney() >= 3000) raidPlayer.setMoney(raidPlayer.getMoney() - 3000);
                            else {
                                e.setCancelled(true);
                                return;
                            }
                            break;
                    }
                    Bukkit.getScheduler().runTaskLaterAsynchronously(PLUGIN, () -> PLUGIN.givePotions(player), 1);
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

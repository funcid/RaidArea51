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

public class InteractListener implements Listener {

    private final RaidArea PLUGIN;
    private final PotionEffect SLOWNESS;

    public InteractListener(RaidArea plugin) {
        PLUGIN = plugin;
        SLOWNESS = new PotionEffect(PotionEffectType.SLOW, 10000, 1);
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
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
        if (PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME))
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                PLUGIN.getPlayers().get(e.getPlayer().getUniqueId()).getCurrentCharacter().getGunWeapon().strike(e.getPlayer());
    }
    @EventHandler
    public void onInteractEventOnEntity(PlayerInteractAtEntityEvent e) {
        if (!PLUGIN.isStation()) {
            if (e.getHand().equals(EquipmentSlot.HAND)) {
                if (e.getRightClicked() instanceof Enderman) {
                    Player player = e.getPlayer();
                    if (!PLUGIN.getPlayers().get(player.getUniqueId()).isDefend()) {
                        player.addPassenger(e.getRightClicked());
                        player.addPotionEffect(SLOWNESS);
                        PLUGIN.setStatus(RaidStatus.SEARCH);
                    }
                }
            }
        }
    }
}

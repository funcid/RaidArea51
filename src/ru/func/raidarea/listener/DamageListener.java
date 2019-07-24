package ru.func.raidarea.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionEffectType;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.RaidTimeStatus;
import ru.func.raidarea.player.RaidPlayer;

public class DamageListener implements Listener {

    private final RaidArea PLUGIN;

    public DamageListener(RaidArea plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME)) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntity() instanceof Enderman) {
            e.setCancelled(true);
            return;
        }
        if (e.getDamager() instanceof Snowball && e.getEntity() instanceof Player) {
            Player player = ((Player) ((Snowball) e.getDamager()).getShooter());
            RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(player.getUniqueId());
            if (PLUGIN.getPlayers().get(e.getEntity().getUniqueId()).isDefend() != raidPlayer.isDefend()) {
                e.setDamage(raidPlayer.getCurrentCharacter().getGunWeapon().getDamage());
                raidPlayer.setMoney(raidPlayer.getMoney() + 5);
                player.sendMessage("§l+ 5 ETH §e(За точный выстрел)");
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                if (e.getEntity().getPassengers().size() > 0) {
                    e.getEntity().removePassenger(e.getEntity().getPassengers().get(0));
                    ((Player) e.getEntity()).removePotionEffect(PotionEffectType.SLOW);
                }
            } else e.setCancelled(true);
        }
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        e.getDrops().clear();
        if (e.getEntity().getKiller() != null) {
            Player player = e.getEntity().getKiller();
            RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(player.getUniqueId());
            raidPlayer.setMoney(raidPlayer.getMoney() + 25);
            player.sendMessage("§l+ 25 ETH §e(За невероянтно меткое убийство)");
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
        }
    }
    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent e) {
        if ((e.getEntityType() == EntityType.FALLING_BLOCK)) {
            e.getBlock().getWorld().createExplosion(e.getBlock().getLocation(), 2);
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockExplosion(BlockExplodeEvent e) {
        e.setCancelled(true);
    }
}

package ru.func.raidarea.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionEffectType;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.RaidTimeStatus;
import ru.func.raidarea.player.RaidPlayer;

public class DamageListener implements Listener {

    private final RaidArea PLUGIN;

    public DamageListener(final RaidArea plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent e) {
        if (!PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME)) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntity() instanceof Enderman) {
            e.setCancelled(true);
            return;
        }

        if (e.getEntity() instanceof Player)
            pullDown((Player) e.getEntity());

        if (e.getDamager() instanceof Snowball && e.getEntity() instanceof Player) {
            Player player = ((Player) ((Snowball) e.getDamager()).getShooter());
            RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(player.getUniqueId());
            if (PLUGIN.getPlayers().get(e.getEntity().getUniqueId()).isDefend() != raidPlayer.isDefend()) {
                e.setDamage(raidPlayer.getCurrentCharacter().getGunWeapon().getDamage());
                raidPlayer.setMoney(raidPlayer.getMoney() + 5);
                player.sendMessage("§l+ 5 ETH §e(За точный выстрел)");
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            } else e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSomeDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player)
            pullDown((Player) e.getEntity());
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            e.setCancelled(true);
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent e) {
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
    public void onBlockFall(final EntityChangeBlockEvent e) {
        if ((e.getEntityType().equals(EntityType.FALLING_BLOCK)))
            if (e.getTo().equals(Material.IRON_BLOCK))
                explode(e.getBlock().getLocation());
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemSpawn(final ItemSpawnEvent e) {
        if (e.getEntity().getItemStack().getType().equals(Material.IRON_BLOCK))
            explode(e.getLocation());
        e.setCancelled(true);
    }

    private void pullDown(final Player player) {
        if (player.getPassengers().size() > 0) {
            player.removePassenger(player.getPassengers().get(0));
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }

    private void explode(final Location location) {
        location.getWorld().createExplosion(location, 3);
    }
}

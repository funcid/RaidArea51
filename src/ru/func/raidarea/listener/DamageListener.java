package ru.func.raidarea.listener;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
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
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        if (!PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME)) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntity() instanceof Enderman) {
            e.setCancelled(e.getEntity() instanceof Enderman);
            return;
        }

        if (e.getEntity() instanceof Player) {
            pullDown((Player) e.getEntity());
            if (e.getDamager() instanceof Player) {
                if (((Player) e.getDamager()).getInventory().getItemInMainHand().getItemMeta() == null) {
                    e.setDamage(4);
                    return;
                }
                e.setCancelled(true);
            }
        }
        if (e.getDamager() instanceof Snowball && e.getEntity() instanceof Player) {
            Player player = ((Player) ((Snowball) e.getDamager()).getShooter());
            RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(player.getUniqueId());
            if (PLUGIN.getPlayers().get(e.getEntity().getUniqueId()).isDefend() != raidPlayer.isDefend()) {
                e.setDamage(raidPlayer.getCurrentCharacter().getGunWeapon().getDamage());
                raidPlayer.depositMoney(5);
                player.sendMessage("§l+ 5 ETH §eЗа точный попадание в цель.");
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            } else e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent e) {
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
            raidPlayer.depositMoney(25);
            raidPlayer.setKills(raidPlayer.getKills() + 1);

            player.sendMessage("§l+ 25 ETH §eОтличное убийство!");
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
        }
    }

    private void pullDown(final Player player) {
        if (player.getPassengers().size() > 0) {
            Bukkit.broadcastMessage(String.format("[§b!§f] §l%s§f §c§lпотерял §fпришельца!", player.getName()));

            player.removePassenger(player.getPassengers().get(0));
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }
}

package ru.func.raidarea.listener;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffectType;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.RaidClock;
import ru.func.raidarea.RaidTimeStatus;
import ru.func.raidarea.player.Shuffler;

@AllArgsConstructor
public class DamageListener implements Listener {

    private final RaidArea plugin;
    private final RaidClock raidClock;

    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        if (!raidClock.getTimeStatus().equals(RaidTimeStatus.GAME)) {
            e.setCancelled(true);
            return;
        }

        /* ENDERMAN DAMAGING */
        if (e.getEntity() instanceof Enderman) {
            e.setCancelled(true);
            return;
        }

        /* PLAYER DAMAGING */
        if (e.getEntity() instanceof Player) {

            pullDown((Player) e.getEntity());

            Player attacker;
            Shuffler raidPlayer;

            /* BY PLAYER */
            if (e.getDamager() instanceof Player) {
                attacker = (Player) e.getDamager();
                raidPlayer = plugin.getPlayers().get(attacker.getUniqueId());
                if (plugin.getPlayers().get(e.getEntity().getUniqueId()).isDefend() != raidPlayer.isDefend() &&
                        ((Player) e.getDamager()).getInventory().getItemInMainHand().getItemMeta() == null) {

                    e.setDamage(2);
                    raidPlayer.depositMoney(10);
                    attacker.sendMessage("§l+ 10 ETH §eЗа отличный удар.");
                    attacker.playSound(attacker.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                } else
                    e.setCancelled(true);
            }

            /* BY SNOWBALL */
            else if (e.getDamager() instanceof Snowball) {
                attacker = ((Player) ((Snowball) e.getDamager()).getShooter());
                raidPlayer = plugin.getPlayers().get(attacker.getUniqueId());
                if (plugin.getPlayers().get(e.getEntity().getUniqueId()).isDefend() != raidPlayer.isDefend()) {
                    e.setDamage(raidPlayer.getCurrentCharacter().getWeapon().getDamage());
                    raidPlayer.depositMoney(15);
                    attacker.sendMessage("§l+ 15 ETH §eЗа точное попадание в цель.");
                    attacker.playSound(attacker.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                } else
                    e.setCancelled(true);
            }
        } else
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            pullDown((Player) e.getEntity());

            e.setCancelled(e.getCause().equals(EntityDamageEvent.DamageCause.FALL));

            if (e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) ||
                    e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) ||
                    e.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING))
                e.setCancelled(!plugin.getPlayers().get(e.getEntity().getUniqueId()).isDefend());
        }
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent e) {
        e.setDeathMessage(null);
        e.getDrops().clear();
        if (e.getEntity().getKiller() != null) {
            Player player = e.getEntity().getKiller();
            Shuffler raidPlayer = plugin.getPlayers().get(player.getUniqueId());
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

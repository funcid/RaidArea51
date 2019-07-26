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
        // ОСТОРОЖНО, ХРУПКИЙ МЕТОД
        if (!PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME)) {
            e.setCancelled(true);
            return;
        }

        // ENDERMAN DAMAGING
        if (e.getEntity() instanceof Enderman) {
            e.setCancelled(true);
            return;
        }

        // PLAYER DAMAGING
        if (e.getEntity() instanceof Player) {

            pullDown((Player) e.getEntity());

            Player attacker;
            RaidPlayer raidPlayer;
            // BY PLAYER
            if (e.getDamager() instanceof Player) {
                attacker = (Player) e.getDamager();
                raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(attacker.getUniqueId());
                if (PLUGIN.getPlayers().get(e.getEntity().getUniqueId()).isDefend() != raidPlayer.isDefend()) {
                    if (((Player) e.getDamager()).getInventory().getItemInMainHand().getItemMeta() == null) {
                        e.setDamage(3);
                        raidPlayer.depositMoney(10);
                        attacker.sendMessage("§l+ 10 ETH §eЗа отличный удар.");
                        attacker.playSound(attacker.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                    } else
                        e.setCancelled(true);
                } else
                    e.setCancelled(true);
            }
            // BY SNOWBALL
            else if (e.getDamager() instanceof Snowball) {
                attacker = ((Player) ((Snowball) e.getDamager()).getShooter());
                raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(attacker.getUniqueId());
                if (PLUGIN.getPlayers().get(e.getEntity().getUniqueId()).isDefend() != raidPlayer.isDefend()) {
                    e.setDamage(raidPlayer.getCurrentCharacter().getGunWeapon().getDamage());
                    raidPlayer.depositMoney(15);
                    attacker.sendMessage("§l+ 15 ETH §eЗа точный попадание в цель.");
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
            if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL))
                e.setCancelled(true);
            else if (e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) ||
                    e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) ||
                    e.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING))
                e.setCancelled(!PLUGIN.getPlayers().get(e.getEntity().getUniqueId()).isDefend());
        }
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

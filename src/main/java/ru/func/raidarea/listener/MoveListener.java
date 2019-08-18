package ru.func.raidarea.listener;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.RaidStatus;

@AllArgsConstructor
public class MoveListener implements Listener {

    private final RaidArea PLUGIN;

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent e) {
        if (PLUGIN.getRaidSpawn().distance(e.getTo()) < 6) {
            Player player = e.getPlayer();
            if (player.getPassengers().size() > 0) {

                PLUGIN.setEndermanAmount(PLUGIN.getEndermanAmount() - 1);

                Entity entity = player.getPassengers().get(0);
                entity.remove();

                player.removePotionEffect(PotionEffectType.SLOW);
                player.removePotionEffect(PotionEffectType.BLINDNESS);

                if (PLUGIN.getEndermanAmount() == 0) {
                    PLUGIN.setAttackersWin(true);
                    PLUGIN.setGameStatus(RaidStatus.SEARCHED);
                    return;
                }

                Bukkit.broadcastMessage(String.format("[§b!§f] Пришелец спасен! Осталось спасти еще %d пришельцев.", PLUGIN.getEndermanAmount()));
            }
        }
    }
}

package ru.func.raidarea.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.func.raidarea.RaidArea;

public class MoveListener implements Listener {

    private final RaidArea PLUGIN;

    public MoveListener(final RaidArea plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent e) {
        if (PLUGIN.getRaidSpawn().distance(e.getTo()) < 6) {
            Player player = e.getPlayer();
            if (player.getPassengers().size() == 1) {
                PLUGIN.setAttackersWin(true);
                Entity entity = player.getPassengers().get(0);
                entity.remove();
            }
        }
    }
}

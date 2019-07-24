package ru.func.raidarea.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.func.raidarea.RaidArea;

public class MoveListener implements Listener {

    private final RaidArea PLUGIN;

    public MoveListener(RaidArea plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (PLUGIN.getRaidSpawn().distance(e.getTo()) < 6)
            if (e.getPlayer().getPassengers().size() == 1)
                PLUGIN.setAttackersWin(true);
    }
}

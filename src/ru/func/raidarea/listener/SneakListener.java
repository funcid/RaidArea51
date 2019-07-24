package ru.func.raidarea.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.RaidTimeStatus;

public class SneakListener implements Listener {

    private final RaidArea PLUGIN;

    public SneakListener(RaidArea plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void playerSneakEvent(PlayerToggleSneakEvent e) {
        if (PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME))
            PLUGIN.getPlayers().get(e.getPlayer().getUniqueId()).getCurrentCharacter().usePerk(e.getPlayer());
    }
}

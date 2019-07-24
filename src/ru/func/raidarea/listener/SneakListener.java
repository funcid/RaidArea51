package ru.func.raidarea.listener;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.RaidTimeStatus;

public class SneakListener implements Listener {

    private final RaidArea PLUGIN;

    public SneakListener(final RaidArea plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void playerSneakEvent(final PlayerToggleSneakEvent e) {
        if (PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME))
            if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL))
                PLUGIN.getPlayers().get(e.getPlayer().getUniqueId()).getCurrentCharacter().usePerk(e.getPlayer());
    }
}

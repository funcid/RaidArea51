package ru.func.raidarea.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
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
        Player player = e.getPlayer();
        if (PLUGIN.getTimeStatus().equals(RaidTimeStatus.GAME))
            if (player.getGameMode().equals(GameMode.SURVIVAL))
                PLUGIN.getPlayers().get(player.getUniqueId()).getCurrentCharacter().usePerk(player);
    }
}

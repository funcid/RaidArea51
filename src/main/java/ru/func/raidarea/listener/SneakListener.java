package ru.func.raidarea.listener;

import lombok.AllArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.RaidClock;
import ru.func.raidarea.RaidTimeStatus;

@AllArgsConstructor
public class SneakListener implements Listener {

    private final RaidArea plugin;
    private final RaidClock raidClock;

    @EventHandler
    public void playerSneakEvent(final PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if (raidClock.getTimeStatus().equals(RaidTimeStatus.GAME))
            if (player.getGameMode().equals(GameMode.SURVIVAL))
                if (plugin.getPlayers().containsKey(player.getUniqueId()))
                    plugin.getPlayers().get(player.getUniqueId()).getCurrentCharacter().usePerk(player);
    }
}

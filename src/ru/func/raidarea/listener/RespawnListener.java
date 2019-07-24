package ru.func.raidarea.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.player.RaidPlayer;

public class RespawnListener implements Listener {

    private final RaidArea PLUGIN;

    public RespawnListener(RaidArea plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(player.getUniqueId());

        e.setRespawnLocation(player.getLocation());

        player.setGameMode(GameMode.SPECTATOR);
        Bukkit.getScheduler().runTaskLater(PLUGIN, () -> {
            raidPlayer.getCurrentCharacter().giveAmmunition(player);
            player.setGameMode(GameMode.SURVIVAL);
            if (raidPlayer.isDefend())
                player.teleport(PLUGIN.getDefSpawn());
            else
                player.teleport(PLUGIN.getRaidSpawn());
        }, 100);
    }
}

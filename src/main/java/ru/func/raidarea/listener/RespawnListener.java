package ru.func.raidarea.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.player.RaidPlayer;

public class RespawnListener implements Listener {

    private final RaidArea PLUGIN;
    private final PotionEffect REGENERATION;

    public RespawnListener(final RaidArea plugin) {
        PLUGIN = plugin;
        REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 5);
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(player.getUniqueId());

        e.setRespawnLocation(player.getLocation());

        player.setGameMode(GameMode.SPECTATOR);
        Bukkit.getScheduler().runTaskLater(PLUGIN, () -> {
            raidPlayer.getCurrentCharacter().giveAmmunition(player);
            PLUGIN.giveItems(player);
            player.setGameMode(GameMode.SURVIVAL);
            player.addPotionEffect(REGENERATION);
            player.teleport(raidPlayer.isDefend() ? PLUGIN.getDefSpawn() : PLUGIN.getRaidSpawn());
        }, 120);
    }
}

package ru.func.raidarea.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

public class UsualListener implements Listener {

    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void inEntityTeleportation(final EntityTeleportEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void noHungry(final FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }
}

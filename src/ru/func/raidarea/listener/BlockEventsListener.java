package ru.func.raidarea.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class BlockEventsListener implements Listener {

    @EventHandler
    public void onBlockFall(final EntityChangeBlockEvent e) {
        if ((e.getEntityType().equals(EntityType.FALLING_BLOCK)))
            if (e.getTo().equals(Material.IRON_BLOCK))
                createExplode(e.getBlock().getLocation());
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(!(e.getBlock().getType().equals(Material.FENCE) && e.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null));
    }

    @EventHandler
    public void onItemSpawn(final ItemSpawnEvent e) {
        if (e.getEntity().getItemStack().getType().equals(Material.IRON_BLOCK))
            createExplode(e.getLocation());
        e.setCancelled(true);
    }

    @EventHandler
    public void explodeEntityEvent(EntityExplodeEvent e) {
        explode(e.blockList(), e.getLocation(), 0.2F);
        e.setCancelled(true);
    }
    @EventHandler
    public void explodeBlockEvent(BlockExplodeEvent e) {
        explode(e.blockList(), e.getBlock().getLocation(), 0.2F);
    }

    @EventHandler
    public void onArrowShoot(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            if (e.getHitBlock() != null) e.getHitBlock().getWorld().createExplosion(e.getHitBlock().getLocation(), 2);
            else e.getHitEntity().setVelocity(e.getEntity().getVelocity());
            e.getEntity().remove();
        }
    }

    private void explode(List<Block> blockList, Location center, float power) {
        for (Block block : blockList) {
            if (block.getType().equals(Material.LEVER))
                continue;
            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), (byte) 0);
            fallingBlock.setDropItem(false);
            fallingBlock.setVelocity(center.toVector().subtract(block.getLocation().toVector()).subtract(new Vector(0, -2, 0)).multiply(power));

            block.setType(Material.AIR);
        }
    }

    private void createExplode(final Location location) {
        location.getWorld().createExplosion(location, 2);
    }
}

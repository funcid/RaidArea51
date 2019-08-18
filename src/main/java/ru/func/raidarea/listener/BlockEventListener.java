package ru.func.raidarea.listener;

import lombok.AllArgsConstructor;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import ru.func.raidarea.RaidArea;
import ru.func.raidarea.player.RaidPlayer;

import java.util.List;

@AllArgsConstructor
public class BlockEventListener implements Listener {

    private final RaidArea PLUGIN;
    //private final Map<Location, Integer> breakingBlocks = Maps.newHashMap();

    @EventHandler
    public void onBlockFall(final EntityChangeBlockEvent e) {
        if ((e.getEntityType().equals(EntityType.FALLING_BLOCK)))
            if (e.getTo().equals(Material.IRON_BLOCK))
                createExplode(e.getBlock().getLocation());
        e.getEntity().remove();
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null) {
            if (!e.getBlock().getType().equals(Material.FENCE)) {
                Material material = e.getBlock().getType();
                Bukkit.getScheduler().runTaskLater(PLUGIN, () -> e.getBlock().setType(material), 80L);
            }
        } else
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent e) {
        e.setCancelled(true);

        if (e.getBlock().getType().equals(Material.FENCE)) {
            RaidPlayer raidPlayer = (RaidPlayer) PLUGIN.getPlayers().get(e.getPlayer().getUniqueId());
            if (raidPlayer.getMoney() >= 75) {
                raidPlayer.depositMoney(-75);
                e.setCancelled(false);
                PLUGIN.giveItems(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBlockIgnite(final BlockIgniteEvent event) {
        event.setCancelled(event.getCause().equals(BlockIgniteEvent.IgniteCause.LIGHTNING));
    }

    @EventHandler
    public void onItemSpawn(final ItemSpawnEvent e) {
        if (e.getEntity().getItemStack().getType().equals(Material.IRON_BLOCK))
            createExplode(e.getLocation());
        e.setCancelled(true);
    }

    @EventHandler
    public void explodeEntityEvent(final EntityExplodeEvent e) {
        explode(e.blockList(), e.getLocation(), 0.2F);
        e.setCancelled(true);
    }

    @EventHandler
    public void explodeBlockEvent(final BlockExplodeEvent e) {
        explode(e.blockList(), e.getBlock().getLocation(), 0.22F);
        e.setCancelled(true);
    }

    @EventHandler
    public void onArrowShoot(final ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            if (e.getHitBlock() != null) createExplode(e.getHitBlock().getLocation());
            else e.getHitEntity().setVelocity(e.getEntity().getVelocity().multiply(2));
        }
        if (!(e.getEntity() instanceof FishHook))
            e.getEntity().remove();
    }

    private void explode(final List<Block> blockList, final Location center, final float power) {
        Bukkit.getScheduler().runTask(PLUGIN, () -> {
            for (Block block : blockList) {

                /*
                int step = breakingBlocks.getOrDefault(block.getLocation(), 0) + 3;
                if (step == 9)
                    block.setType(Material.AIR);
                PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(new Random().nextInt(1000), new BlockPosition(block.getX(), block.getY(), block.getZ()), step);
                breakingBlocks.put(block.getLocation(), step);

                for (Player p : Bukkit.getServer().getOnlinePlayers())
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                 */

                if (block.getType().equals(Material.LEVER))
                    continue;
                FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), (byte) 0);
                fallingBlock.setDropItem(false);
                fallingBlock.setVelocity(center.toVector().subtract(block.getLocation().toVector()).subtract(new Vector(0, -2, 0)).multiply(power));

                block.setType(Material.AIR);
            }
        });
    }

    private void createExplode(final Location location) {
        location.getWorld().createExplosion(location, 3);
    }
}

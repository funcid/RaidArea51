package ru.func.raidarea.weapon;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Gun implements IGun {

    private @Getter
    ItemStack itemStack;
    private @Getter
    double damage;
    private Material material;
    private Material clipMaterial;
    private String name;
    private int bullets;
    private int delay;

    private final Map<UUID, Long> weaponDelay = Maps.newHashMap();

    private boolean hasCountdown(final UUID user) {
        Long data = weaponDelay.get(user);
        return data != null && data > System.currentTimeMillis();
    }

    private void setCountdown(final UUID user, final int val) {
        weaponDelay.put(user, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(val));
    }

    private long getSecondsLeft(final UUID user) {
        return TimeUnit.MILLISECONDS.toSeconds(weaponDelay.get(user) - System.currentTimeMillis());
    }

    Gun(final GunBuilder gunBuilder) {
        this.material = gunBuilder.getMaterial();
        this.bullets = gunBuilder.getBullets();
        this.delay = gunBuilder.getDelay();
        this.clipMaterial = gunBuilder.getClipMaterial();
        this.itemStack = gunBuilder.getItemStack();
        this.name = gunBuilder.getName();
        this.damage = gunBuilder.getDamage();
    }

    @Override
    public void strike(final Player player) {
        ItemStack gun = player.getInventory().getItemInMainHand();

        if (gun.getType().equals(material)) {
            ItemMeta meta = gun.getItemMeta();
            if ((int) gun.getDurability() > 0) {
                player.setVelocity(player.getEyeLocation().getDirection().multiply(-0.02));

                Snowball bullet = player.launchProjectile(Snowball.class);
                bullet.setVelocity(bullet.getVelocity().multiply(2));

                gun.setDurability((short) (gun.getDurability() - 1));
                meta.setDisplayName(String.format(name, (int) gun.getDurability()));

                if (gun.getDurability() == 0)
                    setCountdown(player.getUniqueId(), delay);
            } else {
                if (player.getInventory().contains(clipMaterial)) {
                    if (hasCountdown(player.getUniqueId())) {
                        StringBuilder stars = new StringBuilder();
                        for (long i = getSecondsLeft(player.getUniqueId()); i < delay; i++)
                            stars.append("+");
                        for (int i = 0; i < getSecondsLeft(player.getUniqueId()); i++)
                            stars.append("_");
                        meta.setDisplayName(String.format(name.replace("%d", "%s"), stars.toString()));
                    } else {
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_STEP, 1, 1);
                        gun.setDurability((short) bullets);

                        Stream.of(player.getInventory().getContents())
                                .filter(Objects::nonNull)
                                .filter(item -> item.getType().equals(clipMaterial))
                                .findAny()
                                .ifPresent(item -> item.setAmount(item.getAmount() - 1));
                    }
                } else
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
            }
            gun.setItemMeta(meta);
        }
    }
}

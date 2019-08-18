package ru.func.raidarea.weapon;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Weaponry {

    /**
     * @return item of this weapon.
     */
    ItemStack getItemStack();

    /**
     * @return damage of player hit.
     */
    double getDamage();

    /**
     * @param player player that use weapon
     */
    void onUse(final Player player);
}

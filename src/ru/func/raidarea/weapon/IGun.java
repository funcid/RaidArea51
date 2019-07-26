package ru.func.raidarea.weapon;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IGun {

    /**
     * This method shoots and also reloads the weapon with beautiful animation.
     * @param player that would strike/reload his weapon.
     */
    void strike(final Player player);

    /**
     * @return item of thist weapon.
     */
    ItemStack getItemStack();

    /**
     * @return damage of player hit.
     */
    double getDamage();
}

package ru.func.raidarea.weapon.gun;

import org.bukkit.entity.Player;
import ru.func.raidarea.weapon.Weaponry;

public interface Shooting extends Weaponry {

    /**
     * This method shoots and also reloads the weapon with beautiful animation.
     *
     * @param player that would strike/reload his weapon.
     */
    void strike(final Player player);
}

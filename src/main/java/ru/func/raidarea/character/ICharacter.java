package ru.func.raidarea.character;

import org.bukkit.entity.Player;
import ru.func.raidarea.weapon.IGun;

public interface ICharacter {

    /**
     * @param user player that use his unique perk.
     */
    void usePerk(final Player user);

    /**
     * @return name of character.
     */
    String getName();

    /**
     * @param currentPlayer is player that will get custom ammunition.
     */
    void giveAmmunition(final Player currentPlayer);

    /**
     * @return weapon that have this character.
     */
    IGun getGunWeapon();
}

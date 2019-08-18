package ru.func.raidarea.player;

import ru.func.raidarea.character.ICharacter;

public interface IPlayer {

    /**
     * @return money that have player.
     */
    int getMoney();

    /**
     * @return character that use attacker player
     */
    ICharacter getCurrentCharacter();

    /**
     * @return amount of kills
     */
    int getKills();

    /**
     * @return amount of wins
     */
    int getWins();

    /**
     * @return true if player defend zone 51
     */
    boolean isDefend();

    void setMoney(final int money);

    void setCurrentCharacter(final ICharacter currentCharacter);

    void setKills(final int kills);

    void setWins(final int wins);

    void setDefend(final boolean defend);

    /**
     * setMoney(getMoney() + money)
     *
     * @param money that introduced
     */
    void depositMoney(final int money);
}

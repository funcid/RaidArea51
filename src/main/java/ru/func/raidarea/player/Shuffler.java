package ru.func.raidarea.player;

import ru.func.raidarea.character.Characterful;

public interface Shuffler {

    /**
     * @return money that have player.
     */
    int getMoney();

    /**
     * @return character that use attacker player
     */
    Characterful getCurrentCharacter();

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

    void setCurrentCharacter(final Characterful currentCharacter);

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

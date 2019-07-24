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

    void setMoney(int money);


    void setCurrentCharacter(ICharacter currentCharacter);

    void setKills(int kills);

    void setWins(int wins);

    void setDefend(boolean defend);
}

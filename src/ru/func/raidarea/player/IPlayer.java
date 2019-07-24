package ru.func.raidarea.player;

import ru.func.raidarea.character.ICharacter;

public interface IPlayer {

    /**
     * @return money that have player.
     */
    int getMoney();

    /**
     * Why clef and no keys? SQL ranked name 'keys'.
     * @return value of keys that have player.
     */
    int getClef();

    /**
     * @return character that use attacker player
     */
    ICharacter getCurrentCharacter();

    /**
     * @return all characters that can use player
     */
    ICharacter[] getCharacters();

    /**
     * @return amount of kills
     */
    int getKills();

    /**
     * @return amount of wins
     */
    int getWins();

    /**
     * @return player level
     */
    int getLevel();

    /**
     * @return true if player defend zone 51
     */
    boolean isDefend();

    void setMoney(int money);

    void setClef(int clef);

    void setCurrentCharacter(ICharacter currentCharacter);

    void setCharacters(ICharacter[] characters);

    void setKills(int kills);

    void setWins(int wins);

    void setDefend(boolean defend);

    void setLevel(int level);
}

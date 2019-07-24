package ru.func.raidarea.player;

import ru.func.raidarea.character.ICharacter;

import java.util.Arrays;

public class RaidPlayer implements IPlayer {

    private int money;
    private int clef;
    private ICharacter currentCharacter;
    private ICharacter characters[];
    private int kills;
    private int wins;
    private boolean defend;
    private int level;

    RaidPlayer(final PlayerBuilder playerBuilder) {
        this.money = playerBuilder.getMoney();
        this.clef = playerBuilder.getClef();
        this.currentCharacter = playerBuilder.getCurrentCharacter();
        this.kills = playerBuilder.getKills();
        this.wins = playerBuilder.getWins();
        this.characters = playerBuilder.getCharacters();
        this.defend = playerBuilder.isDefend();
        this.level = playerBuilder.getLevel();
    }

    public String getStringAllowCharacters() {
        StringBuilder stringBuilder = new StringBuilder(60);
        Arrays.stream(getCharacters()).forEach(character -> stringBuilder.append(character.getName()).append(","));
        return stringBuilder.toString();
    }

    @Override
    public ICharacter[] getCharacters() {
        return characters;
    }

    @Override
    public int getMoney() {
        return money;
    }

    @Override
    public int getClef() {
        return clef;
    }

    @Override
    public ICharacter getCurrentCharacter() {
        return currentCharacter;
    }

    @Override
    public int getKills() {
        return kills;
    }

    @Override
    public int getWins() {
        return wins;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public boolean isDefend() {
        return defend;
    }

    @Override
    public void setMoney(final int money) {
        this.money = money;
    }

    @Override
    public void setClef(final int clef) {
        this.clef = clef;
    }

    @Override
    public void setCurrentCharacter(final ICharacter currentCharacter) {
        this.currentCharacter = currentCharacter;
    }

    @Override
    public void setCharacters(final ICharacter[] characters) {
        this.characters = characters;
    }

    @Override
    public void setKills(final int kills) {
        this.kills = kills;
    }

    @Override
    public void setWins(final int wins) {
        this.wins = wins;
    }

    @Override
    public void setDefend(final boolean defend) {
        this.defend = defend;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }
}

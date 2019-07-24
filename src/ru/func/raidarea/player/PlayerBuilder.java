package ru.func.raidarea.player;

import ru.func.raidarea.character.CharacterUtil;
import ru.func.raidarea.character.ICharacter;

import java.util.Arrays;

public class PlayerBuilder {

    private int money;
    private int clef;
    private ICharacter currentCharacter;
    private ICharacter characters[];
    private int kills;
    private int wins;
    private boolean defend;
    private int level;

    public PlayerBuilder money(final int money) {
        this.money = money;
        return this;
    }

    public PlayerBuilder clef(final int clef) {
        this.clef = clef;
        return this;
    }

    public PlayerBuilder currentCharacter(final ICharacter currentCharacter) {
        this.currentCharacter = currentCharacter;
        return this;
    }

    public PlayerBuilder characters(final String... names) {
        this.characters = Arrays.stream(names).map(CharacterUtil::getCharacterByName).toArray(ICharacter[]::new);
        return this;
    }

    public PlayerBuilder kills(final int kills) {
        this.kills = kills;
        return this;
    }

    public PlayerBuilder wins(final int wins) {
        this.wins = wins;
        return this;
    }

    public PlayerBuilder defend(final boolean defend) {
        this.defend = defend;
        return this;
    }

    public PlayerBuilder level(final int level) {
        this.level = level;
        return this;
    }

    public RaidPlayer build() {
        return new RaidPlayer(this);
    }

    int getMoney() {
        return money;
    }

    int getClef() {
        return clef;
    }

    ICharacter getCurrentCharacter() {
        return currentCharacter;
    }

    int getKills() {
        return kills;
    }

    int getWins() {
        return wins;
    }

    ICharacter[] getCharacters() {
        return characters;
    }

    boolean isDefend() {
        return defend;
    }

    int getLevel() {
        return level;
    }
}

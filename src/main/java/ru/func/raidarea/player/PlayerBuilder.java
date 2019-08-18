package ru.func.raidarea.player;

import lombok.Getter;
import ru.func.raidarea.character.ICharacter;

@Getter
public class PlayerBuilder {

    private int money;
    private ICharacter currentCharacter;
    private int kills;
    private int wins;
    private boolean defend;

    public PlayerBuilder money(final int money) {
        this.money = money;
        return this;
    }

    public PlayerBuilder currentCharacter(final ICharacter currentCharacter) {
        this.currentCharacter = currentCharacter;
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


    public RaidPlayer build() {
        return new RaidPlayer(this);
    }
}

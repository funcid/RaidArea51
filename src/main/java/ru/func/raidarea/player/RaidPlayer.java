package ru.func.raidarea.player;

import lombok.Getter;
import lombok.Setter;
import ru.func.raidarea.character.ICharacter;

public class RaidPlayer implements IPlayer {

    @Getter
    @Setter
    private int money;
    @Getter
    @Setter
    private ICharacter currentCharacter;
    @Getter
    @Setter
    private int kills;
    @Getter
    @Setter
    private int wins;
    @Getter
    @Setter
    private boolean defend;

    RaidPlayer(final PlayerBuilder playerBuilder) {
        this.money = playerBuilder.getMoney();
        this.currentCharacter = playerBuilder.getCurrentCharacter();
        this.kills = playerBuilder.getKills();
        this.wins = playerBuilder.getWins();
        this.defend = playerBuilder.isDefend();
    }

    @Override
    public void depositMoney(final int money) {
        this.money += money;
    }
}

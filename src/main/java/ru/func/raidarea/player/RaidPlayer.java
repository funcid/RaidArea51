package ru.func.raidarea.player;

import lombok.Getter;
import lombok.Setter;
import ru.func.raidarea.character.ICharacter;

@Getter
@Setter
public class RaidPlayer implements IPlayer {

    private int money;
    private ICharacter currentCharacter;
    private int kills;
    private int wins;
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

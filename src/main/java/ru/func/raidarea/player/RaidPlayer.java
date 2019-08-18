package ru.func.raidarea.player;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.func.raidarea.character.Characterful;

@Getter
@Setter
@Builder
public class RaidPlayer implements Shuffler {

    private int money;
    private Characterful currentCharacter;
    private int kills;
    private int wins;
    private boolean defend;

    @Override
    public void depositMoney(final int money) {
        this.money += money;
    }
}

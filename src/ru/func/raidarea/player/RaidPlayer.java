package ru.func.raidarea.player;

import ru.func.raidarea.character.ICharacter;

public class RaidPlayer implements IPlayer {

    private int                   money;
    private ICharacter currentCharacter;
    private int                   kills;
    private int                    wins;
    private boolean              defend;

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

    @Override
    public int getMoney() {
        return money;
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
    public boolean isDefend() {
        return defend;
    }

    @Override
    public void setMoney(final int money) {
        this.money = money;
    }

    @Override
    public void setCurrentCharacter(final ICharacter currentCharacter) {
        this.currentCharacter = currentCharacter;
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
}

package ru.func.raidarea;

public enum RaidStatus {

    ACTIVE_STATION("Станция активна"),
    DIACTIVATED_STATION("Станция диактивирована"),
    SEARCH("Инопланетянин в бегах"),
    ;

    private String name;

    RaidStatus(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

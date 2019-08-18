package ru.func.raidarea;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RaidStatus {

    ACTIVE_STATION("Станция активна"),
    DEACTIVATED_STATION("Станция не активна"),
    SEARCH("Нужно спасти еще %d"),
    SEARCHED("Пришельца спасены"),
    ;

    @Getter
    private String name;
}

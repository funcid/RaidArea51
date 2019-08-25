package ru.func.raidarea;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RaidTimeStatus {

    WAITING(10, "Ожидание"),
    STARTING(20, "Начало"),
    GAME(600, "Игра"),
    ENDING(615, "Завершение"),
    ;
    private int time;
    private String name;
}

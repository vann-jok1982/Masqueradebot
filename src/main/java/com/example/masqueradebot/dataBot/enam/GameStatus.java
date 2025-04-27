package com.example.masqueradebot.dataBot.enam;

public enum GameStatus {
    WAITING_FOR_PLAYERS("Ожидание игроков"),
    IN_PROGRESS("Игра в процессе"),
    WAITING_FOR_ANSWER("Ожидание ответа"),
    WAITING_TO_START_DISCUSSION("Ожидание начала обсуждения"),
    FINISHED("Игра завершена");

    private final String description;

    GameStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
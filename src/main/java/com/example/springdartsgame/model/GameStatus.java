package com.example.springdartsgame.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GameStatus {
    CREATED("created"),
    STARTED("started"),
    PLAYING("playing"),
    USER_WINS("%s wins!"),
    NOBODY_WINS("Nobody wins!");

    public final String status;
}

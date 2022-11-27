package com.example.springdartsgame.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class MoveDTO {
    private long gameId;

    private int move;

    private String playerOne;

    private String playerTwo;

    private String gameStatus;

    private int playerOneScores;

    private int playerTwoScores;

    private String turn;
}

package com.example.springdartsgame.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "game_move")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Move {
    @Id
    @GeneratedValue
    private long id;

    @Column
    private long gameId;

    @Column
    private int move;

    @Column
    private String playerOne;

    @Column
    private String playerTwo;

    @Column
    private String gameStatus;

    @Column
    private int playerOneScores;

    @Column
    private int playerTwoScores;

    @Column
    private String turn;
}

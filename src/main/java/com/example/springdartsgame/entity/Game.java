package com.example.springdartsgame.entity;

import com.example.springdartsgame.model.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "game")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Game {
    @Id
    @GeneratedValue
    private Long id; // Auto-generated

    @Column
    private String playerOne; // Taken from the authentication

    @Column
    private String playerTwo; // Taken from the authentication

    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus; // Can be "created" or "started" or "playing" or "username wins!"

    @Column
    private int playerOneScores; // Only 101, 301 or 501 are allowed

    @Column
    private int playerTwoScores; // Only 101, 301 or 501 are allowed

    @Column
    private String turn; // Can be "playerOne" or "playerTwo"
}

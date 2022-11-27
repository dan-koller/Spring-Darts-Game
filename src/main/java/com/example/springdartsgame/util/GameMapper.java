package com.example.springdartsgame.util;

import com.example.springdartsgame.entity.Game;
import com.example.springdartsgame.entity.Move;
import com.example.springdartsgame.model.GameStatus;
import com.example.springdartsgame.model.dto.GameDTO;
import com.example.springdartsgame.model.dto.MoveDTO;

import java.util.Arrays;

/**
 * This utility class is used to map the Game and Move entities to the Game and Move DTOs.
 * It is required for handling requests and responses.
 */
public class GameMapper {

    /**
     * This method maps a given Move to a MoveDTO for the response.
     *
     * @param move - The Move object to be mapped
     * @return - The MoveDTO object as api response
     */
    public static MoveDTO mapToMoveDTO(Move move) {
        // Validate the move's status
        String status = move.getGameStatus().matches("\\W+ wins!")
                ? String.format("%s wins!", move.getPlayerOneScores() == 0
                ? move.getPlayerOne() : move.getPlayerTwo())
                : move.getGameStatus();

        // Build a new MoveDTO object and return it
        return MoveDTO.builder()
                .gameId(move.getGameId())
                .move(move.getMove())
                .playerOne(move.getPlayerOne())
                .playerTwo(move.getPlayerTwo())
                .gameStatus(status)
                .playerOneScores(move.getPlayerOneScores())
                .playerTwoScores(move.getPlayerTwoScores())
                .turn(move.getTurn())
                .build();
    }

    /**
     * This method maps a given Game to a GameDTO for the response.
     *
     * @param game - The Game object to be mapped
     * @return - The GameDTO object as api response
     */
    public static GameDTO mapToGameDTO(Game game) {
        // Validate the game's status
        String status = game.getGameStatus() == GameStatus.USER_WINS
                ? String.format("%s wins!", game.getPlayerOneScores() == 0
                ? game.getPlayerOne() : game.getPlayerTwo())
                : game.getGameStatus().status;

        // Build a new GameDTO object and return it
        return GameDTO.builder()
                .gameId(game.getId())
                .playerOne(game.getPlayerOne())
                .playerTwo(game.getPlayerTwo())
                .gameStatus(status)
                .playerOneScores(game.getPlayerOneScores())
                .playerTwoScores(game.getPlayerTwoScores())
                .turn(game.getTurn())
                .build();
    }

    /**
     * This method maps a certain move to a GameDTO for the response.
     *
     * @param move - The Move object to be mapped
     * @return - The GameDTO object built from the Move as api response
     */
    public static Game mapMoveToGame(Move move) {
        // Get the current game status based on the move's status
        GameStatus status = Arrays.stream(GameStatus.values())
                .filter(value -> value.status.equals(move.getGameStatus()))
                // If the move's status is not found, set the game status to USER_WINS
                .findFirst().orElse(GameStatus.USER_WINS);

        // Build a new Game object and return it
        return Game.builder()
                .id(move.getGameId())
                .playerOne(move.getPlayerOne())
                .playerTwo(move.getPlayerTwo())
                .gameStatus(status)
                .playerOneScores(move.getPlayerOneScores())
                .playerTwoScores(move.getPlayerTwoScores())
                .turn(move.getTurn())
                .build();
    }

    /**
     * This method maps a certain game to a MoveDTO for the response.
     *
     * @param game - The Game object to be mapped
     * @return - The MoveDTO object built from the Game as api response
     */
    public static Move mapGameToMove(Game game) {
        // Get the current move status based on the game's status
        String status = game.getGameStatus() == GameStatus.USER_WINS
                ? String.format("%s wins!", game.getPlayerOneScores() == 0
                ? game.getPlayerOne() : game.getPlayerTwo())
                : game.getGameStatus().status;

        // Build a new Move object and return it
        return Move.builder()
                .gameId(game.getId())
                .playerOne(game.getPlayerOne())
                .playerTwo(game.getPlayerTwo())
                .gameStatus(status)
                .playerOneScores(game.getPlayerOneScores())
                .playerTwoScores(game.getPlayerTwoScores())
                .turn(game.getTurn())
                .build();
    }
}

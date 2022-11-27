package com.example.springdartsgame.service;

import com.example.springdartsgame.entity.Game;
import com.example.springdartsgame.model.GameStatus;
import com.example.springdartsgame.model.ThrowPoints;
import com.example.springdartsgame.model.Throws;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class contains the logic for the game progress (e.g. updating points, game status, etc.).
 * It also contains the main logic for a darts game.
 */
@Component
public class GameProgressService {
    private enum Player {ONE, TWO} // This enum is used to determine the current player

    public Game updatePoints(Game game, String currentUser, Throws darts) {
        // Get the players and their scores
        Player currentPlayer = game.getPlayerOne().equals(currentUser) ? Player.ONE : Player.TWO;
        String nextPlayer = currentPlayer == Player.ONE ? game.getPlayerTwo() : game.getPlayerOne();
        int scores = currentPlayer == Player.ONE ? game.getPlayerOneScores() : game.getPlayerTwoScores();

        // Get the throw of the current player
        List<ThrowPoints> playerThrows = Stream.of(darts.first(), darts.second(), darts.third())
                .filter(t -> !"none".equals(t))
                .map(points -> Arrays.stream(points.split(":")).mapToInt(Integer::valueOf).toArray())
                .map(a -> new ThrowPoints(a[0], a[1]))
                .toList();

        // Calculate the new score and the remaining score
        int throwsPoints = playerThrows.stream().mapToInt(s -> s.sector() * s.multiplier()).sum();
        int scoreRemains = scores - throwsPoints;

        // Update the game status
        game.setGameStatus(GameStatus.PLAYING);

        // Check if the game is won after the current throw
        if (checkVictory(playerThrows, scores)) {
            game.setGameStatus(GameStatus.USER_WINS);
        } else {
            game.setTurn(nextPlayer);
            if (scoreRemains == 0) scoreRemains = scores;
        }

        // Update the scores
        if (scoreRemains >= 0 && scoreRemains != 1) {
            if (currentPlayer == Player.ONE) game.setPlayerOneScores(scoreRemains);
            else game.setPlayerTwoScores(scoreRemains);
        }

        // Check if the game is a draw or bust
        if (checkBust(playerThrows, scores)) throw new RuntimeException();

        return game;
    }

    /**
     * This method checks if the game is won after the current throw
     *
     * @param playerThrows - The throw of the current player
     * @param score        - The score of the current player
     * @return - True if the game is won, false otherwise
     */
    private boolean checkVictory(List<ThrowPoints> playerThrows, int score) {
        for (var s : playerThrows) {
            score -= s.sector() * s.multiplier();
            if (score == 0 && s.multiplier() == 2) return true;
        }
        return false;
    }

    /**
     * This method checks if the game is a draw or bust
     *
     * @param playerThrows - The throw of the current player
     * @param score        - The score of the current player
     * @return - True if the game is a draw or bust, false otherwise
     */
    private boolean checkBust(List<ThrowPoints> playerThrows, int score) {
        for (int i = 0; i <= playerThrows.size() - 1; i++) {
            ThrowPoints shot = playerThrows.get(i);
            score -= shot.sector() * shot.multiplier();
            if (score <= 1 && i != playerThrows.size() - 1) return true;
        }
        return false;
    }
}

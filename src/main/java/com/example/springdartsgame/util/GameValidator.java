package com.example.springdartsgame.util;

import com.example.springdartsgame.entity.Game;
import com.example.springdartsgame.entity.Move;
import com.example.springdartsgame.model.GameStatus;
import com.example.springdartsgame.model.Result;
import com.example.springdartsgame.model.Throws;
import com.example.springdartsgame.model.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class GameValidator {
    public ResponseEntity<?> validateGame(Game game, String user, List<Game> unfinishedGames) {
        return game == null ?
                new ResponseEntity<>(new Result("Game not found!"), HttpStatus.NOT_FOUND)
                : game.getPlayerOne().equals(user) ?
                new ResponseEntity<>(new Result("You can't play alone!"), HttpStatus.BAD_REQUEST)
                : game.getGameStatus() != GameStatus.CREATED ?
                new ResponseEntity<>(new Result("You can't join the game!"), HttpStatus.BAD_REQUEST)
                : !unfinishedGames.isEmpty() ?
                new ResponseEntity<>(new Result("You have an unfinished game!"), HttpStatus.BAD_REQUEST)
                : null;
    }

    public ResponseEntity<?> validateUserGame(List<Game> unfinishedGames) {
        return !unfinishedGames.isEmpty() ?
                new ResponseEntity<>(new Result("You have an unfinished game!"), HttpStatus.BAD_REQUEST)
                : null;
    }

    public ResponseEntity<?> validateOngoingGame(String user, Game currentGame) {
        return currentGame == null ?
                new ResponseEntity<>(new Result("There are no games available!"), HttpStatus.NOT_FOUND)
                : !Objects.equals(currentGame.getTurn(), user) ?
                new ResponseEntity<>(new Result("Wrong turn!"), HttpStatus.BAD_REQUEST)
                : null;
    }

    public ResponseEntity<?> validateGameId(String id) {
        try {
            var gameId = Long.parseLong(id);
            return gameId < 0 ? new ResponseEntity<>(new Result("Wrong request!"), HttpStatus.BAD_REQUEST) : null;
        } catch (NumberFormatException e) { // TODO: May also be Exception e
            return new ResponseEntity<>(new Result("Wrong request!"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> validateGameHistory(List<Move> moveHistory) {
        return moveHistory.isEmpty() ? new ResponseEntity<>(new Result("Game not found!"), HttpStatus.NOT_FOUND) : null;
    }

    public ResponseEntity<?> validateGameUpdate(Update gameUpdate, Game game) {
        String newWinner = gameUpdate.status().replaceAll(" .*", "");
        return game == null ?
                new ResponseEntity<>(new Result("Game not found!"), HttpStatus.NOT_FOUND)
                : !newWinner.equals("Nobody") && !Objects.equals(game.getPlayerOne(), newWinner)
                && !Objects.equals(game.getPlayerTwo(), newWinner) ?
                new ResponseEntity<>(new Result("Wrong status!"), HttpStatus.BAD_REQUEST)
                :game.getGameStatus() == GameStatus.USER_WINS ?
                new ResponseEntity<>(new Result("The game is already over!"), HttpStatus.BAD_REQUEST)
                : null;
    }

    public ResponseEntity<?> validateGameRevert(Game oldGame, Move move, Move lastMove) {
        return oldGame == null ?
                new ResponseEntity<>(new Result("Game not found!"), HttpStatus.NOT_FOUND)
                : move == null ?
                new ResponseEntity<>(new Result("Move not found!"), HttpStatus.BAD_REQUEST)
                : lastMove.equals(move) ?
                new ResponseEntity<>(new Result("There is nothing to revert!"), HttpStatus.BAD_REQUEST)
                : oldGame.getGameStatus() == GameStatus.USER_WINS ?
                new ResponseEntity<>(new Result("The game is over!"), HttpStatus.BAD_REQUEST)
                : null;
    }

    public ResponseEntity<?> validateTargetScore(int targetScore) {
        return !List.of(101, 301, 501).contains(targetScore) ?
                new ResponseEntity<>(new Result("Wrong target score!"), HttpStatus.BAD_REQUEST)
                : null;
    }

    public ResponseEntity<?> validateThrows(Throws dartThrows) {
        return !Stream.of(dartThrows.first(), dartThrows.second(), dartThrows.third())
                .allMatch(t -> t.matches("(none)|([12]:25)|([1-3]:20)|([1-3]:1[0-9])|([1-3]:[0-9])")) ?
                new ResponseEntity<>(new Result("Wrong throws!"), HttpStatus.BAD_REQUEST)
                : null;
    }
}

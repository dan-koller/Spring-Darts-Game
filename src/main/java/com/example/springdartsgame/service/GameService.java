package com.example.springdartsgame.service;

import com.example.springdartsgame.entity.Game;
import com.example.springdartsgame.entity.Move;
import com.example.springdartsgame.model.*;
import com.example.springdartsgame.model.dto.GameDTO;
import com.example.springdartsgame.model.dto.MoveDTO;
import com.example.springdartsgame.persistence.GameRepository;
import com.example.springdartsgame.util.GameMapper;
import com.example.springdartsgame.util.GameValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameProgressService gameProgressService;
    @Autowired
    private GameValidator gameValidator;
    @Autowired
    private MoveService moveService;

    /**
     * This method creates a new game and saves it to the database
     *
     * @param playerOne   - The name of the player that initiates the game
     * @param targetScore - The target score for the game
     * @return - The Game object as data transfer object (DTO)
     */
    public ResponseEntity<?> createGame(String playerOne, int targetScore) {
        // Validate the target score
        var invalidTargetScore = gameValidator.validateTargetScore(targetScore);
        if (invalidTargetScore != null) return invalidTargetScore;

        // Check if the player has an unfinished game
        List<Game> unfinishedGames = gameRepository
                .findAllUnfinishedPlayerGames(playerOne).stream()
                .filter(g -> g.getGameStatus() != GameStatus.USER_WINS)
                .toList();

        // Validate the game
        var invalidGame = gameValidator.validateUserGame(unfinishedGames);
        if (invalidGame != null) return invalidGame;

        // Create a new game
        Game newGame = gameRepository.save(Game.builder()
                .gameStatus(GameStatus.CREATED)
                .playerOne(playerOne)
                .playerTwo("")
                .playerOneScores(targetScore)
                .playerTwoScores(targetScore)
                .turn(playerOne)
                .build());

        return new ResponseEntity<>(GameMapper.mapToGameDTO(newGame), HttpStatus.OK);
    }

    /**
     * This method returns all the games in the database in descending order by gameId
     *
     * @return - The list of games
     */
    public ResponseEntity<?> listAllGames() {
        List<GameDTO> games = gameRepository
                .findAllGamesSortedById()
                .stream()
                .map(GameMapper::mapToGameDTO).toList();

        return new ResponseEntity<>(games, games.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    /**
     * This method joins a game
     *
     * @param newPlayer - The name of the player that joins the game
     * @param gameId    - The id of the game
     * @return - The Game object
     */
    public ResponseEntity<?> joinGame(String newPlayer, long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);

        // Check if the new player has unfinished games
        List<Game> unfinishedGames = gameRepository
                .findAllUnfinishedPlayerGames(newPlayer).stream()
                .filter(g -> g.getGameStatus() != GameStatus.USER_WINS)
                .toList();

        // Validate the game
        var invalidGame = gameValidator.validateGame(game, newPlayer, unfinishedGames);
        if (invalidGame != null) return invalidGame;

        assert game != null; // The validator should have returned an error if the game is null

        // Update the game status and save it to the database
        game.setGameStatus(GameStatus.STARTED);
        game.setPlayerTwo(newPlayer);
        Game updatedGame = gameRepository.save(game);
        moveService.createGameMove(GameMapper.mapGameToMove(updatedGame));

        return new ResponseEntity<>(GameMapper.mapToGameDTO(updatedGame), HttpStatus.OK);
    }

    /**
     * This method finds a game for a user that wants to play. If a game is found, and the status of the game is
     * created or started or playing, then the user automatically participates in the game on the server.
     *
     * @param player - The name of the player that wants to play
     * @return - The Game object
     */
    public ResponseEntity<?> getGameStatus(String player) {
        // Check if the player has unfinished games
        List<Game> unfinishedGames = gameRepository
                .findAllUnfinishedPlayerGames(player).stream()
                .filter(g -> g.getGameStatus() != GameStatus.USER_WINS)
                .toList();

        // Get the latest game for the player
        Game lastGame = unfinishedGames.isEmpty()
                ? gameRepository.findAllGamesSortedById().stream()
                .filter(g -> g.getGameStatus() == GameStatus.USER_WINS)
                .findFirst().orElse(null)
                : unfinishedGames.get(0);

        // Return the game if it exists
        return unfinishedGames.isEmpty() && lastGame == null
                ? new ResponseEntity<>("{}", HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(GameMapper.mapToGameDTO(lastGame), HttpStatus.OK);
    }

    /**
     * This method allows the player to throw darts
     *
     * @param currentPlayer - The name of the player that throws the darts
     * @param dartThrows    - The throw of the player
     * @return - The Game object
     */
    public ResponseEntity<?> setThrows(String currentPlayer, Throws dartThrows) {
        // Find the current game for the player
        Game currentGame = gameRepository.findAllUnfinishedPlayerGames(currentPlayer).stream()
                .filter(g -> g.getGameStatus() != GameStatus.USER_WINS)
                .findFirst().orElse(null);

        // Validate the throw
        var invalidThrows = gameValidator.validateThrows(dartThrows);
        if (invalidThrows != null) return invalidThrows;

        // Validate the game
        var invalidOngoingGame = gameValidator.validateOngoingGame(currentPlayer, currentGame);
        if (invalidOngoingGame != null) return invalidOngoingGame;

        // Update the game with the throws if they are valid
        try {
            assert currentGame != null; // Null check is done in the validator above
            Game updatedPointsGame = gameProgressService.updatePoints(currentGame, currentPlayer, dartThrows);
            Game updatedGame = gameRepository.save(updatedPointsGame);
            moveService.createGameMove(GameMapper.mapGameToMove(updatedGame));
            return new ResponseEntity<>(GameMapper.mapToGameDTO(updatedGame), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Result("Wrong throws!"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method returns the entire history of a specific game.
     *
     * @param gameId - The id of the game
     * @return - The history as a list of moves
     */
    public ResponseEntity<?> getGameHistory(String gameId) {
        var invalidGameId = gameValidator.validateGameId(gameId);
        if (invalidGameId != null) return invalidGameId;

        List<Move> history = moveService.getGameHistory(Long.parseLong(gameId));
        var invalidHistory = gameValidator.validateGameHistory(history);
        if (invalidHistory != null) return invalidHistory;

        List<MoveDTO> gameHistory = history.stream().map(GameMapper::mapToMoveDTO).toList();
        return ResponseEntity.ok(gameHistory);
    }

    /**
     * This method allows the referee to cancel a game.
     *
     * @param gameUpdate - The game update object containing the game id and the status/reason
     * @return - The game mapped to a GameDTO
     */
    public ResponseEntity<?> cancelGame(Update gameUpdate) {
        long id = gameUpdate.gameId();
        Game game = gameRepository.findById(id).orElse(null);

        var invalidUpdate = gameValidator.validateGameUpdate(gameUpdate, game);
        if (invalidUpdate != null) return invalidUpdate;

        assert game != null; // The validator should have returned an error if the game is null

        game.setGameStatus("Nobody wins!".equals(gameUpdate.status()) ? GameStatus.NOBODY_WINS : GameStatus.USER_WINS);
        game = gameRepository.save(game);
        return ResponseEntity.ok(GameMapper.mapToGameDTO(game));
    }

    /**
     * This method allows a referee to revert the game to a previous state in the game history.
     *
     * @param gameRevert - The game revert object containing the game id and the move id
     * @return - The game mapped to a GameDTO
     */
    public ResponseEntity<?> revertGame(Revert gameRevert) {
        long id = gameRevert.gameId();
        Game oldGame = gameRepository.findById(id).orElse(null);
        Move move = moveService.getGameMove(gameRevert.gameId(), gameRevert.move());
        Move lastMove = moveService.getLastGameMove(gameRevert.gameId());

        var invalidRevert = gameValidator.validateGameRevert(oldGame, move, lastMove);
        if (invalidRevert != null) return invalidRevert;

        Game game = GameMapper.mapMoveToGame(move);
        gameRepository.save(game);
        moveService.deleteMovesAfter(move);
        return ResponseEntity.ok(GameMapper.mapToGameDTO(game));
    }
}

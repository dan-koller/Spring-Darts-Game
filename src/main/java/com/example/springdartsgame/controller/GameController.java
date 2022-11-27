package com.example.springdartsgame.controller;

import com.example.springdartsgame.model.Score;
import com.example.springdartsgame.model.Throws;
import com.example.springdartsgame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    private GameService gameService;

    /**
     * Users can create a game, view the current game status, view games and join a game. There are the following
     * restrictions:
     * - Users should not be able to create a new game if they have an unfinished game on the server.
     * - Users can join the game that has the 'created' status; once to players join the game, it should start.
     * - Two different users must join a game to start. Users can only participate in only one game at a time.
     *
     * @param authentication - The authentication object from the Spring Security
     * @param targetScore    - The target score for the game
     * @return - the ResponseEntity object (in this case the Game pojo as JSON)
     */
    @PostMapping("/game/create")
    public ResponseEntity<?> createGame(Authentication authentication, @RequestBody Score targetScore) {
        return gameService.createGame(authentication.getName(), targetScore.targetScore());
    }

    /**
     * This method returns s list of all the games in the database. Authentication is required but handled by the
     * ResourceServerConfiguration using antMatchers.
     *
     * @return - The ResponseEntity object (in this case the list of games as JSON)
     */
    @GetMapping("/game/list")
    public ResponseEntity<?> listGames() {
        return gameService.listAllGames();
    }

    /**
     * This method allows players to join a game.
     *
     * @param authentication - The authentication object from the Spring Security
     * @param gameId         - The id of the game to join
     * @return - The ResponseEntity object (in this case the Game pojo as JSON)
     */
    @GetMapping("/game/join/{gameId}")
    public ResponseEntity<?> joinGame(Authentication authentication, @PathVariable long gameId) {
        return gameService.joinGame(authentication.getName(), gameId);
    }

    /**
     * If a game is found for a user that wants to play, and the status of the game is created or started or playing,
     * then the user automatically participates in the game on the server.
     * In this case, the endpoint must respond with the HTTP OK status 200 and the following JSON representing
     * the current state of the game.
     *
     * @param authentication - The authentication object from the Spring Security
     * @return - The ResponseEntity object (in this case the Game pojo as JSON)
     */
    @GetMapping("/game/status")
    public ResponseEntity<?> getGameStatus(Authentication authentication) {
        return gameService.getGameStatus(authentication.getName());
    }

    /**
     * This method allows players to throw darts.
     *
     * @param authentication - The authentication object from the Spring Security
     * @return - The ResponseEntity object (in this case the Game pojo as JSON)
     */
    @PostMapping("/game/throws")
    public ResponseEntity<?> setThrows(Authentication authentication, @RequestBody Throws throwsInfo) {
        return gameService.setThrows(authentication.getName(), throwsInfo);
    }

    /**
     * This method returns the history for a game with the given id.
     *
     * @param gameId - The id of the game
     * @return - The ResponseEntity object (in this case the Game pojo as JSON)
     */
    @RequestMapping("/history/{gameId}")
    public ResponseEntity<?> getGameHistory(@PathVariable String gameId) {
        return gameService.getGameHistory(gameId);

    }
}

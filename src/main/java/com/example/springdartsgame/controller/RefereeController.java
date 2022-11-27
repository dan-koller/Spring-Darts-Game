package com.example.springdartsgame.controller;

import com.example.springdartsgame.model.Revert;
import com.example.springdartsgame.model.Update;
import com.example.springdartsgame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class RefereeController {

    @Autowired
    private GameService gameService;

    /**
     * This method allows a referee to cancel a specific game.
     *
     * @param gameUpdate - The game update object including the status/reason
     * @return - The ResponseEntity object
     */
    @PutMapping("/cancel")
    public ResponseEntity<?> cancelGame(@RequestBody Update gameUpdate) {
        return gameService.cancelGame(gameUpdate);
    }

    /**
     * This method allows a referee to revert a specific game to a previous state in the game history.
     *
     * @param gameRevert - The game revert object
     * @return - The ResponseEntity object
     */
    @PutMapping("/revert")
    private ResponseEntity<?> revertGameToMove(@RequestBody Revert gameRevert) {
        return gameService.revertGame(gameRevert);
    }
}

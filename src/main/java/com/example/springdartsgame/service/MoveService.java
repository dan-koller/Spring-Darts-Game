package com.example.springdartsgame.service;

import com.example.springdartsgame.entity.Move;
import com.example.springdartsgame.persistence.MoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This method especially handles the database operations for the move entity.
 */
@Service
public class MoveService {

    @Autowired
    private MoveRepository moveRepository;

    /**
     * This method finds all moves for a specific game and returns them as a list.
     *
     * @param gameId - The id of the game
     * @return - The list of moves
     */
    public List<Move> getGameHistory(long gameId) {
        return moveRepository.findAllByGameId(gameId);
    }

    /**
     * This method finds a game by its id and current move. It is used to retrieve a game state in the game history.
     *
     * @param gameId - The id of the game
     * @param move   - The current move
     * @return - The move object
     */
    public Move getGameMove(long gameId, int move) {
        return moveRepository.findByGameIdAndMove(gameId, move);
    }

    /**
     * This method find the last move of a game by its id.
     *
     * @param gameId - The id of the game
     * @return - The move object
     */
    public Move getLastGameMove(long gameId) {
        return moveRepository.findTopByGameIdOrderByMoveDesc(gameId);
    }

    /**
     * This method validates the integrity of a game move and saves it to the database.
     *
     * @param move - The move object
     */
    public void createGameMove(Move move) {
        Move lastMove = getLastGameMove(move.getGameId());
        move.setMove(lastMove == null ? 0 : lastMove.getMove() + 1);
        moveRepository.save(move);
    }

    /**
     * This method deletes all moves of a game by its id after a certain move. This has to be transactional to avoid
     * concurrency issues when reverting a game.
     *
     * @param move - The move object
     */
    @Transactional
    public void deleteMovesAfter(Move move) {
        moveRepository.deleteAllByGameIdAndMoveGreaterThan(move.getGameId(), move.getMove());
    }
}

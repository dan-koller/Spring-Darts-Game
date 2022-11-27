package com.example.springdartsgame.persistence;

import com.example.springdartsgame.entity.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoveRepository extends JpaRepository<Move, Long> {
    List<Move> findAllByGameId(long gameId);
    Move findByGameIdAndMove(long gameId, int move);
    Move findTopByGameIdOrderByMoveDesc(long gameId);
    void deleteAllByGameIdAndMoveGreaterThan(long gameId, int move);
}

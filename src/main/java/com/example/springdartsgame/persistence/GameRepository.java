package com.example.springdartsgame.persistence;

import com.example.springdartsgame.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("select g from Game g order by g.id DESC")
    List<Game> findAllGamesSortedById();
    @Query("select g from Game g where g.playerOne = ?1 or g.playerTwo = ?1 order by g.id DESC")
    List<Game> findAllUnfinishedPlayerGames(String player);
}

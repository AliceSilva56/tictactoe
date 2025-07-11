package com.tictactoe.game.repositories;

import com.tictactoe.game.entities.Game;
import com.tictactoe.game.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByPlayerXOrPlayerOOrderByCreatedAtDesc(User playerX, User playerO);

    @Query("SELECT g FROM Game g LEFT JOIN FETCH g.playerX LEFT JOIN FETCH g.playerO WHERE g.id = :id")
    Optional<Game> findByIdWithPlayers(@Param("id") Long id);
}
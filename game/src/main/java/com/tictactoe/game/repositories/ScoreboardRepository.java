package com.tictactoe.game.repositories;

import com.tictactoe.game.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreboardRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u ORDER BY u.score DESC")
    List<User> findAllOrderByScoreDesc();

    @Query("SELECT u FROM User u ORDER BY u.victories DESC")
    List<User> findAllOrderByVictoriesDesc();

    @Query("SELECT u FROM User u WHERE u.username LIKE %:username% ORDER BY u.score DESC")
    List<User> findByUsernameContainingOrderByScoreDesc(String username);
}

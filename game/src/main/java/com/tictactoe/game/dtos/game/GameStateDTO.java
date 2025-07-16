package com.tictactoe.game.dtos.game;

public record GameStateDTO(
        String[] board,
        String currentPlayer,
        String winner,
        boolean gameOver,
        String playerX,
        String playerO
) {}


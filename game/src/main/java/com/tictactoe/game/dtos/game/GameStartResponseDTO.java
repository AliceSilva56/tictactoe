package com.tictactoe.game.dtos.game;

public record GameStartResponseDTO(
        Long gameId,
        String playerX,
        String playerO,
        String message
) {}

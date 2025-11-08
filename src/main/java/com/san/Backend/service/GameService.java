package com.san.Backend.service;

import com.san.Backend.dto.BallRequestDTO;
import com.san.Backend.dto.GameSetupDTO;
import com.san.Backend.dto.GameStateDTO;
import com.san.Backend.dto.PlayerUpdateDTO;

public interface GameService {

    /**
     * Creates a new game and sets up the first innings.
     * @param setupDTO DTO from the React multi-step form.
     * @return The initial GameStateDTO.
     */
    GameStateDTO createNewGame(GameSetupDTO setupDTO);

    /**
     * Retrieves the current state of a game.
     * @param matchId The ID of the game to fetch.
     * @return The current GameStateDTO.
     */
    GameStateDTO getGameState(Long matchId);

    /**
     * The main game engine. Processes a single ball event.
     * @param matchId The ID of the game.
     *_@param ballRequest DTO containing what the user clicked (e.g., "RUNS", "WICKET").
     * @return The *new* GameStateDTO after the ball is processed.
     */
    GameStateDTO processBall(Long matchId, BallRequestDTO ballRequest);

    /**
     * Updates a player's name (e.g., "Batsman 1" -> "Virat").
     * @param matchId The ID of the game.
     * @param playerUpdate DTO containing the player status (e.g., "ON_STRIKE") and new name.
     * @return The updated GameStateDTO.
     */
    GameStateDTO updatePlayerName(Long matchId, PlayerUpdateDTO playerUpdate);

    /**
     * Manually ends the current innings.
     * @param matchId The ID of the game.
     * @return The new GameStateDTO (e.g., starting innings 2).
     */
    GameStateDTO endInnings(Long matchId);
}
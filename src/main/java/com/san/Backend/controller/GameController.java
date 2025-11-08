package com.san.Backend.controller;

import com.san.Backend.dto.BallRequestDTO;
import com.san.Backend.dto.GameSetupDTO;
import com.san.Backend.dto.GameStateDTO;
import com.san.Backend.dto.PlayerUpdateDTO;
import com.san.Backend.service.GameService; // Import the service
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    // 1. Inject the GameService
    @Autowired
    private GameService gameService;

    // 2. Uncomment all the method bodies

    @PostMapping("/start")
    public ResponseEntity<GameStateDTO> startGame(@RequestBody GameSetupDTO setupDTO) {
        GameStateDTO gameState = gameService.createNewGame(setupDTO);
        return ResponseEntity.ok(gameState);
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<GameStateDTO> getGameState(@PathVariable Long matchId) {
        GameStateDTO gameState = gameService.getGameState(matchId);
        return ResponseEntity.ok(gameState);
    }

    @PostMapping("/{matchId}/ball")
    public ResponseEntity<GameStateDTO> processBall(@PathVariable Long matchId, @RequestBody BallRequestDTO ballRequest) {
        GameStateDTO gameState = gameService.processBall(matchId, ballRequest);
        return ResponseEntity.ok(gameState);
    }

    @PutMapping("/{matchId}/player")
    public ResponseEntity<GameStateDTO> updatePlayer(@PathVariable Long matchId, @RequestBody PlayerUpdateDTO playerUpdate) {
        GameStateDTO gameState = gameService.updatePlayerName(matchId, playerUpdate);
        return ResponseEntity.ok(gameState);
    }

    @PostMapping("/{matchId}/end-innings")
    public ResponseEntity<GameStateDTO> endInnings(@PathVariable Long matchId) {
        GameStateDTO gameState = gameService.endInnings(matchId);
        return ResponseEntity.ok(gameState);
    }
}
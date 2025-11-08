package com.san.Backend.service;

import com.san.Backend.dto.*;
import com.san.Backend.model.*;
import com.san.Backend.repository.BallEventRepository;
import com.san.Backend.repository.GameMatchRepository;
import com.san.Backend.repository.PlayerStatsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameServiceImpl implements GameService {

    @Autowired
    private GameMatchRepository gameMatchRepository;

    @Autowired
    private PlayerStatsRepository playerStatsRepository;

    @Autowired
    private BallEventRepository ballEventRepository;

    // Tracks runs in the current over for maiden calculation
    private int currentOverRuns = 0;

    @Override
    public GameStateDTO createNewGame(GameSetupDTO setupDTO) {
        GameMatch game = new GameMatch();
        game.setTeam1Name(setupDTO.getTeam1Name());
        game.setTeam2Name(setupDTO.getTeam2Name());
        game.setMaxOvers(setupDTO.getOvers());
        game.setBattingTeam(setupDTO.getBattingTeam());
        
        String bowlingTeam = setupDTO.getTeam1Name().equals(setupDTO.getBattingTeam()) ? 
                             setupDTO.getTeam2Name() : setupDTO.getTeam1Name();
        game.setBowlingTeam(bowlingTeam);

        game.setStatus(GameStatus.INNINGS_1);
        game.setTarget(0);
        game.setCurrentScore(0);
        game.setCurrentWickets(0);
        game.setCurrentBalls(0);
        game.setFreeHit(false);
        game.setExtrasWides(0);
        game.setExtrasNoballs(0);
        
        // Initialize Innings 1 stats to 0
        game.setInnings1Score(0);
        game.setInnings1Wickets(0);
        game.setInnings1Balls(0);
        game.setInnings1ExtrasWides(0);
        game.setInnings1ExtrasNoballs(0);


        // Create initial batsmen
        PlayerStats batsman1 = new PlayerStats();
        batsman1.setGameMatch(game);
        batsman1.setName("Batsman 1"); // Default placeholder name
        batsman1.setTeamName(game.getBattingTeam());
        batsman1.setStatus(PlayerStatus.ON_STRIKE);

        PlayerStats batsman2 = new PlayerStats();
        batsman2.setGameMatch(game);
        batsman2.setName("Batsman 2"); // Default placeholder name
        batsman2.setTeamName(game.getBattingTeam());
        batsman2.setStatus(PlayerStatus.NON_STRIKE);

        game.getPlayerStats().add(batsman1);
        game.getPlayerStats().add(batsman2);

        // Save the game. Batsmen are saved due to Cascade.ALL
        GameMatch savedGame = gameMatchRepository.save(game);
        
        return mapToGameStateDTO(savedGame);
    }

    @Override
    public GameStateDTO getGameState(Long matchId) {
        GameMatch game = findMatchById(matchId);
        return mapToGameStateDTO(game);
    }

    @Override
    public GameStateDTO updatePlayerName(Long matchId, PlayerUpdateDTO playerUpdate) {
        GameMatch game = findMatchById(matchId);
        PlayerStatus status = PlayerStatus.valueOf(playerUpdate.getPlayerStatus());

        // This endpoint is now ONLY for batsmen. Bowler updates are handled by processBall.
        if (status == PlayerStatus.ON_STRIKE || status == PlayerStatus.NON_STRIKE) {
            PlayerStats player = playerStatsRepository.findByGameMatchAndStatus(game, status)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with status: " + status));
            
            player.setName(playerUpdate.getNewName());
            playerStatsRepository.save(player);
        }

        return mapToGameStateDTO(game);
    }

    @Override
    public GameStateDTO endInnings(Long matchId) {
        GameMatch game = findMatchById(matchId);
        // We only run this logic if the game is not already completed
        if (game.getStatus() != GameStatus.COMPLETED) {
             runInningsEndLogic(game);
             gameMatchRepository.save(game);
        }
        return mapToGameStateDTO(game);
    }

    @Override
    @Transactional
    public GameStateDTO processBall(Long matchId, BallRequestDTO ballRequest) {
        GameMatch game = findMatchById(matchId);
        if (game.getStatus() == GameStatus.COMPLETED) {
            return mapToGameStateDTO(game); // Game is over
        }

        // --- START: Bowler Validation and Setting Logic ---
            
        String bowlerNameFromRequest = ballRequest.getBowlerName();
        if (bowlerNameFromRequest == null || bowlerNameFromRequest.trim().isEmpty()) {
            throw new EntityNotFoundException("Current bowler not set. Please set a bowler.");
        }

        PlayerStats currentBowlerInDB = playerStatsRepository.findByGameMatchAndStatus(game, PlayerStatus.CURRENT_BOWLER)
            .orElse(null);

        PlayerStats bowler; // This will be the bowler for this ball
        
        if (currentBowlerInDB == null || !currentBowlerInDB.getName().equals(bowlerNameFromRequest)) {
            if (currentBowlerInDB != null) {
                currentBowlerInDB.setStatus(PlayerStatus.BENCH);
                playerStatsRepository.save(currentBowlerInDB);
            }

            String bowlingTeam = game.getBowlingTeam();
            bowler = game.getPlayerStats().stream()
                .filter(p -> p.getTeamName().equals(bowlingTeam) && p.getName().equals(bowlerNameFromRequest))
                .findFirst()
                .orElseGet(() -> {
                    PlayerStats newBowler = new PlayerStats();
                    newBowler.setGameMatch(game);
                    newBowler.setName(bowlerNameFromRequest);
                    newBowler.setTeamName(bowlingTeam);
                    newBowler.setStatus(PlayerStatus.BENCH); 
                    game.getPlayerStats().add(newBowler); 
                    return newBowler;
                });
            
            bowler.setStatus(PlayerStatus.CURRENT_BOWLER);
            bowler = playerStatsRepository.save(bowler); 
            
        } else {
            bowler = currentBowlerInDB;
        }
        
        // --- END: Bowler Validation and Setting Logic ---


        PlayerStats striker = playerStatsRepository.findByGameMatchAndStatus(game, PlayerStatus.ON_STRIKE)
            .orElseThrow(() -> new EntityNotFoundException("On-strike batsman not found"));
        PlayerStats nonStriker = playerStatsRepository.findByGameMatchAndStatus(game, PlayerStatus.NON_STRIKE)
            .orElseThrow(() -> new EntityNotFoundException("Non-strike batsman not found"));

        boolean isLegalBall = true;
        BallEvent ballEvent = new BallEvent();
        ballEvent.setGameMatch(game);
        
        int currentInningsNum = (game.getStatus() == GameStatus.INNINGS_1) ? 1 : 2;
        int overNum = (game.getCurrentBalls() / 6) + 1;
        ballEvent.setInningsNumber(currentInningsNum);
        ballEvent.setOverNumber(overNum);
        ballEvent.setBowlerName(bowler.getName());

        switch (ballRequest.getType()) {
            case "RUNS":
                int runs = ballRequest.getRuns();
                game.setCurrentScore(game.getCurrentScore() + runs);
                striker.setRunsScored(striker.getRunsScored() + runs);
                striker.setBallsFaced(striker.getBallsFaced() + 1);
                if (runs == 4) striker.setFours(striker.getFours() + 1);
                if (runs == 6) striker.setSixes(striker.getSixes() + 1);
                
                bowler.setRunsConceded(bowler.getRunsConceded() + runs);
                currentOverRuns += runs;
                
                ballEvent.setEvent(String.valueOf(runs));
                
                if (runs == 1 || runs == 3) {
                    swapStrike(striker, nonStriker);
                }
                if (game.isFreeHit()) game.setFreeHit(false);
                break;

            case "WIDE":
                isLegalBall = false;
                game.setCurrentScore(game.getCurrentScore() + 1);
                game.setExtrasWides(game.getExtrasWides() + 1);
                bowler.setRunsConceded(bowler.getRunsConceded() + 1);
                bowler.setWides(bowler.getWides() + 1);
                currentOverRuns += 1;
                ballEvent.setEvent("wd");
                break;

            case "NO_BALL":
                isLegalBall = false;
                game.setCurrentScore(game.getCurrentScore() + 1);
                game.setExtrasNoballs(game.getExtrasNoballs() + 1);
                bowler.setRunsConceded(bowler.getRunsConceded() + 1);
                bowler.setNoballs(bowler.getNoballs() + 1);
                currentOverRuns += 1;
                game.setFreeHit(true);
                ballEvent.setEvent("nb");
                break;

            case "WICKET":
                isLegalBall = true;
                
                if (game.isFreeHit()) {
                    if ("RUN_OUT".equals(ballRequest.getDismissalType())) {
                        game.setCurrentWickets(game.getCurrentWickets() + 1);
                        striker.setBallsFaced(striker.getBallsFaced() + 1);
                        striker.setStatus(PlayerStatus.BENCH);
                        ballEvent.setEvent("W"); 

                        if (game.getCurrentWickets() < 10) {
                            PlayerStats newBatsman = new PlayerStats();
                            newBatsman.setGameMatch(game);
                            newBatsman.setName("Batsman " + (game.getCurrentWickets() + 2));
                            newBatsman.setTeamName(game.getBattingTeam());
                            newBatsman.setStatus(PlayerStatus.ON_STRIKE);
                            game.getPlayerStats().add(newBatsman);
                        }
                    } else {
                        striker.setBallsFaced(striker.getBallsFaced() + 1);
                        ballEvent.setEvent("0"); 
                    }
                    game.setFreeHit(false); 
                
                } else {
                    game.setCurrentWickets(game.getCurrentWickets() + 1);
                    striker.setBallsFaced(striker.getBallsFaced() + 1);
                    striker.setStatus(PlayerStatus.BENCH);
                    
                    if (!"RUN_OUT".equals(ballRequest.getDismissalType())) {
                        bowler.setWicketsTaken(bowler.getWicketsTaken() + 1);
                    }
                    ballEvent.setEvent("W");

                    if (game.getCurrentWickets() < 10) {
                        PlayerStats newBatsman = new PlayerStats();
                        newBatsman.setGameMatch(game);
                        newBatsman.setName("Batsman " + (game.getCurrentWickets() + 2));
                        newBatsman.setTeamName(game.getBattingTeam());
                        newBatsman.setStatus(PlayerStatus.ON_STRIKE);
                        game.getPlayerStats().add(newBatsman);
                    }
                }
                break;
        }

        game.getBallHistory().add(ballEvent);

        if (isLegalBall) {
            game.setCurrentBalls(game.getCurrentBalls() + 1);
            bowler.setBallsBowled(bowler.getBallsBowled() + 1);
        }

        // --- Check for End of Over ---
        if (isLegalBall && game.getCurrentBalls() % 6 == 0 && game.getCurrentBalls() > 0) {
            if (currentOverRuns == 0) {
                bowler.setMaidens(bowler.getMaidens() + 1);
            }
            
            // --- (FIX for NonUniqueResultException) ---
            // We must re-fetch the current striker and non-striker
            // in case they were changed by the wicket logic above.
            PlayerStats currentStriker = playerStatsRepository.findByGameMatchAndStatus(game, PlayerStatus.ON_STRIKE)
                .orElse(null); // Can be null if 10th wicket fell
            PlayerStats currentNonStriker = playerStatsRepository.findByGameMatchAndStatus(game, PlayerStatus.NON_STRIKE)
                .orElse(null); // Can be null if 10th wicket fell
                
            if (currentStriker != null && currentNonStriker != null) {
                swapStrike(currentStriker, currentNonStriker);
            }
            // --- (END OF FIX) ---

            bowler.setStatus(PlayerStatus.BENCH); 
            currentOverRuns = 0; 
        }

        // --- Check for End of Innings ---
        boolean inningsOver = false;
        if (game.getCurrentWickets() == 10) {
            inningsOver = true;
        }
        if (isLegalBall && game.getCurrentBalls() == game.getMaxOvers() * 6) {
            inningsOver = true;
        }
        if (game.getStatus() == GameStatus.INNINGS_2 && game.getCurrentScore() >= game.getTarget()) {
            inningsOver = true; 
        }

        if (inningsOver) {
            runInningsEndLogic(game);
        }

        gameMatchRepository.save(game);
        return mapToGameStateDTO(game);
    }


    // --- PRIVATE HELPER METHODS ---

    private GameMatch findMatchById(Long matchId) {
        return gameMatchRepository.findById(matchId)
            .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + matchId));
    }

    private void runInningsEndLogic(GameMatch game) {
        if (game.getStatus() == GameStatus.INNINGS_1) {
            
            game.setInnings1Score(game.getCurrentScore());
            game.setInnings1Wickets(game.getCurrentWickets());
            game.setInnings1Balls(game.getCurrentBalls());
            game.setInnings1ExtrasWides(game.getExtrasWides());
            game.setInnings1ExtrasNoballs(game.getExtrasNoballs());

            game.setStatus(GameStatus.INNINGS_2);
            game.setTarget(game.getCurrentScore() + 1);
            
            String oldBattingTeam = game.getBattingTeam();
            game.setBattingTeam(game.getBowlingTeam());
            game.setBowlingTeam(oldBattingTeam);

            game.setCurrentScore(0);
            game.setCurrentWickets(0);
            game.setCurrentBalls(0);
            game.setFreeHit(false);
            game.setExtrasWides(0);
            game.setExtrasNoballs(0);
            this.currentOverRuns = 0;

            game.getPlayerStats().forEach(p -> p.setStatus(PlayerStatus.BENCH));

            PlayerStats batsman1 = new PlayerStats();
            batsman1.setGameMatch(game);
            batsman1.setName("Batsman 1");
            batsman1.setTeamName(game.getBattingTeam());
            batsman1.setStatus(PlayerStatus.ON_STRIKE);

            PlayerStats batsman2 = new PlayerStats();
            batsman2.setGameMatch(game);
            batsman2.setName("Batsman 2");
            batsman2.setTeamName(game.getBattingTeam());
            batsman2.setStatus(PlayerStatus.NON_STRIKE);

            game.getPlayerStats().add(batsman1);
            game.getPlayerStats().add(batsman2);
            
        } else if (game.getStatus() == GameStatus.INNINGS_2) {
            game.setStatus(GameStatus.COMPLETED);
            
            String winningTeam;
            String resultString;
            
            if (game.getCurrentScore() >= game.getTarget()) {
                winningTeam = game.getBattingTeam();
                int wicketsLeft = 10 - game.getCurrentWickets();
                resultString = winningTeam + " won by " + wicketsLeft + " " + (wicketsLeft == 1 ? "wicket" : "wickets");
            } else if (game.getCurrentScore() == game.getTarget() - 1) {
                resultString = "Match is a Tie";
            } else {
                winningTeam = game.getBowlingTeam();
                int runsWonBy = (game.getTarget() - 1) - game.getCurrentScore();
                resultString = winningTeam + " won by " + runsWonBy + " " + (runsWonBy == 1 ? "run" : "runs");
            }
            
            game.setMatchResult(resultString);
        }
    }

    private void swapStrike(PlayerStats striker, PlayerStats nonStriker) {
        striker.setStatus(PlayerStatus.NON_STRIKE);
        nonStriker.setStatus(PlayerStatus.ON_STRIKE);
    }

    private GameStateDTO mapToGameStateDTO(GameMatch game) {
        GameStateDTO dto = new GameStateDTO();
        dto.setMatchId(game.getId());
        dto.setStatus(game.getStatus().name());
        dto.setBattingTeam(game.getBattingTeam());
        dto.setBowlingTeam(game.getBowlingTeam());
        dto.setScore(game.getCurrentScore());
        dto.setWickets(game.getCurrentWickets());
        dto.setOvers(formatOvers(game.getCurrentBalls()));
        dto.setTarget(game.getTarget());
        dto.setFreeHit(game.isFreeHit());
        dto.setCrr(calculateCrr(game.getCurrentScore(), game.getCurrentBalls()));
        dto.setMatchResult(game.getMatchResult()); 

        ExtrasDTO extras = new ExtrasDTO();
        extras.setWides(game.getExtrasWides());
        extras.setNoballs(game.getExtrasNoballs());
        extras.setTotal(game.getExtrasWides() + game.getExtrasNoballs());
        dto.setExtras(extras);

        game.getPlayerStats().stream()
            .filter(p -> p.getStatus() == PlayerStatus.ON_STRIKE)
            .findFirst()
            .ifPresent(p -> dto.setOnStrikeBatsman(mapToPlayerDTO(p)));

        game.getPlayerStats().stream()
            .filter(p -> p.getStatus() == PlayerStatus.NON_STRIKE)
            .findFirst()
            .ifPresent(p -> dto.setNonStrikeBatsman(mapToPlayerDTO(p)));
            
        game.getPlayerStats().stream()
            .filter(p -> p.getStatus() == PlayerStatus.CURRENT_BOWLER)
            .findFirst()
            .ifPresent(p -> dto.setCurrentBowler(mapToPlayerDTO(p)));
        
        List<PlayerStatsDTO> currentBattingCard = game.getPlayerStats().stream()
            .filter(p -> p.getTeamName().equals(game.getBattingTeam()) && (p.getBallsFaced() > 0 || p.getStatus() == PlayerStatus.ON_STRIKE || p.getStatus() == PlayerStatus.NON_STRIKE))
            .map(this::mapToPlayerDTO)
            .collect(Collectors.toList());
        dto.setCurrentInningsBattingCard(currentBattingCard);
        
        List<PlayerStatsDTO> currentBowlingCard = game.getPlayerStats().stream()
            .filter(p -> p.getTeamName().equals(game.getBowlingTeam()) && p.getBallsBowled() > 0)
            .map(this::mapToPlayerDTO)
            .collect(Collectors.toList());
        dto.setCurrentInningsBowlingCard(currentBowlingCard);

        int currentInningsNum = (game.getStatus() == GameStatus.INNINGS_1) ? 1 : 2;
        List<BallEvent> currentInningsBalls = game.getBallHistory().stream()
            .filter(b -> b.getInningsNumber() == currentInningsNum)
            .toList();
        dto.setCurrentInningsOvers(mapBallEventsToOverDTOs(currentInningsBalls));
        
        if (game.getStatus() == GameStatus.INNINGS_2 || game.getStatus() == GameStatus.COMPLETED) {
            FirstInningsSummaryDTO summary = new FirstInningsSummaryDTO();
            
            String innings1BattingTeam = game.getBowlingTeam();
            String innings1BowlingTeam = game.getBattingTeam();

            summary.setBattingTeam(innings1BattingTeam);
            summary.setScore(game.getInnings1Score());
            summary.setWickets(game.getInnings1Wickets());
            summary.setOvers(formatOvers(game.getInnings1Balls()));

            ExtrasDTO innings1Extras = new ExtrasDTO();
            innings1Extras.setWides(game.getInnings1ExtrasWides());
            innings1Extras.setNoballs(game.getInnings1ExtrasNoballs());
            innings1Extras.setTotal(game.getInnings1ExtrasWides() + game.getInnings1ExtrasNoballs());
            summary.setExtras(innings1Extras);

            List<PlayerStatsDTO> innings1BattingCard = game.getPlayerStats().stream()
                .filter(p -> p.getTeamName().equals(innings1BattingTeam) && p.getBallsFaced() > 0)
                .map(this::mapToPlayerDTO)
                .collect(Collectors.toList());
            summary.setBattingCard(innings1BattingCard);

            List<PlayerStatsDTO> innings1BowlingCard = game.getPlayerStats().stream()
                .filter(p -> p.getTeamName().equals(innings1BowlingTeam) && p.getBallsBowled() > 0)
                .map(this::mapToPlayerDTO)
                .collect(Collectors.toList());
            summary.setBowlingCard(innings1BowlingCard);

            dto.setFirstInningsSummary(summary);

            List<BallEvent> firstInningsBalls = game.getBallHistory().stream()
                .filter(b -> b.getInningsNumber() == 1)
                .toList();
            dto.setFirstInningsOvers(mapBallEventsToOverDTOs(firstInningsBalls));
        }
        
        return dto;
    }

    private List<OverDTO> mapBallEventsToOverDTOs(List<BallEvent> balls) {
        Map<Integer, List<BallEvent>> oversMap = balls.stream()
            .collect(Collectors.groupingBy(
                BallEvent::getOverNumber,
                LinkedHashMap::new, 
                Collectors.toList()
            ));

        return oversMap.entrySet().stream()
            .map(entry -> {
                OverDTO overDTO = new OverDTO();
                overDTO.setOverNumber(entry.getKey());
                
                if (entry.getValue().isEmpty()) {
                    return null; 
                }
                overDTO.setBowlerName(entry.getValue().get(0).getBowlerName());
                
                List<String> ballEvents = entry.getValue().stream()
                    .map(BallEvent::getEvent)
                    .collect(Collectors.toList());
                overDTO.setBalls(ballEvents);
                
                return overDTO;
            })
            .filter(java.util.Objects::nonNull) 
            .sorted(Comparator.comparingInt(OverDTO::getOverNumber)) 
            .collect(Collectors.toList());
    }

    private PlayerStatsDTO mapToPlayerDTO(PlayerStats p) {
        if (p == null) return null;
        PlayerStatsDTO dto = new PlayerStatsDTO();
        dto.setName(p.getName());
        // Batting
        dto.setRuns(p.getRunsScored());
        dto.setBalls(p.getBallsFaced());
        dto.setFours(p.getFours());
        dto.setSixes(p.getSixes());
        dto.setSr(calculateSR(p.getRunsScored(), p.getBallsFaced()));
        // Bowling
        dto.setOvers(formatOvers(p.getBallsBowled()));
        dto.setMaidens(p.getMaidens());
        dto.setRunsConceded(p.getRunsConceded());
        dto.setWickets(p.getWicketsTaken());
        dto.setEco(calculateECO(p.getRunsConceded(), p.getBallsBowled())); // Corrected typo: getBallsBowled()
        return dto;
    }

    // --- Calculation Helpers ---

    private String formatOvers(int totalBalls) {
        int overs = totalBalls / 6;
        int balls = totalBalls % 6;
        return overs + "." + balls;
    }

    private String calculateSR(int runs, int balls) {
        if (balls == 0) return "0.00";
        return String.format("%.2f", (double) runs * 100 / balls);
    }

    private String calculateECO(int runs, int balls) {
        if (balls == 0) return "0.00";
        double oversDecimal = (balls / 6) + ((balls % 6) / 6.0);
        return String.format("%.2f", (double) runs / oversDecimal);
    }

    private String calculateCrr(int score, int balls) {
        if (balls == 0) return "0.00";
        double oversDecimal = (balls / 6) + ((balls % 6) / 6.0);
        return String.format("%.2f", (double) score / oversDecimal);
    }
}
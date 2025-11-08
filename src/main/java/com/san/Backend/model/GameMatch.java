package com.san.Backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GameMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String team1Name;
    private String team2Name;
    private int maxOvers;
    private int target; // Target to chase (0 for 1st innings)

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    // --- Current Innings State ---
    private String battingTeam;
    private String bowlingTeam;
    private int currentScore;
    private int currentWickets;
    private int currentBalls; // Legal balls in this innings
    private boolean isFreeHit;
    
    private int extrasWides;
    private int extrasNoballs;

    // --- First Innings Final Stats ---
    @Column(columnDefinition = "integer default 0")
    private int innings1Score;
    
    @Column(columnDefinition = "integer default 0")
    private int innings1Wickets;
    
    @Column(columnDefinition = "integer default 0")
    private int innings1Balls;
    
    @Column(columnDefinition = "integer default 0")
    private int innings1ExtrasWides;
    
    @Column(columnDefinition = "integer default 0")
    private int innings1ExtrasNoballs;
    
    // --- Final Match Result ---
    private String matchResult;

    @OneToMany(mappedBy = "gameMatch", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PlayerStats> playerStats = new ArrayList<>();

    @OneToMany(mappedBy = "gameMatch", cascade = CascadeType.ALL)
    private List<BallEvent> ballHistory = new ArrayList<>();

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTeam1Name() { return team1Name; }
    public void setTeam1Name(String team1Name) { this.team1Name = team1Name; }

    public String getTeam2Name() { return team2Name; }
    public void setTeam2Name(String team2Name) { this.team2Name = team2Name; }

    public int getMaxOvers() { return maxOvers; }
    public void setMaxOvers(int maxOvers) { this.maxOvers = maxOvers; }

    public int getTarget() { return target; }
    public void setTarget(int target) { this.target = target; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }

    public String getBattingTeam() { return battingTeam; }
    public void setBattingTeam(String battingTeam) { this.battingTeam = battingTeam; }

    public String getBowlingTeam() { return bowlingTeam; }
    public void setBowlingTeam(String bowlingTeam) { this.bowlingTeam = bowlingTeam; }

    public int getCurrentScore() { return currentScore; }
    public void setCurrentScore(int currentScore) { this.currentScore = currentScore; }

    public int getCurrentWickets() { return currentWickets; }
    public void setCurrentWickets(int currentWickets) { this.currentWickets = currentWickets; }

    public int getCurrentBalls() { return currentBalls; }
    public void setCurrentBalls(int currentBalls) { this.currentBalls = currentBalls; }

    public boolean isFreeHit() { return isFreeHit; }
    public void setFreeHit(boolean freeHit) { this.isFreeHit = freeHit; }

    public int getExtrasWides() { return extrasWides; }
    public void setExtrasWides(int extrasWides) { this.extrasWides = extrasWides; }

    public int getExtrasNoballs() { return extrasNoballs; }
    public void setExtrasNoballs(int extrasNoballs) { this.extrasNoballs = extrasNoballs; }

    public List<PlayerStats> getPlayerStats() { return playerStats; }
    public void setPlayerStats(List<PlayerStats> playerStats) { this.playerStats = playerStats; }

    public List<BallEvent> getBallHistory() { return ballHistory; }
    public void setBallHistory(List<BallEvent> ballHistory) { this.ballHistory = ballHistory; }

    public int getInnings1Score() { return innings1Score; }
    public void setInnings1Score(int innings1Score) { this.innings1Score = innings1Score; }

    public int getInnings1Wickets() { return innings1Wickets; }
    public void setInnings1Wickets(int innings1Wickets) { this.innings1Wickets = innings1Wickets; }

    public int getInnings1Balls() { return innings1Balls; }
    public void setInnings1Balls(int innings1Balls) { this.innings1Balls = innings1Balls; }

    public int getInnings1ExtrasWides() { return innings1ExtrasWides; }
    public void setInnings1ExtrasWides(int innings1ExtrasWides) { this.innings1ExtrasWides = innings1ExtrasWides; }
    
    public int getInnings1ExtrasNoballs() { return innings1ExtrasNoballs; }
    public void setInnings1ExtrasNoballs(int innings1ExtrasNoballs) { this.innings1ExtrasNoballs = innings1ExtrasNoballs; }

    public String getMatchResult() { return matchResult; }
    public void setMatchResult(String matchResult) { this.matchResult = matchResult; }
}
package com.san.Backend.dto;

import java.util.List;

public class GameStateDTO {
    private Long matchId;
    private String status; // "INNINGS_1", "INNINGS_2", "COMPLETED"
    private String battingTeam;
    private String bowlingTeam;
    private int score;
    private int wickets;
    private String overs; // "0.3"
    private int target;
    private boolean isFreeHit;
    private String crr; // Current Run Rate
    private ExtrasDTO extras;
    private PlayerStatsDTO onStrikeBatsman;
    private PlayerStatsDTO nonStrikeBatsman;
    private PlayerStatsDTO currentBowler;
    
    // --- Scorecards & Overs ---
    private List<PlayerStatsDTO> currentInningsBattingCard;
    private List<PlayerStatsDTO> currentInningsBowlingCard;
    private List<OverDTO> currentInningsOvers;
    
    // --- Summary Fields ---
    private FirstInningsSummaryDTO firstInningsSummary;
    private List<OverDTO> firstInningsOvers;
    private String matchResult;

    // --- Getters and Setters ---

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBattingTeam() {
        return battingTeam;
    }

    public void setBattingTeam(String battingTeam) {
        this.battingTeam = battingTeam;
    }

    public String getBowlingTeam() {
        return bowlingTeam;
    }

    public void setBowlingTeam(String bowlingTeam) {
        this.bowlingTeam = bowlingTeam;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getWickets() {
        return wickets;
    }

    public void setWickets(int wickets) {
        this.wickets = wickets;
    }

    public String getOvers() {
        return overs;
    }

    public void setOvers(String overs) {
        this.overs = overs;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public boolean isFreeHit() {
        return isFreeHit;
    }

    public void setFreeHit(boolean freeHit) {
        this.isFreeHit = freeHit;
    }

    public String getCrr() {
        return crr;
    }

    public void setCrr(String crr) {
        this.crr = crr;
    }

    public ExtrasDTO getExtras() {
        return extras;
    }

    public void setExtras(ExtrasDTO extras) {
        this.extras = extras;
    }

    public PlayerStatsDTO getOnStrikeBatsman() {
        return onStrikeBatsman;
    }

    public void setOnStrikeBatsman(PlayerStatsDTO onStrikeBatsman) {
        this.onStrikeBatsman = onStrikeBatsman;
    }

    public PlayerStatsDTO getNonStrikeBatsman() {
        return nonStrikeBatsman;
    }

    public void setNonStrikeBatsman(PlayerStatsDTO nonStrikeBatsman) {
        this.nonStrikeBatsman = nonStrikeBatsman;
    }

    public PlayerStatsDTO getCurrentBowler() {
        return currentBowler;
    }

    public void setCurrentBowler(PlayerStatsDTO currentBowler) {
        this.currentBowler = currentBowler;
    }

    public List<PlayerStatsDTO> getCurrentInningsBattingCard() {
        return currentInningsBattingCard;
    }

    public void setCurrentInningsBattingCard(List<PlayerStatsDTO> currentInningsBattingCard) {
        this.currentInningsBattingCard = currentInningsBattingCard;
    }

    public List<PlayerStatsDTO> getCurrentInningsBowlingCard() {
        return currentInningsBowlingCard;
    }

    public void setCurrentInningsBowlingCard(List<PlayerStatsDTO> currentInningsBowlingCard) {
        this.currentInningsBowlingCard = currentInningsBowlingCard;
    }

    public FirstInningsSummaryDTO getFirstInningsSummary() {
        return firstInningsSummary;
    }

    public void setFirstInningsSummary(FirstInningsSummaryDTO firstInningsSummary) {
        this.firstInningsSummary = firstInningsSummary;
    }

    public List<OverDTO> getCurrentInningsOvers() {
        return currentInningsOvers;
    }

    public void setCurrentInningsOvers(List<OverDTO> currentInningsOvers) {
        this.currentInningsOvers = currentInningsOvers;
    }

    public List<OverDTO> getFirstInningsOvers() {
        return firstInningsOvers;
    }

    public void setFirstInningsOvers(List<OverDTO> firstInningsOvers) {
        this.firstInningsOvers = firstInningsOvers;
    }

    public String getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(String matchResult) {
        this.matchResult = matchResult;
    }
}
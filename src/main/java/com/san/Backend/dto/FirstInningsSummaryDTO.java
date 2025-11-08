package com.san.Backend.dto;

import java.util.List;

public class FirstInningsSummaryDTO {
    private String battingTeam;
    private int score;
    private int wickets;
    private String overs;
    private List<PlayerStatsDTO> battingCard;
    private List<PlayerStatsDTO> bowlingCard;
    
    // --- (NEW) ADD THIS FIELD ---
    private ExtrasDTO extras;

    // --- Getters and Setters ---
    
    public String getBattingTeam() { return battingTeam; }
    public void setBattingTeam(String battingTeam) { this.battingTeam = battingTeam; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }
    public String getOvers() { return overs; }
    public void setOvers(String overs) { this.overs = overs; }
    public List<PlayerStatsDTO> getBattingCard() { return battingCard; }
    public void setBattingCard(List<PlayerStatsDTO> battingCard) { this.battingCard = battingCard; }
    public List<PlayerStatsDTO> getBowlingCard() { return bowlingCard; }
    public void setBowlingCard(List<PlayerStatsDTO> bowlingCard) { this.bowlingCard = bowlingCard; }
    
    // --- (NEW) ADD GETTER/SETTER ---
    public ExtrasDTO getExtras() { return extras; }
    public void setExtras(ExtrasDTO extras) { this.extras = extras; }
}
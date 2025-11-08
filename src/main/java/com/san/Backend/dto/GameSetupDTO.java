package com.san.Backend.dto;

public class GameSetupDTO {
    private String team1Name;
    private String team2Name;
    private int overs;
    private String battingTeam;

    // Getters and Setters
    public String getTeam1Name() { return team1Name; }
    public void setTeam1Name(String team1Name) { this.team1Name = team1Name; }

    public String getTeam2Name() { return team2Name; }
    public void setTeam2Name(String team2Name) { this.team2Name = team2Name; }

    public int getOvers() { return overs; }
    public void setOvers(int overs) { this.overs = overs; }

    public String getBattingTeam() { return battingTeam; }
    public void setBattingTeam(String battingTeam) { this.battingTeam = battingTeam; }
}
package com.san.Backend.dto;

public class BallRequestDTO {
    private String type; // e.g., "RUNS", "WICKET", "WIDE", "NO_BALL"
    private int runs; // e.g., 0, 1, 2, 3, 4, 6
    private String dismissalType; // e.g., "CATCH", "BOLD", "STUMP"

    // --- ADD THIS ---
    private String bowlerName;

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getRuns() { return runs; }
    public void setRuns(int runs) { this.runs = runs; }

    public String getDismissalType() { return dismissalType; }
    public void setDismissalType(String dismissalType) { this.dismissalType = dismissalType; }

    // --- ADD THIS ---
    public String getBowlerName() { return bowlerName; }
    public void setBowlerName(String bowlerName) { this.bowlerName = bowlerName; }
}
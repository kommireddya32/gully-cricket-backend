package com.san.Backend.dto;

import java.util.List;

public class OverDTO {
    private int overNumber;
    private String bowlerName;
    private List<String> balls;

    // --- Getters and Setters ---
    
    public int getOverNumber() { return overNumber; }
    public void setOverNumber(int overNumber) { this.overNumber = overNumber; }

    public String getBowlerName() { return bowlerName; }
    public void setBowlerName(String bowlerName) { this.bowlerName = bowlerName; }

    public List<String> getBalls() { return balls; }
    public void setBalls(List<String> balls) { this.balls = balls; }
}
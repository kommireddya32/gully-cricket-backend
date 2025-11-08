package com.san.Backend.dto;

public class PlayerUpdateDTO {
    private String playerStatus; // "ON_STRIKE", "NON_STRIKE", "CURRENT_BOWLER"
    private String newName;

    // Getters and Setters
    public String getPlayerStatus() { return playerStatus; }
    public void setPlayerStatus(String playerStatus) { this.playerStatus = playerStatus; }

    public String getNewName() { return newName; }
    public void setNewName(String newName) { this.newName = newName; }
}
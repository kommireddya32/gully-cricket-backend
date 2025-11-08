package com.san.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*; // Make sure this is imported

@Entity
public class BallEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_match_id")
    @JsonIgnore
    private GameMatch gameMatch;

    private int inningsNumber;
    private String event; // e.g., "1", "4", "W", "wd", "nb"

    // --- UPDATED FIELDS (This is the fix) ---
    
    @Column(columnDefinition = "integer default 0")
    private int overNumber;

    @Column(columnDefinition = "varchar(255) default ''")
    private String bowlerName;

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GameMatch getGameMatch() { return gameMatch; }
    public void setGameMatch(GameMatch gameMatch) { this.gameMatch = gameMatch; }

    public int getInningsNumber() { return inningsNumber; }
    public void setInningsNumber(int inningsNumber) { this.inningsNumber = inningsNumber; }

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }

    // --- NEW GETTERS/SETTERS ---
    
    public int getOverNumber() { return overNumber; }
    public void setOverNumber(int overNumber) { this.overNumber = overNumber; }

    public String getBowlerName() { return bowlerName; }
    public void setBowlerName(String bowlerName) { this.bowlerName = bowlerName; }
}
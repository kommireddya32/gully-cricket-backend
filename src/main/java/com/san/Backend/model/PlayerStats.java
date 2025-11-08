package com.san.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class PlayerStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_match_id")
    @JsonIgnore // Prevents infinite loops when serializing to JSON
    private GameMatch gameMatch;

    private String name;
    private String teamName;
    
    @Enumerated(EnumType.STRING)
    private PlayerStatus status;

    // --- Batting Stats ---
    private int runsScored;
    private int ballsFaced;
    private int fours;
    private int sixes;

    // --- Bowling Stats ---
    private int ballsBowled;
    private int runsConceded;
    private int wicketsTaken;
    private int maidens;
    private int noballs;
    private int wides;

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GameMatch getGameMatch() { return gameMatch; }
    public void setGameMatch(GameMatch gameMatch) { this.gameMatch = gameMatch; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public PlayerStatus getStatus() { return status; }
    public void setStatus(PlayerStatus status) { this.status = status; }

    public int getRunsScored() { return runsScored; }
    public void setRunsScored(int runsScored) { this.runsScored = runsScored; }

    public int getBallsFaced() { return ballsFaced; }
    public void setBallsFaced(int ballsFaced) { this.ballsFaced = ballsFaced; }

    public int getFours() { return fours; }
    public void setFours(int fours) { this.fours = fours; }

    public int getSixes() { return sixes; }
    public void setSixes(int sixes) { this.sixes = sixes; }

    public int getBallsBowled() { return ballsBowled; }
    public void setBallsBowled(int ballsBowled) { this.ballsBowled = ballsBowled; }

    public int getRunsConceded() { return runsConceded; }
    public void setRunsConceded(int runsConceded) { this.runsConceded = runsConceded; }

    public int getWicketsTaken() { return wicketsTaken; }
    public void setWicketsTaken(int wicketsTaken) { this.wicketsTaken = wicketsTaken; }

    public int getMaidens() { return maidens; }
    public void setMaidens(int maidens) { this.maidens = maidens; }

    public int getNoballs() { return noballs; }
    public void setNoballs(int noballs) { this.noballs = noballs; }

    public int getWides() { return wides; }
    public void setWides(int wides) { this.wides = wides; }
}
package com.san.Backend.dto;

public class PlayerStatsDTO {
    private String name;
    private int runs;
    private int balls;
    private int fours;
    private int sixes;
    private String sr; // Strike Rate
    private String overs; // e.g., "0.3"
    private int maidens;
    private int runsConceded;
    private int wickets;
    private String eco; // Economy

    // Getters and Setters
    // (Add all getters and setters for these fields)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getRuns() { return runs; }
    public void setRuns(int runs) { this.runs = runs; }
    public int getBalls() { return balls; }
    public void setBalls(int balls) { this.balls = balls; }
    public int getFours() { return fours; }
    public void setFours(int fours) { this.fours = fours; }
    public int getSixes() { return sixes; }
    public void setSixes(int sixes) { this.sixes = sixes; }
    public String getSr() { return sr; }
    public void setSr(String sr) { this.sr = sr; }
    public String getOvers() { return overs; }
    public void setOvers(String overs) { this.overs = overs; }
    public int getMaidens() { return maidens; }
    public void setMaidens(int maidens) { this.maidens = maidens; }
    public int getRunsConceded() { return runsConceded; }
    public void setRunsConceded(int runsConceded) { this.runsConceded = runsConceded; }
    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }
    public String getEco() { return eco; }
    public void setEco(String eco) { this.eco = eco; }
}
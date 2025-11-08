package com.san.Backend.repository;

import com.san.Backend.model.GameMatch;
import com.san.Backend.model.PlayerStats;
import com.san.Backend.model.PlayerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {

    // This will be crucial for finding the on-strike batsman, bowler, etc.
    Optional<PlayerStats> findByGameMatchAndStatus(GameMatch gameMatch, PlayerStatus status);

    // This helps find all players for a specific team in a match
    List<PlayerStats> findByGameMatchAndTeamName(GameMatch gameMatch, String teamName);
}
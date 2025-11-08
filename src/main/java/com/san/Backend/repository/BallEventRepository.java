package com.san.Backend.repository;

import com.san.Backend.model.BallEvent;
import com.san.Backend.model.GameMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BallEventRepository extends JpaRepository<BallEvent, Long> {

    // This will get all balls for a specific innings, in order
    List<BallEvent> findByGameMatchAndInningsNumber(GameMatch gameMatch, int inningsNumber);
}
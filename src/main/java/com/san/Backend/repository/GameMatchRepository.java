package com.san.Backend.repository;

import com.san.Backend.model.GameMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameMatchRepository extends JpaRepository<GameMatch, Long> {
    // JpaRepository already provides:
    // - save()
    // - findById()
    // - findAll()
    // - delete()
    // ...and many more. We don't need to add anything for now.
}
package com.taylor.restfuldash.repository;

import com.taylor.restfuldash.model.RaidTeam;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository interface for RaidTeam entity
public interface RaidTeamRepository extends JpaRepository<RaidTeam, Long> {
}

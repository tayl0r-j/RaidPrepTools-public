package com.taylor.restfuldash.repository;

import com.taylor.restfuldash.model.RaidTeamMember;
import com.taylor.restfuldash.model.SignupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// Repository interface for RaidTeamMember entity
public interface RaidTeamMemberRepository extends JpaRepository<RaidTeamMember, Long> {

    List<RaidTeamMember> findByTeamId(Long teamId);

    List<RaidTeamMember> findByTeamIdAndStatus(Long teamId, SignupStatus status);

    @Query("""
        SELECT m.playerClass as playerClass,
               m.playerSpec  as playerSpec,
               COUNT(m)      as count
        FROM RaidTeamMember m
        WHERE m.team.id = :teamId
        GROUP BY m.playerClass, m.playerSpec
        ORDER BY m.playerClass, m.playerSpec
        """)
    List<RaidCompositionRow> getCompositionByTeam(@Param("teamId") Long teamId);

    interface RaidCompositionRow {
        com.taylor.restfuldash.model.PlayerClasses getPlayerClass();
        com.taylor.restfuldash.model.PlayerSpec getPlayerSpec();
        Long getCount();
    }
}

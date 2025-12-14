package com.taylor.restfuldash.dto;

import com.taylor.restfuldash.model.RaidTeamMember;
import lombok.Builder;

import java.util.List;

// Response DTO for the raid optimization endpoint
// Returns teamId, groups of assigned members, and unassigned members
@Builder
public record OptimizeRaidResponseDto(
        Long teamId,
        List<List<RaidTeamMember>> groups,
        List<RaidTeamMember> unassigned
) {}

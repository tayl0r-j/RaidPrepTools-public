package com.taylor.restfuldash.service;

//import com.taylor.restfuldash.dto.OptimizeRaidResponseDto;
import com.taylor.restfuldash.exception.NotFoundException;
import com.taylor.restfuldash.model.*;
import com.taylor.restfuldash.repository.RaidTeamMemberRepository;
import com.taylor.restfuldash.repository.RaidTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RaidTeamService {

    private final RaidTeamRepository raidTeamRepository;
    private final RaidTeamMemberRepository raidTeamMemberRepository;

    // Basic CRUD Operations for RaidTeam Entities 
    // Create a new raid team
    public RaidTeam createTeam(RaidTeam team) {
        return raidTeamRepository.save(team);
    }
    // Get a raid team by ID
    public RaidTeam getTeam(Long id) {
        return raidTeamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RaidTeam not found: " + id));
    }
    // Update an existing raid team
    public RaidTeam updateTeam(Long id, RaidTeam updated) {
        RaidTeam existing = getTeam(id);
        existing.setTeamName(updated.getTeamName());
        return raidTeamRepository.save(existing);
    }
    // Delete a raid team by ID
    public void deleteTeam(Long id) {
        raidTeamRepository.delete(getTeam(id));
    }
    // Get all raid teams
    public List<RaidTeam> getAllTeams() {
        return raidTeamRepository.findAll();
    }
}

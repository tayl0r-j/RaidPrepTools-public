package com.taylor.restfuldash.controller;

import com.taylor.restfuldash.dto.OptimizeRaidResponseDto;
import com.taylor.restfuldash.model.RaidTeam;
import com.taylor.restfuldash.service.RaidTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.taylor.restfuldash.dto.CompositionSummaryDto;
import com.taylor.restfuldash.service.RaidTeamMemberService;
import java.util.List;

@RestController
@RequestMapping("/api/raid-teams")
@RequiredArgsConstructor
public class RaidTeamController {

    private final RaidTeamService raidTeamService;
    private final RaidTeamMemberService raidTeamMemberService;

    @PostMapping
    public ResponseEntity<RaidTeam> createTeam(@RequestBody RaidTeam team) {
        return ResponseEntity.ok(raidTeamService.createTeam(team));
    }

    @GetMapping
    public ResponseEntity<List<RaidTeam>> getAllTeams() {
        return ResponseEntity.ok(raidTeamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaidTeam> getTeam(@PathVariable Long id) {
        return ResponseEntity.ok(raidTeamService.getTeam(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RaidTeam> updateTeam(
            @PathVariable Long id,
            @RequestBody RaidTeam updated
    ) {
        return ResponseEntity.ok(raidTeamService.updateTeam(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        raidTeamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    // Get team composition summary
    @GetMapping("/{id}/composition")
    public ResponseEntity<List<CompositionSummaryDto>> getTeamComposition(@PathVariable Long id) {
        return ResponseEntity.ok(raidTeamMemberService.getTeamComposition(id));
    }
}

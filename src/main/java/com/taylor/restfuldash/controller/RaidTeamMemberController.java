package com.taylor.restfuldash.controller;

import com.taylor.restfuldash.dto.SlotUpdateRequest;
import com.taylor.restfuldash.model.RaidTeamMember;
import com.taylor.restfuldash.service.RaidTeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raid-team-members")
@RequiredArgsConstructor
public class RaidTeamMemberController {

    private final RaidTeamMemberService raidTeamMemberService;

    @PostMapping
    public ResponseEntity<RaidTeamMember> createMember(@RequestBody RaidTeamMember member) {
        return ResponseEntity.ok(raidTeamMemberService.createMember(member));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaidTeamMember> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(raidTeamMemberService.getMember(id));
    }

    @GetMapping("/by-team/{teamId}")
    public ResponseEntity<List<RaidTeamMember>> getMembersByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(raidTeamMemberService.getMembersByTeam(teamId));
    }

    @GetMapping("/sorted-by-team/{teamId}")
    public ResponseEntity<List<RaidTeamMember>> getSortedMembersByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(raidTeamMemberService.getSortedMembers(teamId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        raidTeamMemberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/slot")
    public ResponseEntity<RaidTeamMember> updateMemberSlot(
            @PathVariable Long id,
            @RequestBody SlotUpdateRequest request
    ) {
        RaidTeamMember updated = raidTeamMemberService.updateMemberSlot(
                id,
                request.getGroupIndex(),
                request.getSlotIndex()
        );
        return ResponseEntity.ok(updated);
    }
}

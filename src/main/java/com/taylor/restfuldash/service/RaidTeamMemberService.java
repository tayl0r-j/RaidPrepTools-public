package com.taylor.restfuldash.service;
import com.taylor.restfuldash.util.RaidMemberSorter;

import com.taylor.restfuldash.exception.NotFoundException;
import com.taylor.restfuldash.model.RaidTeamMember;
import com.taylor.restfuldash.repository.RaidTeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.taylor.restfuldash.dto.CompositionSummaryDto;
import com.taylor.restfuldash.repository.RaidTeamMemberRepository.RaidCompositionRow;
//import com.taylor.restfuldash.model.PlayerClasses;
//import com.taylor.restfuldash.model.RaidRoles;
//import com.taylor.restfuldash.model.SignupStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RaidTeamMemberService {

    private final RaidTeamMemberRepository raidTeamMemberRepository;

    // Create a member (expects team already assigned in JSON)
    public RaidTeamMember createMember(RaidTeamMember member) {
        return raidTeamMemberRepository.save(member);
    }

    // Get a single member by id
    public RaidTeamMember getMember(Long id) {
        return raidTeamMemberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RaidTeamMember not found: " + id));
    }

    // List all members for a given team
    public List<RaidTeamMember> getMembersByTeam(Long teamId) {
        return raidTeamMemberRepository.findByTeamId(teamId);
    }

    // Delete a member
    public void deleteMember(Long id) {
        if (!raidTeamMemberRepository.existsById(id)) {
            throw new NotFoundException("RaidTeamMember not found: " + id);
        }
        raidTeamMemberRepository.deleteById(id);
    }

    // Get sorted members for a given team
    public List<RaidTeamMember> getSortedMembers(Long teamId) {
    List<RaidTeamMember> members = raidTeamMemberRepository.findByTeamId(teamId);

        // Sort members using the RaidMemberSorter utility
        RaidMemberSorter.sortMembers(members);

    return members;
    }

    // Get team composition summary
    public List<CompositionSummaryDto> getTeamComposition(Long teamId) {
    List<RaidCompositionRow> rows = raidTeamMemberRepository.getCompositionByTeam(teamId);

    return rows.stream()
            .map(row -> new CompositionSummaryDto(
                    row.getPlayerClass(),
                    row.getPlayerSpec(),
                    row.getCount()
            ))
            .toList();
    }

    // Update member's group and slot indices
    public RaidTeamMember updateMemberSlot(Long memberId, Integer groupIndex, Integer slotIndex) {
        RaidTeamMember member = getMember(memberId);
        member.setGroupIndex(groupIndex);
        member.setSlotIndex(slotIndex);
        return raidTeamMemberRepository.save(member);
    }
}
package com.taylor.restfuldash.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaidTeamMember {

    // Primary key for the RaidTeamMember entity
    // Generated automatically by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long raiderId;

    // Player details
    private String playerName;
    @Enumerated(EnumType.STRING)
    private PlayerClasses playerClass;  // "Warrior", "Priest", etc.
    @Enumerated(EnumType.STRING)
    private PlayerSpec playerSpec;   // "Fury", "Holy", etc.
    @Enumerated(EnumType.STRING)
    private RaidRoles role;         // "Tank", "Healer", "Melee", "Ranged"

    // Many-to-one relationship with RaidTeam entity
    @ManyToOne
    @JoinColumn(name = "raid_team_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Prevents infinite recursion during serialization
    private RaidTeam team;

    // Status of the member's signup for the raid
    // Can be SIGNED_UP, ROSTERED, BENCHED, TENTATIVE, or ABSENT
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SignupStatus status = SignupStatus.SIGNED_UP;

    // Group and slot indices for rostered members  
    private Integer groupIndex; // 1-8 for an 8-group raid
    private Integer slotIndex;  // 1-5 for a 5-slot group
}

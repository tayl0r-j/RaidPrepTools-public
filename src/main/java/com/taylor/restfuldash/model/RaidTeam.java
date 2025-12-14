package com.taylor.restfuldash.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// Entity representing a Raid Team in the application
@Entity
// This class models a raid team with a unique ID, a team name, and a list of members.
// Lombok annotations to generate getters, setters, constructors, and builder pattern methods
@Getter
@Setter
@Builder
// Lombok annotations to generate no-args and all-args constructors
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "raidTeamId", "teamName", "members" })
public class RaidTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("raidTeamId")
    private Long id;

    private String teamName;

    // One-to-many relationship with RaidTeamMember entities
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    // List to hold members of the raid team
    @Builder.Default
    private List<RaidTeamMember> members = new ArrayList<>();

    // Convenience helpers to manage bidirectional relationship
    public void addMember(RaidTeamMember m) {
        members.add(m);
        m.setTeam(this);
    }

    public void removeMember(RaidTeamMember m) {
        members.remove(m);
        m.setTeam(null);
    }
    
}

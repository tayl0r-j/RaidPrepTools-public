package com.taylor.restfuldash.util;

import com.taylor.restfuldash.model.*;

import java.util.Comparator;
import java.util.List;

public class RaidMemberSorter {

    public static void sortMembers(List<RaidTeamMember> members) {
        members.sort(
                Comparator.comparingInt((RaidTeamMember m) -> statusPriority(m.getStatus()))
                        .thenComparingInt(m -> classPriority(m.getPlayerClass()))
                        .thenComparingInt(m -> rolePriority(m.getRole()))
                        .thenComparing(m -> safeName(m.getPlayerName()))
        );
    }

    private static int statusPriority(SignupStatus s) {
        if (s == null) return 99;
        return switch (s) {
            case ROSTERED -> 1;
            case SIGNED_UP -> 2;
            case TENTATIVE -> 3;
            case BENCHED -> 4;
            case ABSENT -> 5;
        };
    }

    private static int classPriority(PlayerClasses c) {
        if (c == null) return 99;
        return switch (c) {
            case WARRIOR -> 1;
            case PALADIN -> 2;
            case DRUID -> 3;
            case PRIEST -> 4;
            case SHAMAN -> 5;
            case MAGE -> 6;
            case WARLOCK -> 7;
            case HUNTER -> 8;
            case ROGUE -> 9;
            default -> 99;
        };
    }

    private static int rolePriority(RaidRoles r) {
        if (r == null) return 99;
        return switch (r) {
            case TANK -> 1;
            case HEALER -> 2;
            case MELEE -> 3;
            case RANGED -> 4;
        };
    }

    private static String safeName(String name) {
        return name == null ? "" : name.toLowerCase();
    }
}

package com.taylor.restfuldash.model;

public enum SignupStatus {
    SIGNED_UP,   // in the pool
    ROSTERED,    // placed into a group and slot
    BENCHED,     // optional bench bucket
    TENTATIVE,   // optional ignored bucket
    ABSENT,      // explicitly marked as absent    
}

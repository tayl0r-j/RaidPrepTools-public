package com.taylor.restfuldash.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// Base entity model for the application
// Represents the core data structure with common fields
// Used as the primary data structure for persistence
@Entity
@Table(name = "base_models")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BaseModel(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
package com.taylor.restfuldash.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

// DTO for incoming requests 
// Contains fields for name and description with validation
// Used for creating or updating BaseModel entities
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
}
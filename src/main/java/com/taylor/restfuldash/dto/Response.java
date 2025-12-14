package com.taylor.restfuldash.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

// DTO for outgoing responses
// Contains fields for BaseModel attributes along with message and success status
// Used for sending data back to clients
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
    private Boolean success;
}
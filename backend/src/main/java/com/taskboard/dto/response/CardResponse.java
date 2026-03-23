package com.taskboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    private UUID id;
    private String title;
    private String description;
    private Integer position;
    private UUID listId;
    private UUID assigneeId;
    private String assigneeUsername;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
}

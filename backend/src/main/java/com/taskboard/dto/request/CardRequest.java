package com.taskboard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CardRequest {

    @NotBlank
    @Size(min = 1, max = 200)
    private String title;

    private String description;
    private UUID assigneeId;
    private LocalDate dueDate;
}

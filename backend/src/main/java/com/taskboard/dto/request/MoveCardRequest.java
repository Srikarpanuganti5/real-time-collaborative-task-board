package com.taskboard.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MoveCardRequest {

    @NotNull
    private UUID targetListId;

    @NotNull
    @Min(0)
    private Integer targetPosition;
}

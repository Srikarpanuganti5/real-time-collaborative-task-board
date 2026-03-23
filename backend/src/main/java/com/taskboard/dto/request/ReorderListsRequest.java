package com.taskboard.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReorderListsRequest {

    @NotNull
    private UUID boardId;

    @NotNull
    private List<UUID> orderedListIds;
}

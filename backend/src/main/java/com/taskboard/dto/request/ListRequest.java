package com.taskboard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ListRequest {

    @NotBlank
    @Size(min = 1, max = 100)
    private String title;
}

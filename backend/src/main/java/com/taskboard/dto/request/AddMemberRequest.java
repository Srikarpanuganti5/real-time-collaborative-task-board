package com.taskboard.dto.request;

import com.taskboard.entity.BoardRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMemberRequest {

    @Email
    @NotBlank
    private String email;

    @NotNull
    private BoardRole role;
}

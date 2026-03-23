package com.taskboard.dto.response;

import com.taskboard.entity.BoardRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardMemberResponse {
    private UUID userId;
    private String username;
    private String email;
    private BoardRole role;
}

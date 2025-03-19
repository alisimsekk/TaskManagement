package com.alisimsek.taskmanagement.project.controller.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectTeamMemberDto {
    private String guid;
    private String username;
}

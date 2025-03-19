package com.alisimsek.taskmanagement.project.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddTeamMemberToProjectRequest {
    @NotBlank
    private String projectGuid;
    @NotBlank
    private String teamMemberGuid;
}

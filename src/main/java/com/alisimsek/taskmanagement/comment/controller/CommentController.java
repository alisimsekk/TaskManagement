package com.alisimsek.taskmanagement.comment.controller;

import com.alisimsek.taskmanagement.comment.controller.dto.request.CommentCreateRequest;
import com.alisimsek.taskmanagement.comment.controller.dto.request.CommentUpdateRequest;
import com.alisimsek.taskmanagement.comment.controller.dto.response.CommentDto;
import com.alisimsek.taskmanagement.comment.service.CommentService;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.response.ApiResponse;
import com.alisimsek.taskmanagement.common.util.PaginationUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Create a new comment", description = "Available for all authenticated users")
    @PostMapping
    public ResponseEntity<ApiResponse<CommentDto>> createComment(@Valid @RequestBody CommentCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(commentService.createComment(request)));
    }

    @Operation(summary = "Get comment by GUID", description = "Available for all authenticated users")
    @GetMapping("/{guid}")
    public ResponseEntity<ApiResponse<CommentDto>> getCommentByGuid(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getCommentByGuid(guid)));
    }

    @Operation(summary = "Get all comments with pagination", description = "Available for all authenticated users")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentDto>>> getAllComments(Pageable pageable) {
        Page<CommentDto> commentPage = commentService.getAllComments(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(commentPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(commentPage.getContent()));
    }

    @Operation(summary = "Search comments with criteria", description = "Available for all authenticated users")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CommentDto>>> searchComments(
            @RequestParam(required = false) String taskTitle,
            @RequestParam(required = false) String authorFirstName,
            @RequestParam(required = false) String authorLastName,
            @RequestParam(required = false, defaultValue = "ACTIVE") EntityStatus entityStatus,
            Pageable pageable) {
        Page<CommentDto> commentPage = commentService.searchComment(taskTitle, authorFirstName, authorLastName, entityStatus, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(commentPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(commentPage.getContent()));
    }

    @Operation(summary = "Update comment details", description = "Available for authenticated users who own the comment")
    @PutMapping("/{guid}")
    public ResponseEntity<ApiResponse<CommentDto>> updateComment(@PathVariable String guid, @Valid @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(commentService.updateComment(guid, request)));
    }

    @Operation(summary = "Delete a comment", description = "Available for authenticated users who own the comment")
    @DeleteMapping("/{guid}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable String guid) {
        commentService.deleteComment(guid);
        return ResponseEntity.ok(ApiResponse.empty());
    }

    @Operation(summary = "Activate a deleted comment", description = "Available for authenticated users who own the comment")
    @PostMapping("/activate/{guid}")
    public ResponseEntity<ApiResponse<CommentDto>> activateComment(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(commentService.activateComment(guid)));
    }
}

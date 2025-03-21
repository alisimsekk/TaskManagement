package com.alisimsek.taskmanagement.attachment.controller;

import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentCreateDto;
import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentUpdateRequest;
import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentsCreateRequest;
import com.alisimsek.taskmanagement.attachment.controller.dto.response.AttachmentDto;
import com.alisimsek.taskmanagement.attachment.controller.dto.response.FileDownloadDto;
import com.alisimsek.taskmanagement.attachment.controller.dto.response.FileUploadResponseDto;
import com.alisimsek.taskmanagement.attachment.service.AttachmentService;
import com.alisimsek.taskmanagement.attachment.service.FileStorageService;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.response.ApiResponse;
import com.alisimsek.taskmanagement.common.util.PaginationUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/attachments")
@RequiredArgsConstructor
@Slf4j
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final FileStorageService fileStorageService;

    @Operation(summary = "Create multiple attachments at once", description = "Permission: ADD_ATTACHMENT")
    @PreAuthorize("hasAuthority('ADD_ATTACHMENT')")
    @PostMapping
    public ResponseEntity<ApiResponse<List<AttachmentDto>>> createBulkAttachments(@Valid @RequestBody AttachmentsCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(attachmentService.createBulkAttachments(request)));
    }

    @Operation(summary = "Create a new attachment for a task", description = "Permission: ADD_ATTACHMENT")
    @PreAuthorize("hasAuthority('ADD_ATTACHMENT')")
    @PostMapping("/{taskGuid}")
    public ResponseEntity<ApiResponse<AttachmentDto>> createAttachment(@PathVariable String taskGuid, @Valid @RequestBody AttachmentCreateDto request) {
        return ResponseEntity.ok(ApiResponse.success(attachmentService.createAttachment(taskGuid, request)));
    }

    @Operation(summary = "Get attachment by GUID", description = "Available for authenticated users")
    @GetMapping("/{guid}")
    public ResponseEntity<ApiResponse<AttachmentDto>> getAttachmentByGuid(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(attachmentService.getAttachmentByGuid(guid)));
    }

    @Operation(summary = "Get all attachments with pagination", description = "Available for authenticated users")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AttachmentDto>>> getAllAttachments(Pageable pageable) {
        Page<AttachmentDto> attachmentPage = attachmentService.getAllAttachments(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(attachmentPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(attachmentPage.getContent()));

    }

    @Operation(summary = "Search attachments with criteria", description = "Available for authenticated users")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AttachmentDto>>> searchAttachment(
            @RequestParam(required = false) String originalFilename,
            @RequestParam(required = false) String taskTitle,
            @RequestParam(required = false, defaultValue = "ACTIVE") EntityStatus entityStatus,
            Pageable pageable) {
        Page<AttachmentDto> attachmentPage = attachmentService.searchAttachment(originalFilename, taskTitle, entityStatus, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(attachmentPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(attachmentPage.getContent()));
    }

    @Operation(summary = "Update attachment details", description = "Permission: ADD_ATTACHMENT")
    @PreAuthorize("hasAuthority('ADD_ATTACHMENT')")
    @PutMapping("/{guid}")
    public ResponseEntity<ApiResponse<AttachmentDto>> updateAttachment(@PathVariable String guid, @Valid @RequestBody AttachmentUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(attachmentService.updateAttachment(guid, request)));
    }

    @Operation(summary = "Delete an attachment", description = "Permission: ADD_ATTACHMENT")
    @PreAuthorize("hasAuthority('ADD_ATTACHMENT')")
    @DeleteMapping("/{guid}")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(@PathVariable String guid) {
        attachmentService.deleteAttachment(guid);
        return ResponseEntity.ok(ApiResponse.empty());
    }

    @Operation(summary = "Activate a deleted attachment", description = "Permission: ADD_ATTACHMENT")
    @PreAuthorize("hasAuthority('ADD_ATTACHMENT')")
    @PostMapping("/activate/{guid}")
    public ResponseEntity<ApiResponse<AttachmentDto>> activateAttachment(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(attachmentService.activateAttachment(guid)));
    }

    @Operation(summary = "Upload files to server", description = "Permission: ADD_ATTACHMENT")
    @PreAuthorize("hasAuthority('ADD_ATTACHMENT')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<FileUploadResponseDto>>> saveFiles(@RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(ApiResponse.success(fileStorageService.saveFiles(files)));
    }

    @Operation(summary = "Download a file by filename", description = "Available for authenticated users")
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        FileDownloadDto fileDownloadDto = fileStorageService.downloadFile(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileDownloadDto.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,fileDownloadDto.getHeaderValue())
                .body(fileDownloadDto.getResource());
    }
}

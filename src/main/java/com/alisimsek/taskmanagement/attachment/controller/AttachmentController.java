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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<ApiResponse<List<AttachmentDto>>> createBulkAttachments(@Valid @RequestBody AttachmentsCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(attachmentService.createBulkAttachments(request)));
    }

    @PostMapping("/{taskGuid}")
    public ResponseEntity<ApiResponse<AttachmentDto>> createAttachment(@PathVariable String taskGuid, @Valid @RequestBody AttachmentCreateDto request) {
        return ResponseEntity.ok(ApiResponse.success(attachmentService.createAttachment(taskGuid, request)));
    }

    @GetMapping("/{guid}")
    public ResponseEntity<ApiResponse<AttachmentDto>> getAttachmentByGuid(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(attachmentService.getAttachmentByGuid(guid)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttachmentDto>>> getAllAttachments(Pageable pageable) {
        Page<AttachmentDto> attachmentPage = attachmentService.getAllAttachments(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(attachmentPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(attachmentPage.getContent()));

    }

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

    @PutMapping("/{guid}")
    public ResponseEntity<ApiResponse<AttachmentDto>> updateAttachment(@PathVariable String guid, @Valid @RequestBody AttachmentUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(attachmentService.updateAttachment(guid, request)));
    }

    @DeleteMapping("/{guid}")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(@PathVariable String guid) {
        attachmentService.deleteAttachment(guid);
        return ResponseEntity.ok(ApiResponse.empty());
    }

    @PostMapping("/activate/{guid}")
    public ResponseEntity<ApiResponse<AttachmentDto>> activateAttachment(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(attachmentService.activateAttachment(guid)));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<FileUploadResponseDto>>> saveFiles(@RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(ApiResponse.success(fileStorageService.saveFiles(files)));
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        FileDownloadDto fileDownloadDto = fileStorageService.downloadFile(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileDownloadDto.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,fileDownloadDto.getHeaderValue())
                .body(fileDownloadDto.getResource());
    }
}

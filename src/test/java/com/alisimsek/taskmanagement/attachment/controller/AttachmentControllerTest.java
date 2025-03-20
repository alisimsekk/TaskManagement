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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static com.alisimsek.taskmanagement.attachment.AttachmentTestProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AttachmentControllerTest {

    @Mock
    private AttachmentService attachmentService;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private AttachmentController attachmentController;

    private MockMvc mockMvc;

    private AttachmentDto attachmentDto;
    private AttachmentCreateDto attachmentCreateDto;
    private AttachmentUpdateRequest attachmentUpdateRequest;
    private AttachmentsCreateRequest attachmentsCreateRequest;
    private FileUploadResponseDto fileUploadResponseDto;
    private FileDownloadDto fileDownloadDto;
    private String attachmentGuid;
    private String taskGuid;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(attachmentController).build();

        attachmentGuid = "5b364c88-4d22-4913-aaed-a4460aee331e";
        taskGuid = "352c1feb-763e-4b0f-b43c-2e9728df6d0b";
        attachmentDto = getAttachmentDto(taskGuid);
        attachmentCreateDto = getAttachmentCreateDto();
        attachmentsCreateRequest = getAttachmentCreateRequest(taskGuid, attachmentCreateDto);
        attachmentUpdateRequest = getAttachmentUpdateRequest();
        fileUploadResponseDto = getFileUploadResponseDto();
        fileDownloadDto = getFileDownloadDto();
    }

    @Test
    void createBulkAttachments_shouldReturnSuccessResponse() throws Exception {
        List<AttachmentDto> attachmentDtos = Collections.singletonList(attachmentDto);
        when(attachmentService.createBulkAttachments(any(AttachmentsCreateRequest.class))).thenReturn(attachmentDtos);

        ResultActions response = mockMvc.perform(post(ATTACHMENT_API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(attachmentsCreateRequest)))
                .andExpect(status().isOk());
        assertNotNull(response);
        assertEquals(200, response.andReturn().getResponse().getStatus());
        assertTrue(response.andReturn().getResponse().getContentAsString().contains(attachmentDto.getOriginalFilename()));
    }

    @Test
    void createAttachment_shouldReturnSuccessResponse() {
        when(attachmentService.createAttachment(anyString(), any(AttachmentCreateDto.class))).thenReturn(attachmentDto);

        ResponseEntity<ApiResponse<AttachmentDto>> response = attachmentController.createAttachment(taskGuid, attachmentCreateDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(attachmentDto, response.getBody().getData());
    }

    @Test
    void getAttachmentByGuid_shouldReturnSuccessResponse() {
        when(attachmentService.getAttachmentByGuid(anyString())).thenReturn(attachmentDto);

        ResponseEntity<ApiResponse<AttachmentDto>> response = attachmentController.getAttachmentByGuid(attachmentGuid);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(attachmentDto, response.getBody().getData());
    }

    @Test
    void getAllAttachments_shouldReturnSuccessResponseWithPaginationHeaders() {
        List<AttachmentDto> attachmentDtos = Collections.singletonList(attachmentDto);
        Page<AttachmentDto> AttachmentDtoPage = new PageImpl<>(attachmentDtos);
        Pageable pageable = mock(Pageable.class);

        when(attachmentService.getAllAttachments(any(Pageable.class))).thenReturn(AttachmentDtoPage);

        ResponseEntity<ApiResponse<List<AttachmentDto>>> response = attachmentController.getAllAttachments(pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(attachmentDtos, response.getBody().getData());
    }

    @Test
    void searchAttachment_shouldReturnSuccessResponseWithPaginationHeaders() {
        List<AttachmentDto> attachmentDtos = Collections.singletonList(attachmentDto);
        Page<AttachmentDto> page = new PageImpl<>(attachmentDtos);
        Pageable pageable = mock(Pageable.class);
        String taskTitle = "test-task-title";

        when(attachmentService.searchAttachment(anyString(), anyString(), any(EntityStatus.class), any(Pageable.class))).thenReturn(page);

        ResponseEntity<ApiResponse<List<AttachmentDto>>> response = attachmentController.searchAttachment(
                ORIGINAL_FILE_NAME, taskTitle, EntityStatus.ACTIVE, pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(attachmentDtos, response.getBody().getData());
    }

    @Test
    void updateAttachment_shouldReturnSuccessResponse() {
        attachmentDto.setOriginalFilename("updated-file.txt");
        when(attachmentService.updateAttachment(anyString(), any(AttachmentUpdateRequest.class))).thenReturn(attachmentDto);

        ResponseEntity<ApiResponse<AttachmentDto>> response = attachmentController.updateAttachment(attachmentGuid, attachmentUpdateRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("updated-file.txt", response.getBody().getData().getOriginalFilename());
        assertEquals(attachmentDto, response.getBody().getData());
    }

    @Test
    void deleteAttachment_shouldReturnEmptySuccessResponse() {
        doNothing().when(attachmentService).deleteAttachment(anyString());

        ResponseEntity<ApiResponse<Void>> response = attachmentController.deleteAttachment(attachmentGuid);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getData());
        verify(attachmentService).deleteAttachment(attachmentGuid);
    }

    @Test
    void activateAttachment_shouldReturnSuccessResponse() {
        when(attachmentService.activateAttachment(anyString())).thenReturn(attachmentDto);

        ResponseEntity<ApiResponse<AttachmentDto>> response = attachmentController.activateAttachment(attachmentGuid);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(attachmentDto, response.getBody().getData());
    }

    @Test
    void saveFiles_shouldReturnSuccessResponse() {
        List<MultipartFile> files = Collections.singletonList(getMockMultipartFile());
        List<FileUploadResponseDto> uploadResponses = Collections.singletonList(fileUploadResponseDto);

        when(fileStorageService.saveFiles(anyList())).thenReturn(uploadResponses);

        ResponseEntity<ApiResponse<List<FileUploadResponseDto>>> response = attachmentController.saveFiles(files);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(uploadResponses.getFirst().getOriginalFilename(), response.getBody().getData().getFirst().getOriginalFilename());
        verify(fileStorageService).saveFiles(files);
    }

    @Test
    void downloadFile_shouldReturnResourceResponse() {
        String filename = "test-file.txt";
        when(fileStorageService.downloadFile(anyString())).thenReturn(fileDownloadDto);

        ResponseEntity<Resource> response = attachmentController.downloadFile(filename);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.TEXT_PLAIN, response.getHeaders().getContentType());
        assertEquals("attachment; filename=\"test-file.txt\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(fileDownloadDto.getResource(), response.getBody());
    }

}

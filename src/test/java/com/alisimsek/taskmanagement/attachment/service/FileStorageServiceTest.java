package com.alisimsek.taskmanagement.attachment.service;

import com.alisimsek.taskmanagement.attachment.controller.dto.response.FileDownloadDto;
import com.alisimsek.taskmanagement.attachment.controller.dto.response.FileUploadResponseDto;
import com.alisimsek.taskmanagement.common.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static com.alisimsek.taskmanagement.attachment.AttachmentTestProvider.getMultipartFile;
import static com.alisimsek.taskmanagement.attachment.AttachmentTestProvider.getMultipartFileThatThrowsIOException;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    @Test
    void saveFiles_ShouldReturnListOfFileUploadResponseDto() {
        List<MultipartFile> files = Arrays.asList(
                getMultipartFile("test1.pdf", "application/pdf"),
                getMultipartFile("test2.jpg", "image/jpeg")
        );

        List<FileUploadResponseDto> results = fileStorageService.saveFiles(files);

        assertEquals(2, results.size());
        assertEquals("test1.pdf", results.get(0).getOriginalFilename());
        assertEquals("test2.jpg", results.get(1).getOriginalFilename());
        assertEquals("application/pdf", results.get(0).getFileType());
        assertEquals("image/jpeg", results.get(1).getFileType());
    }

    @Test
    void uploadFile_ShouldThrowEmptyFileException_WhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", new byte[0]
        );

        assertThrows(EmptyFileException.class, () ->
                fileStorageService.saveFiles(List.of(emptyFile))
        );
    }

    @Test
    void uploadFile_ShouldThrowInvalidFilenameException_WhenFilenameIsNull() {
        MockMultipartFile fileWithNullName = new MockMultipartFile(
                "file", null, "application/pdf", "test content".getBytes()
        );

        assertThrows(InvalidFilenameException.class, () ->
                fileStorageService.saveFiles(List.of(fileWithNullName))
        );
    }

    @Test
    void uploadFile_ShouldThrowInvalidFilenameException_WhenFilenameIsEmpty() {
        MockMultipartFile fileWithEmptyName = new MockMultipartFile(
                "file", "", "application/pdf", "test content".getBytes()
        );

        assertThrows(InvalidFilenameException.class, () ->
                fileStorageService.saveFiles(List.of(fileWithEmptyName))
        );
    }

    @Test
    void uploadFile_ShouldThrowFileSizeLimitException_WhenFileSizeExceedsLimit() {
        MockMultipartFile largeFile = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", new byte[11 * 1024 * 1024]
        );

        assertThrows(FileSizeLimitException.class, () ->
                fileStorageService.saveFiles(List.of(largeFile)));
    }

    @Test
    void uploadFile_ShouldThrowFileTypeNotAllowedException_WhenFileExtensionNotAllowed() {
        MockMultipartFile fileWithInvalidExtension = new MockMultipartFile(
                "file", "test.exe", "application/octet-stream", "test content".getBytes()
        );

        assertThrows(FileTypeNotAllowedException.class, () ->
                fileStorageService.saveFiles(List.of(fileWithInvalidExtension))
        );
    }

    @Test
    void uploadFile_ShouldThrowFileExtensionNotFoundException_WhenFileHasNoExtension() {
        MockMultipartFile fileWithNoExtension = new MockMultipartFile(
                "file", "testfile", "application/octet-stream", "test content".getBytes()
        );

        assertThrows(FileExtensionNotFoundException.class, () ->
                fileStorageService.saveFiles(List.of(fileWithNoExtension))
        );
    }


    @Test
    void uploadFile_ShouldThrowFileStorageException_WhenIOExceptionOccurs() throws Exception {
        MultipartFile fileWithIOException = getMultipartFileThatThrowsIOException();

        assertThrows(FileStorageException.class, () ->
                fileStorageService.saveFiles(List.of(fileWithIOException))
        );
    }

    @Test
    void downloadFile_ShouldReturnFileDownloadDto_WhenFileExists() throws Exception {
        List<FileUploadResponseDto> uploadResults = fileStorageService.saveFiles(
                List.of(getMultipartFile("test-download.pdf", "application/pdf"))
        );
        String uniqueFilename = uploadResults.get(0).getUniqueFilename();

        FileDownloadDto result = fileStorageService.downloadFile(uniqueFilename);

        assertNotNull(result);
        assertNotNull(result.getResource());
        assertTrue(result.getResource().exists());
        assertEquals("application/pdf", result.getContentType());
        assertTrue(result.getHeaderValue().contains("attachment"));
        assertTrue(result.getHeaderValue().contains(uniqueFilename));
    }

    @Test
    void downloadFile_ShouldThrowFileNotFoundException_WhenFileDoesNotExist() {
        String nonExistentFilename = "non-existent-file.pdf";

        assertThrows(FileNotFoundException.class, () ->
                fileStorageService.downloadFile(nonExistentFilename)
        );
    }

    @Test
    void downloadFile_ShouldThrowUnauthorizedDirectoryAccessException_WhenPathTraversalAttempt() {
        String pathTraversalFilename = "../../../etc/passwd";

        assertThrows(FileNotFoundException.class, () ->
                fileStorageService.downloadFile(pathTraversalFilename)
        );
    }

    @Test
    void downloadFile_ShouldHandleEncodedFilenames() throws Exception {
        List<FileUploadResponseDto> uploadResults = fileStorageService.saveFiles(
                List.of(getMultipartFile("test file with spaces.pdf", "application/pdf"))
        );
        String uniqueFilename = uploadResults.get(0).getUniqueFilename();

        FileDownloadDto result = fileStorageService.downloadFile(uniqueFilename);

        assertNotNull(result);
        assertNotNull(result.getResource());
        assertTrue(result.getResource().exists());
    }
}

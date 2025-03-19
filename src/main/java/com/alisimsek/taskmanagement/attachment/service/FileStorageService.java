package com.alisimsek.taskmanagement.attachment.service;

import com.alisimsek.taskmanagement.attachment.controller.dto.response.FileDownloadDto;
import com.alisimsek.taskmanagement.attachment.controller.dto.response.FileUploadResponseDto;
import com.alisimsek.taskmanagement.common.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileStorageService {

    private static final String STORAGE_DIR = "uploads/";
    private static final String DOWNLOAD_URI = "/api/v1/attachments/download/";
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "pdf", "docx", "xlsx", "txt");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public List<FileUploadResponseDto> saveFiles(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    private FileUploadResponseDto uploadFile(MultipartFile file) {
        validateFile(file);
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = generateUniqueFilename(fileExtension);
        Path storageDirectory = Paths.get(STORAGE_DIR).toAbsolutePath().normalize();
        Path targetPath = storageDirectory.resolve(uniqueFilename);

        try {
            Files.createDirectories(storageDirectory);

            if (!targetPath.startsWith(storageDirectory)) {
                throw new UnauthorizedDirectoryAccessException();
            }

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("File uploaded successfully: {}", targetPath);
            String fileDownloadUri = DOWNLOAD_URI.concat(uniqueFilename);

            return FileUploadResponseDto.builder()
                    .originalFilename(originalFilename)
                    .uniqueFilename(uniqueFilename)
                    .fileDownloadUri(fileDownloadUri)
                    .fileType(file.getContentType())
                    .size(file.getSize())
                    .build();
        } catch (IOException e) {
            log.error("An error occurred while uploading the file: {}", e.getMessage());
            throw new FileStorageException();
        }
    }

    public FileDownloadDto downloadFile(String filename) {
        try {
            String encodedFilename = getEncodedFilename(filename);

            Path storageDirectory = Paths.get(STORAGE_DIR).toAbsolutePath().normalize();
            Path filePath = storageDirectory.resolve(encodedFilename).normalize();

            if (!filePath.startsWith(storageDirectory)) {
                throw new UnauthorizedDirectoryAccessException();
            }

            if (!Files.exists(filePath)) {
                throw new FileNotFoundException();
            }

            Resource resource = new UrlResource(filePath.toUri());
            String contentType = determineContentType(filePath);
            String headerValue = "attachment; filename=\"" + filename + "\"";
            log.info("File downloaded -> {}", resource.getFilename());
            return FileDownloadDto.builder()
                    .resource(resource)
                    .contentType(contentType)
                    .headerValue(headerValue)
                    .build();
        } catch (MalformedURLException | FileNotFoundException e) {
            log.error("An error occurred while downloading the file: {}", e.getMessage());
            throw new FileNotFoundException();
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException();
        }

        String originalFilename = file.getOriginalFilename();
        if (Objects.isNull(originalFilename) || originalFilename.trim().isEmpty()) {
            throw new InvalidFilenameException();
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeLimitException();
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileTypeNotAllowedException();
        }
    }

    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf(".");
        if (lastIndex == -1) {
            throw new FileExtensionNotFoundException();
        }
        return filename.substring(lastIndex + 1).toLowerCase();
    }

    private String determineContentType(Path filePath) {
        try {
            String contentType = Files.probeContentType(filePath);
            return contentType != null ? contentType : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

    private String generateUniqueFilename(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    private static String getEncodedFilename(String filename) {
        return URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
    }
}

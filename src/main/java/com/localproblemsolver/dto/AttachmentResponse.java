package com.localproblemsolver.dto;

import java.time.LocalDateTime;

public class AttachmentResponse {

    private Long id;
    private Long problemId;
    private String fileName;
    private String fileType;
    private String filePath;
    private LocalDateTime uploadedAt;

    public AttachmentResponse() {
    }

    public AttachmentResponse(
            Long id,
            Long problemId,
            String fileName,
            String fileType,
            String filePath,
            LocalDateTime uploadedAt) {

        this.id = id;
        this.problemId = problemId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
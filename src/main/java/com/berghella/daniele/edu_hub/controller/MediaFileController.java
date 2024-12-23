package com.berghella.daniele.edu_hub.controller;

import com.berghella.daniele.edu_hub.model.MediaFileDTO;
import com.berghella.daniele.edu_hub.model.Subject;
import com.berghella.daniele.edu_hub.model.User;
import com.berghella.daniele.edu_hub.service.MediaFileService;
import com.berghella.daniele.edu_hub.service.SubjectService;
import com.berghella.daniele.edu_hub.service.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UploadedFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MediaFileController {

    private final MediaFileService mediaFileService = new MediaFileService("uploads");
    private final SubjectService subjectService = new SubjectService();
    private final UserService userService = new UserService();

    public void registerRoutes(Javalin app) {
        app.get("/files", this::getAllFiles);
        app.get("/files/{subjectId}", this::getFilesBySubjectId);
        app.get("/files/download/{id}", this::downloadFileById);
        app.post("/files/upload", this::uploadFiles);
        app.delete("/files/delete/{id}", this::deleteFileById);
    }

    private void uploadFiles(Context ctx) {
        List<UploadedFile> uploadedFiles = ctx.uploadedFiles("files");

        if (uploadedFiles.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("No files have been received!");
            return;
        }

        try {
            UUID subjectId = UUID.fromString(Objects.requireNonNull(ctx.formParam("subject-id")));
            UUID teacherId = UUID.fromString(Objects.requireNonNull(ctx.formParam("teacher-id")));
            Optional<User> teacherOP = userService.getUserById(teacherId);
            Optional<Subject> subjectOP = subjectService.getSubjectById(subjectId);

            if (teacherOP.isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST).result("Teacher ID not found!");
                return;
            }
            if (subjectOP.isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST).result("Subject ID not found!");
                return;
            }

            User teacher = teacherOP.get();
            Subject subject = subjectOP.get();

            StringBuilder response = new StringBuilder();
            response.append("Files successfully saved:\n");

            for (UploadedFile uploadedFile : uploadedFiles) {
                String fileName = uploadedFile.filename();

                try (InputStream fileContent = uploadedFile.content()) {
                    Map<UUID, Path> fileMap = mediaFileService.uploadFile(fileName, fileContent, subject, teacher);
                    UUID fileId = fileMap.keySet().iterator().next();
                    Path filePath = fileMap.get(fileId);

                    response.append(" - File: ").append(fileName)
                            .append(" -> Path: ").append(filePath)
                            .append(" / ID: ").append(fileId).append("\n");
                } catch (IOException e) {
                    ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("An error occurred while saving the file " + fileName);
                    return;
                }
            }

            ctx.status(HttpStatus.CREATED).result(response.toString());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("An unexpected error occurred");
        }
    }

    public void getAllFiles(Context ctx) {
        try {
            List<MediaFileDTO> files = mediaFileService.getAllFiles();

            if (files.isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST).result("Files not found!");
            } else {
                ctx.status(HttpStatus.OK).json(files);
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("This error occurred while fetching files: " + e.getMessage());
        }
    }

    public void getFilesBySubjectId(Context ctx) {
        try {
            UUID subjectId = UUID.fromString(ctx.pathParam("subjectId"));
            List<MediaFileDTO> files = mediaFileService.getFilesBySubjectId(subjectId);
            if (files.isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST).result("Files not found!");
            } else {
                ctx.status(HttpStatus.OK).json(files);
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("An error occurred while fetching files");
        }
    }

    private void downloadFileById(Context ctx) {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            File file = mediaFileService.downloadFileById(id).orElse(null);

            if (file == null) {
                ctx.status(HttpStatus.BAD_REQUEST).result("File not found");
                return;
            }

            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            ctx.contentType(mimeType);
            ctx.header("Content-Disposition", "attachment; filename=" + file.getName());
            ctx.result(new FileInputStream(file));
        } catch (IOException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("An error occurred while fetching files");
        }
    }

    public void deleteFileById(Context ctx) {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            boolean success = mediaFileService.isDeletedFileById(id);

            if (success) {
                ctx.status(HttpStatus.OK).result("File deleted!");
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).result("File not found or impossible to delete.");
            }

        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("An error occurred while deleting files");
        }
    }

}

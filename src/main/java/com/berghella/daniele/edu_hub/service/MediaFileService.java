package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.MediaFileDAO;
import com.berghella.daniele.edu_hub.model.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

public class MediaFileService {
    private final String uploadDir;
    private final MediaFileDAO mediaFileDAO = new MediaFileDAO();

    public MediaFileService(String uploadDir) {
        this.uploadDir = uploadDir;
        ensureUploadDirExists();
    }

    private void ensureUploadDirExists() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public Map<UUID, Path> uploadFile(String fileName, InputStream fileContent, Course course, User teacher) throws IOException {
        Path filePath = Paths.get(uploadDir, fileName);
        Map<UUID, Path> fileMap = new HashMap<>();
        try (OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileContent.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            LocalDate uploadDate = LocalDate.now();
            MediaFile file = new MediaFile(fileName, filePath, course, teacher, uploadDate);
            mediaFileDAO.uploadFile(file);
            fileMap.put(file.getId(), filePath);
        }
        return fileMap;
    }

    public List<MediaFileDTO> getAllFiles() {
        return mediaFileDAO.getAllFiles();
    }

    public List<MediaFileDTO> getFilesByCourseId(UUID courseId) {
        return mediaFileDAO.getFilesByCourseId(courseId);
    }

    public Optional<File> downloadFileById(UUID id) {
        return mediaFileDAO.downloadFileById(id).map(mediaFile -> {
            Path filePath = Paths.get(mediaFile.getPath().toString());
            File file = filePath.toFile();
            if (file.exists() && file.isFile()) {
                return file;
            }
            return null;
        });
    }

    public boolean isDeletedFileById(UUID id) {
        try {
            ResultSet rs = mediaFileDAO.getFileResultSetById(id);

            if (!rs.next()) {
                return false;
            }

            Path filePath = Paths.get(rs.getString("path"));
            File file = filePath.toFile();
            boolean fileDeleted = true;

            if (file.exists()) {
                fileDeleted = file.delete();
            }

            boolean recordDeleted = mediaFileDAO.isDeletedFileById(id);
            return fileDeleted && recordDeleted;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


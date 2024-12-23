package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.auth.model.Auth;
import com.berghella.daniele.edu_hub.model.MediaFile;
import com.berghella.daniele.edu_hub.model.MediaFileDTO;
import com.berghella.daniele.edu_hub.model.Subject;
import com.berghella.daniele.edu_hub.model.User;
import com.berghella.daniele.edu_hub.service.SubjectService;
import com.berghella.daniele.edu_hub.service.UserService;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MediaFileDAO {
    private final Connection connection = DatabaseConnection.getInstance().getConnection();
    private SubjectService subjectService = new SubjectService();
    private UserService userService = new UserService();

    public void uploadFile(MediaFile file){
        String insertFileSQL = "INSERT INTO file(id, file_name, path, subject_id, teacher_id, upload_date) " + "VALUES (?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement psInsertFile = connection.prepareStatement(insertFileSQL);
            psInsertFile.setObject(1, file.getId());
            psInsertFile.setString(2, file.getFileName());
            psInsertFile.setString(3, file.getPath().toString());
            psInsertFile.setObject(4, file.getSubject().getId());
            psInsertFile.setObject(5, file.getTeacher().getId());
            psInsertFile.setObject(6, file.getUploadDate());
            psInsertFile.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MediaFileDTO> getAllFiles() {
        String query = "SELECT * FROM file";
        List<MediaFileDTO> files = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MediaFileDTO file = new MediaFileDTO();
                file.setId(UUID.fromString(rs.getString("id")));
                file.setFileName(rs.getString("file_name"));
                file.setPath(Paths.get(rs.getString("path")));
                file.setSubjectName(subjectService.getSubjectById(UUID.fromString(rs.getString("subject_id"))).get().getName());
                file.setTeacherLastName(userService.getUserById(UUID.fromString(rs.getString("teacher_id"))).get().getLastName());
                file.setUploadDate(rs.getDate("upload_date").toLocalDate());
                files.add(file);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return files;
    }

    public List<MediaFileDTO> getFilesBySubjectId(UUID subjectId) {
        String query = "SELECT * FROM file WHERE subject_id = ?";
        List<MediaFileDTO> files = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MediaFileDTO file = new MediaFileDTO();
                    file.setId(UUID.fromString(rs.getString("id")));
                    file.setFileName(rs.getString("file_name"));
                    file.setPath(Paths.get(rs.getString("path")));
                    file.setSubjectName(subjectService.getSubjectById(UUID.fromString(rs.getString("subject_id"))).get().getName());
                    file.setTeacherLastName(userService.getUserById(UUID.fromString(rs.getString("teacher_id"))).get().getLastName());
                    file.setUploadDate(rs.getDate("upload_date").toLocalDate());
                    files.add(file);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return files;
    }

    public ResultSet getFileResultSetById(UUID id){
        try {
            String selectFileByIdSQL = "SELECT * FROM file WHERE id = ?";
            PreparedStatement psSelectFileById = connection.prepareStatement(selectFileByIdSQL);
            psSelectFileById.setObject(1, id);
            return psSelectFileById.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<MediaFile> downloadFileById(UUID id){
        MediaFile file = new MediaFile();
        try {
            ResultSet rs = getFileResultSetById(id);

            if (rs.next()) {
                file.setId(UUID.fromString(rs.getString("id")));
                file.setFileName(rs.getString("file_name"));
                file.setPath(Paths.get(rs.getString("path")));
                file.setSubject(subjectService.getSubjectById(UUID.fromString(rs.getString("subject_id"))).get());
                file.setTeacher(userService.getUserById(UUID.fromString(rs.getString("teacher_id"))).get());
                file.setUploadDate(rs.getDate("upload_date").toLocalDate());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(file);
    }

    public boolean isDeletedFileById(UUID id) {
        String query = "DELETE FROM file WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}

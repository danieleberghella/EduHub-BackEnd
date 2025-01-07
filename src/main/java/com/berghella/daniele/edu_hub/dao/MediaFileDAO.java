package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.*;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MediaFileDAO {
    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public void uploadFile(MediaFile file) {
        String insertFileSQL = "INSERT INTO file(id, file_name, path, course_id, teacher_id, upload_date) " + "VALUES (?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement psInsertFile = connection.prepareStatement(insertFileSQL);
            psInsertFile.setObject(1, file.getId());
            psInsertFile.setString(2, file.getFileName());
            psInsertFile.setString(3, file.getPath().toString());
            psInsertFile.setObject(4, file.getCourse().getId());
            psInsertFile.setObject(5, file.getTeacher().getId());
            psInsertFile.setObject(6, file.getUploadDate());
            psInsertFile.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MediaFileDTO> getAllFiles() {
        String query = """
                    SELECT 
                        f.id AS file_id, 
                        f.file_name, 
                        f.path, 
                        f.upload_date, 
                        c.name AS course_name, 
                        u.last_name AS teacher_last_name
                    FROM 
                        file f
                    JOIN 
                        course c
                    ON 
                        f.course_id = c.id
                    JOIN 
                        users u
                    ON 
                        f.teacher_id = u.id
                """;
        List<MediaFileDTO> files = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MediaFileDTO file = new MediaFileDTO();
                file.setId(UUID.fromString(rs.getString("file_id")));
                file.setFileName(rs.getString("file_name"));
                file.setPath(Paths.get(rs.getString("path")));
                file.setCourseName(rs.getString("course_name"));
                file.setTeacherLastName(rs.getString("teacher_last_name"));
                file.setUploadDate(rs.getDate("upload_date").toLocalDate());
                files.add(file);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return files;
    }

    public List<MediaFileDTO> getFilesByCourseId(UUID courseId) {
        String query = """
                    SELECT 
                        f.id AS file_id, 
                        f.file_name, 
                        f.path, 
                        f.upload_date, 
                        c.name AS course_name, 
                        u.last_name AS teacher_last_name
                    FROM 
                        file f
                    JOIN 
                        course c
                    ON 
                        f.course_id = c.id
                    JOIN 
                        users u
                    ON 
                        f.teacher_id = u.id
                    WHERE 
                        f.course_id = ?
                """;
        List<MediaFileDTO> files = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MediaFileDTO file = new MediaFileDTO();
                    file.setId(UUID.fromString(rs.getString("file_id")));
                    file.setFileName(rs.getString("file_name"));
                    file.setPath(Paths.get(rs.getString("path")));
                    file.setCourseName(rs.getString("course_name"));
                    file.setTeacherLastName(rs.getString("teacher_last_name"));
                    file.setUploadDate(rs.getDate("upload_date").toLocalDate());
                    files.add(file);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return files;
    }

    public ResultSet getFileResultSetById(UUID id) {
        try {
            String selectFileByIdSQL = """
                        SELECT 
                            f.id AS file_id,
                            f.file_name,
                            f.path,
                            f.upload_date,
                            c.id AS course_id,
                            c.name AS course_name,
                            c.total_hours AS course_total_hours,
                            u.id AS teacher_id,
                            u.first_name AS teacher_first_name,
                            u.last_name AS teacher_last_name
                        FROM 
                            file f
                        JOIN 
                            course c
                        ON 
                            f.course_id = c.id
                        JOIN 
                            users u
                        ON 
                            f.teacher_id = u.id
                        WHERE 
                            f.id = ?
                    """;
            PreparedStatement psSelectFileById = connection.prepareStatement(selectFileByIdSQL);
            psSelectFileById.setObject(1, id);
            return psSelectFileById.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<MediaFile> downloadFileById(UUID id) {
        try {
            ResultSet rs = getFileResultSetById(id);

            if (rs.next()) {
                MediaFile file = new MediaFile();
                file.setId(UUID.fromString(rs.getString("file_id")));
                file.setFileName(rs.getString("file_name"));
                file.setPath(Paths.get(rs.getString("path")));
                file.setUploadDate(rs.getDate("upload_date").toLocalDate());

                Course course = new Course();
                course.setId(UUID.fromString(rs.getString("course_id")));
                course.setName(rs.getString("course_name"));
                course.setTotalHours(rs.getInt("course_total_hours"));
                file.setCourse(course);

                User teacher = new User();
                teacher.setId(UUID.fromString(rs.getString("teacher_id")));
                teacher.setFirstName(rs.getString("teacher_first_name"));
                teacher.setLastName(rs.getString("teacher_last_name"));
                file.setTeacher(teacher);

                return Optional.of(file);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
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

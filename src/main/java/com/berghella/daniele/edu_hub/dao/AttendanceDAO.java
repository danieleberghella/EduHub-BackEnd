package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.AttendanceDTO;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttendanceDAO {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public UUID markAttendance(UUID userId, UUID courseId, LocalDate attendanceDate, boolean isPresent, UUID subjectId) {
        String sql = "INSERT INTO attendance (id, student_id, course_id, attendance_date, is_present, subject_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            UUID attendanceId = UUID.randomUUID();
            statement.setObject(1, attendanceId);
            statement.setObject(2, userId);
            statement.setObject(3, courseId);
            statement.setObject(4, attendanceDate);
            statement.setBoolean(5, isPresent);
            statement.setObject(6, subjectId);
            statement.executeUpdate();
            return attendanceId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AttendanceDTO> getUserAttendancesByCourseId(UUID studentId, UUID courseId) {
        List<AttendanceDTO> attendances = new ArrayList<>();
        String query = "SELECT a.* FROM attendance a WHERE student_id = ? AND course_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, studentId);
            stmt.setObject(2, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID student = UUID.fromString(rs.getString("student_id"));
                    UUID course = UUID.fromString(rs.getString("course_id"));
                    LocalDate attendanceDate = rs.getDate("attendance_date").toLocalDate();
                    boolean isPresent = rs.getBoolean("is_present");
                    UUID subjectId = UUID.fromString(rs.getString("subject_id"));

                    AttendanceDTO attendance = new AttendanceDTO(student, course, attendanceDate, isPresent, subjectId);
                    attendances.add(attendance);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return attendances;
    }

    public List<AttendanceDTO> getUserAttendancesBySubjectId(UUID studentId, UUID subjectId) {
        List<AttendanceDTO> attendances = new ArrayList<>();
        String query = "SELECT a.* FROM attendance a WHERE student_id = ? AND subject_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, studentId);
            stmt.setObject(2, subjectId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID student = UUID.fromString(rs.getString("student_id"));
                    UUID course = UUID.fromString(rs.getString("course_id"));
                    LocalDate attendanceDate = rs.getDate("attendance_date").toLocalDate();
                    boolean isPresent = rs.getBoolean("is_present");
                    UUID subject = UUID.fromString(rs.getString("subject_id"));

                    AttendanceDTO attendance = new AttendanceDTO(student, course, attendanceDate, isPresent, subject);
                    attendances.add(attendance);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return attendances;
    }
}

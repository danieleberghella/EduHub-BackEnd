package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.Subject;
import com.berghella.daniele.edu_hub.model.SubjectCourse;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SubjectCourseDAO {
    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    private Subject mapResultSetToSubject(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setId(UUID.fromString(rs.getString("id")));
        subject.setName(rs.getString("name"));
        subject.setDescription(rs.getString("description"));
        return subject;
    }

    public List<SubjectCourse> getAllSubjectCourses() {
        List<SubjectCourse> subjectCourses = new ArrayList<>();
        String sql = "SELECT * FROM subject_course";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                SubjectCourse sc = mapResultSetToSubjectCourse(rs);
                subjectCourses.add(sc);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching subject_courses", e);
        }
        return subjectCourses;
    }

    public List<Subject> getSubjectsByCourseId(UUID courseId, boolean isEnrolled) {
        String query;
        if (isEnrolled) {
            query = "SELECT DISTINCT s.* " +
                    "FROM subject s " +
                    "JOIN subject_course sc ON s.id = sc.subject_id " +
                    "WHERE sc.course_id = ?";
        } else {
            query = "SELECT DISTINCT s.* " +
                    "FROM subject s " +
                    "LEFT JOIN subject_course sc ON s.id = sc.subject_id AND sc.course_id = ? " +
                    "WHERE sc.course_id IS NULL";
        }

        List<Subject> subjects = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                subjects.add(mapResultSetToSubject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving subjects by course ID", e);
        }
        return subjects;
    }

    public Optional<SubjectCourse> getSubjectCourseById(UUID id) {
        String sql = "SELECT * FROM subject_course WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToSubjectCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching subject_course by ID", e);
        }
        return Optional.empty();
    }

    public UUID createSubjectCourse(SubjectCourse subjectCourse) {
        String sql = "INSERT INTO subject_course (id, subject_id, course_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, subjectCourse.getId());
            ps.setObject(2, subjectCourse.getSubjectId());
            ps.setObject(3, subjectCourse.getCourseId());
            ps.executeUpdate();
            return subjectCourse.getId();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating subject_course", e);
        }
    }

    public SubjectCourse updateSubjectCourse(UUID id, SubjectCourse subjectCourse) {
        String sql = "UPDATE subject_course SET subject_id = ?, course_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, subjectCourse.getSubjectId());
            ps.setObject(2, subjectCourse.getCourseId());
            ps.setObject(3, id);
            if (ps.executeUpdate() > 0) {
                return getSubjectCourseById(id).orElse(null);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating subject_course", e);
        }
        return null;
    }

    public boolean deleteSubjectCourseBySubjectAndCourseId(UUID subjectId, UUID courseId) {
        String sql = "DELETE FROM subject_course WHERE subject_id = ? AND course_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, subjectId);
            ps.setObject(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting subject_course by subject and course IDs", e);
        }
    }

    private SubjectCourse mapResultSetToSubjectCourse(ResultSet rs) throws SQLException {
        return new SubjectCourse(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("subject_id")),
                UUID.fromString(rs.getString("course_id"))
        );
    }
}

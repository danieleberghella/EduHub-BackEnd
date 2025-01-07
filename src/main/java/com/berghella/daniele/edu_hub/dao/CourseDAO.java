package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.Course;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CourseDAO {
    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public void createCourse(Course course){
        String insertCourseSQL = "INSERT INTO course(id, name, description, total_hours) " + "VALUES (?, ?, ?, ?);";
        try {
            PreparedStatement psInsertCourse = connection.prepareStatement(insertCourseSQL);
            psInsertCourse.setObject(1, course.getId());
            psInsertCourse.setString(2, course.getName());
            psInsertCourse.setString(3, course.getDescription());
            psInsertCourse.setInt(4, course.getTotalHours());
            psInsertCourse.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String getAllCoursesSQL = "SELECT * FROM course";
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(getAllCoursesSQL);
            while (rs.next()){
                Course course = new Course(
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("total_hours"));
                course.setId(UUID.fromString(rs.getString("id")));
                courses.add(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return courses;
    }

    public List<Course> getAllCoursesByUserId(UUID userId) {
        List<Course> coursesByUserId = new ArrayList<>();
        String getAllCoursesByUserIdSQL = """
        SELECT c.id, c.name, c.description, c.total_hours 
        FROM course c
        INNER JOIN enrollment e ON c.id = e.course_id
        WHERE e.user_id = ?
    """;
        try (PreparedStatement ps = connection.prepareStatement(getAllCoursesByUserIdSQL)) {
            ps.setObject(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Course course = new Course(
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("total_hours"));
                course.setId(UUID.fromString(rs.getString("id")));
                coursesByUserId.add(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching courses for user: " + userId);
        }
        return coursesByUserId;
    }

    public Optional<Course> getCourseById(UUID id) {
        String selectCourseByIdSQL = "SELECT * FROM course WHERE id = ?";
        try {
            PreparedStatement psSelectCourseById = connection.prepareStatement(selectCourseByIdSQL);
            psSelectCourseById.setObject(1, id);
            ResultSet rs = psSelectCourseById.executeQuery();
            if (rs.next()) {
                Course course = new Course();
                course.setId(id);
                course.setName(rs.getString("name"));
                course.setDescription(rs.getString("description"));
                course.setTotalHours(rs.getInt("total_hours"));
                return Optional.of(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Course updateCourseById(Course updatedCourse, UUID oldCourseId) {
        if (getCourseById(oldCourseId).isPresent()){
            StringBuilder sql = new StringBuilder("UPDATE course SET ");
            List<Object> parameters = new ArrayList<>();

            if (updatedCourse.getName() != null) {
                sql.append("name = ?, ");
                parameters.add(updatedCourse.getName());
            }
            if (updatedCourse.getDescription() != null) {
                sql.append("description = ?, ");
                parameters.add(updatedCourse.getDescription());
            }
            if (updatedCourse.getTotalHours() > 0) {
                sql.append("total_hours = ?, ");
                parameters.add(updatedCourse.getTotalHours());
            }

            sql.setLength(sql.length() - 2);
            sql.append(" WHERE id = ?");
            parameters.add(oldCourseId);
            try {
                PreparedStatement psUpdateCourse = connection.prepareStatement(sql.toString());
                for (int i = 0; i < parameters.size(); i++) {
                    psUpdateCourse.setObject(i + 1, parameters.get(i));
                }
                psUpdateCourse.executeUpdate();
                return getCourseById(oldCourseId).orElse(null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public boolean isDeletedCourseById(UUID id) {
        String deleteCourseSQL = "DELETE FROM course WHERE id = ?";
        try {
            PreparedStatement psDeleteCourse = connection.prepareStatement(deleteCourseSQL);
            psDeleteCourse.setObject(1, id);
            int rowsAffected = psDeleteCourse.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting course with ID: " + id);
        }
    }
}

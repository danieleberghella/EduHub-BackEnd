package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.EnrollmentDAO;
import com.berghella.daniele.edu_hub.model.Course;
import com.berghella.daniele.edu_hub.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class EnrollmentService {

    private final EnrollmentDAO enrollmentDAO;

    public EnrollmentService() {
        this.enrollmentDAO = new EnrollmentDAO();
    }

    public UUID enrollUser(UUID userId, UUID courseId, LocalDate enrollmentDate) {
        return enrollmentDAO.enrollUser(userId, courseId, enrollmentDate);
    }

    public boolean isDeletedEnrollmentByUserIdAndCourseId(UUID userId, UUID courseId) {
        return enrollmentDAO.isDeletedEnrollmentByUserIdAndCourseId(userId, courseId);
    }

    public List<Course> getCoursesByUser(UUID userId) {
        return enrollmentDAO.getCoursesByUser(userId);
    }

    public List<User> getUsersByCourse(UUID courseId) {
        return enrollmentDAO.getUsersByCourse(courseId);
    }

}

package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.CourseDAO;
import com.berghella.daniele.edu_hub.model.Course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CourseService {
    private CourseDAO courseDAO = new CourseDAO();

    public void createCourse(Course course){
        courseDAO.createCourse(course);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }

    public Optional<Course> getCourseById(UUID id) {
        return courseDAO.getCourseById(id);
    }

    public Course updateCourseById(Course courseUpdate, UUID oldCourseId) {
        return courseDAO.updateCourseById(courseUpdate, oldCourseId);
    }

    public boolean isDeletedCourseById(UUID id) {
        return courseDAO.isDeletedCourseById(id);
    }
}

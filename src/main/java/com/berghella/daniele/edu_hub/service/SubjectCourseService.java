package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.SubjectCourseDAO;
import com.berghella.daniele.edu_hub.model.Subject;
import com.berghella.daniele.edu_hub.model.SubjectCourse;

import java.util.List;
import java.util.UUID;

public class SubjectCourseService {
    private final SubjectCourseDAO subjectCourseDAO = new SubjectCourseDAO();

    public List<SubjectCourse> getAllSubjectCourses() {
        return subjectCourseDAO.getAllSubjectCourses();
    }

    public List<Subject> getSubjectsByCourseId(UUID courseId, boolean isEnrolled) {
        return subjectCourseDAO.getSubjectsByCourseId(courseId, isEnrolled);
    }

    public SubjectCourse getSubjectCourseById(UUID id) {
        return subjectCourseDAO.getSubjectCourseById(id).orElse(null);
    }

    public UUID createSubjectCourse(SubjectCourse subjectCourse) {
        return subjectCourseDAO.createSubjectCourse(subjectCourse);
    }

    public SubjectCourse updateSubjectCourse(UUID id, SubjectCourse subjectCourse) {
        return subjectCourseDAO.updateSubjectCourse(id, subjectCourse);
    }

    public boolean deleteSubjectCourseBySubjectAndCourseId(UUID subjectId, UUID courseId) {
        return subjectCourseDAO.deleteSubjectCourseBySubjectAndCourseId(subjectId, courseId);
    }
}

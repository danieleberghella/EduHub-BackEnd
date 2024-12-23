package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.SubjectDAO;
import com.berghella.daniele.edu_hub.model.Subject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SubjectService {
    private SubjectDAO subjectDAO = new SubjectDAO();

    public void createSubject(Subject subject){
        subjectDAO.createSubject(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectDAO.getAllSubjects();
    }

    public Optional<Subject> getSubjectById(UUID id) {
        return subjectDAO.getSubjectById(id);
    }

    public Subject updateSubjectById(Subject subjectUpdate, UUID oldSubjectId) {
        return subjectDAO.updateSubjectById(subjectUpdate, oldSubjectId);
    }

    public boolean isDeletedSubjectById(UUID id) {
        return subjectDAO.isDeletedSubjectById(id);
    }
}

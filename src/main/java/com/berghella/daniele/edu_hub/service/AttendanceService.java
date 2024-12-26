package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.AttendanceDAO;
import com.berghella.daniele.edu_hub.model.AttendanceDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    public UUID markAttendance(UUID userId, UUID courseId, LocalDate attendanceDate, boolean isPresent, UUID subjectId) {
        return attendanceDAO.markAttendance(userId, courseId, attendanceDate, isPresent, subjectId);
    }

    public List<AttendanceDTO> getUserAttendancesByCourseId(UUID studentId, UUID courseId) {
        return attendanceDAO.getUserAttendancesByCourseId(studentId, courseId);
    }

    public List<AttendanceDTO> getUserAttendancesBySubjectId(UUID studentId, UUID subjectId) {
        return attendanceDAO.getUserAttendancesBySubjectId(studentId, subjectId);
    }
}

package com.berghella.daniele.edu_hub.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private UUID id = UUID.randomUUID();
    private UUID studentId;
    private UUID courseId;
    private LocalDate attendanceDate;
    private boolean isPresent;
    private UUID subjectId;

    public AttendanceDTO(UUID studentId, UUID courseId, LocalDate attendanceDate, boolean isPresent, UUID subjectId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.attendanceDate = attendanceDate;
        this.isPresent = isPresent;
        this.subjectId = subjectId;
    }
}

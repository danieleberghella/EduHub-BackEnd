package com.berghella.daniele.edu_hub.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class SubjectCourse {
    private UUID id;
    private UUID subjectId;
    private UUID courseId;

    public SubjectCourse(UUID id, UUID subjectId, UUID courseId) {
        this.id = id;
        this.subjectId = subjectId;
        this.courseId = courseId;
    }
}

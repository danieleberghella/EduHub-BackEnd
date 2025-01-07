package com.berghella.daniele.edu_hub.model;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class SubjectCourseDTO {
    private List<UUID> subjectIds;
    private UUID courseId;
}
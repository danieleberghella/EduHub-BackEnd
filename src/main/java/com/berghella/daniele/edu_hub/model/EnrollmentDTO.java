package com.berghella.daniele.edu_hub.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class EnrollmentDTO {
    private List<UUID> userIds;
    private UUID courseId;
}

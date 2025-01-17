package com.berghella.daniele.edu_hub.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MediaFileDTO {
    private UUID id = UUID.randomUUID();
    private String fileName;
    private Path path;
    private String courseName;
    private String teacherLastName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate uploadDate;

    public MediaFileDTO(String fileName, Path path, String courseName, String teacherLastName, LocalDate uploadDate) {
        this.fileName = fileName;
        this.path = path;
        this.courseName = courseName;
        this.teacherLastName = teacherLastName;
        this.uploadDate = uploadDate;
    }
}

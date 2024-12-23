package com.berghella.daniele.edu_hub.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MediaFile {
    private UUID id = UUID.randomUUID();
    private String fileName;
    private Path path;
    private Subject subject;
    private User teacher;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate uploadDate;

    public MediaFile(String fileName, Path path, Subject subject, User teacher, LocalDate uploadDate) {
        this.fileName = fileName;
        this.path = path;
        this.subject = subject;
        this.teacher = teacher;
        this.uploadDate = uploadDate;
    }
}

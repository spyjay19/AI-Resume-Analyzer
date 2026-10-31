package com.example.airesumeanalyzer.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class Resume {
    private String fileName;

    private String extractedText;

    private LocalDateTime uploadedAt = LocalDateTime.now();

    private String analysis;

}

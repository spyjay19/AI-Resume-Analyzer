package com.example.airesumeanalyzer.Controller;

import com.example.airesumeanalyzer.Entity.Resume;
import com.example.airesumeanalyzer.Service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    @PostMapping
    public ResponseEntity<Resume> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).equals("application/pdf")) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resumeService.processResume(file));
    }
}

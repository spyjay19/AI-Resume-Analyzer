package com.example.airesumeanalyzer.Controller;

import com.example.airesumeanalyzer.Entity.Resume;
import com.example.airesumeanalyzer.Repository.ResumeRepository;
import com.example.airesumeanalyzer.Service.AnalysisService;
import com.example.airesumeanalyzer.Service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;
    private final AnalysisService analysisService;
    private final ResumeRepository resumeRepository;

    @PostMapping
    public ResponseEntity<Resume> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String contentType = file.getContentType();

        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resumeService.processResume(file));
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<String> analyze(@PathVariable String id) throws IOException {
        Resume resume = resumeRepository.findById(id).orElse(null);

        if (resume == null) {
            return ResponseEntity.notFound().build();
        }

        String analysis = analysisService.analyzeResume(resume.getExtractedText());
        return ResponseEntity.ok(analysis);
    }
}

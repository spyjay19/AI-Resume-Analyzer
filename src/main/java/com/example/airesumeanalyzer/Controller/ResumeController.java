package com.example.airesumeanalyzer.Controller;

import com.example.airesumeanalyzer.Entity.AnalysisResult;
import com.example.airesumeanalyzer.Entity.Resume;
import com.example.airesumeanalyzer.Repository.ResumeRepository;
import com.example.airesumeanalyzer.Service.AnalysisService;
import com.example.airesumeanalyzer.Service.ResumeService;
import jakarta.transaction.Transactional;
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

    @Transactional
    @PostMapping("/{id}/analyze")
    public ResponseEntity<AnalysisResult> analyze(@PathVariable String id) throws IOException {
        Resume resume = resumeRepository.findByIdNative(id).orElse(null);

        if (resume == null) {
            return ResponseEntity.notFound().build();
        }

        if (resume.getAnalysis() != null && !resume.getAnalysis().isEmpty()) {
            AnalysisResult analysisResult = new AnalysisResult(resume.getAnalysis(), true);
            return ResponseEntity.ok(analysisResult);
        }

        String analysis = analysisService.analyzeResume(resume.getExtractedText());
        resume.setAnalysis(analysis);
        resumeRepository.save(resume);

        AnalysisResult analysisResult = new AnalysisResult(resume.getAnalysis(), false);

        return ResponseEntity.ok(analysisResult);
    }
}

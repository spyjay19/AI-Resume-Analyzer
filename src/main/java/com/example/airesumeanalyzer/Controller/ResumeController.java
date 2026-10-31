package com.example.airesumeanalyzer.Controller;

import com.example.airesumeanalyzer.Entity.Resume;
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

    @PostMapping("/analyze")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String contentType = file.getContentType();

        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.badRequest().build();
        }

        String extractedText = resumeService.extractText(file);
        String analysis = analysisService.analyzeResume(extractedText);
        return ResponseEntity.ok(analysis);
    }
}

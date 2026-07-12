package com.example.airesumeanalyzer.Service;

import com.example.airesumeanalyzer.Entity.Resume;
import com.example.airesumeanalyzer.Repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;

    public Resume processResume(MultipartFile file) throws IOException{
        String text;

        try (PDDocument doc = Loader.loadPDF(file.getBytes())){
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(doc);
        }

        Resume resume = new Resume();
        resume.setFileName(file.getOriginalFilename());
        resume.setExtractedText(text);
        return resumeRepository.save(resume);
    }
}

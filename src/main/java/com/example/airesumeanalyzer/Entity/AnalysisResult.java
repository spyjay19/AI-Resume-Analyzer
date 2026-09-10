package com.example.airesumeanalyzer.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AnalysisResult {
    private String analysisText;
    private boolean cached;
}

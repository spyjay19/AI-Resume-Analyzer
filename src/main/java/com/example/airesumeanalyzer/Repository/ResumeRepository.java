package com.example.airesumeanalyzer.Repository;

import com.example.airesumeanalyzer.Entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, String> {}

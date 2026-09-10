package com.example.airesumeanalyzer.Repository;

import com.example.airesumeanalyzer.Entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, String> {
    @Query(value = "SELECT * from resume Where id = :id", nativeQuery = true)
    Optional<Resume> findByIdNative(@Param("id") String id);
}
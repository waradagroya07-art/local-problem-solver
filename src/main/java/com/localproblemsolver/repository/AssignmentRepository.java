package com.localproblemsolver.repository;

import com.localproblemsolver.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssignmentRepository
        extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findByProblemId(Long problemId);
}
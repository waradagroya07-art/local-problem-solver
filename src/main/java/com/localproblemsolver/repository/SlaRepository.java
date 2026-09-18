package com.localproblemsolver.repository;

import com.localproblemsolver.entity.Sla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlaRepository extends JpaRepository<Sla, Long> {

    Optional<Sla> findByProblemId(Long problemId);
}
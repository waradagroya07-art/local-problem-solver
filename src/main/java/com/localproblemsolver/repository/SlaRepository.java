package com.localproblemsolver.repository;

import com.localproblemsolver.entity.Sla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SlaRepository extends JpaRepository<Sla, Long> {

    Optional<Sla> findByProblemId(Long problemId);

    long countByBreachedTrue();

    @Query("""
        SELECT COUNT(s.id)
        FROM Sla s
        JOIN s.problem p
        JOIN Assignment a ON a.problem.id = p.id
        WHERE a.authority.id = :authorityId
          AND s.breached = true
    """)
    long countBreachedSlasByAuthority(
            @Param("authorityId") Long authorityId
    );
}
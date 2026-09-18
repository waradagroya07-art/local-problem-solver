package com.localproblemsolver.service;

import com.localproblemsolver.dto.SlaResponse;
import com.localproblemsolver.entity.Priority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.Sla;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.SlaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SlaService {

    private final SlaRepository slaRepository;
    private final ProblemRepository problemRepository;

    public SlaService(
            SlaRepository slaRepository,
            ProblemRepository problemRepository) {

        this.slaRepository = slaRepository;
        this.problemRepository = problemRepository;
    }

    @Transactional
    public SlaResponse createSla(Long problemId) {

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Problem not found with id: " + problemId
                        )
                );

        if (problem.getPriority() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Problem must have a priority before SLA creation"
            );
        }

        if (slaRepository.findByProblemId(problemId).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "SLA already exists for this problem"
            );
        }

        LocalDateTime deadline =
                calculateDeadline(
                        problem.getCreatedAt(),
                        problem.getPriority()
                );

        Sla sla = new Sla();

        sla.setProblem(problem);
        sla.setDeadline(deadline);
        sla.setBreached(false);

        Sla savedSla = slaRepository.save(sla);

        return convertToResponse(savedSla);
    }

    public SlaResponse getSla(Long problemId) {

        Sla sla = slaRepository.findByProblemId(problemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "SLA not found for problem id: "
                                        + problemId
                        )
                );

        updateBreachStatus(sla);

        return convertToResponse(sla);
    }

    public List<SlaResponse> getAllSlas() {

        return slaRepository.findAll()
                .stream()
                .map(sla -> {
                    updateBreachStatus(sla);
                    return convertToResponse(sla);
                })
                .toList();
    }

    private LocalDateTime calculateDeadline(
            LocalDateTime createdAt,
            Priority priority) {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        return switch (priority) {

            case CRITICAL ->
                    createdAt.plusHours(24);

            case HIGH ->
                    createdAt.plusHours(48);

            case MEDIUM ->
                    createdAt.plusHours(72);

            case LOW ->
                    createdAt.plusHours(168);
        };
    }

    private void updateBreachStatus(Sla sla) {

        if (!sla.isBreached()
                && LocalDateTime.now()
                .isAfter(sla.getDeadline())) {

            sla.setBreached(true);
            slaRepository.save(sla);
        }
    }

    private SlaResponse convertToResponse(Sla sla) {

        return new SlaResponse(
                sla.getId(),
                sla.getProblem().getId(),
                sla.getProblem().getPriority().name(),
                sla.getDeadline(),
                sla.isBreached()
        );
    }
}
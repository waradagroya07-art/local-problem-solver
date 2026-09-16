package com.localproblemsolver.service;

import com.localproblemsolver.entity.Priority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.Severity;
import com.localproblemsolver.repository.ProblemRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PriorityCalculationService {

    private final ProblemRepository problemRepository;

    public PriorityCalculationService(
            ProblemRepository problemRepository) {

        this.problemRepository = problemRepository;
    }

    public Priority calculatePriority(Problem problem) {

        /*
         * Critical severity represents an urgent problem.
         * Therefore, it gets Critical priority directly.
         */
        if (problem.getSeverity() == Severity.CRITICAL) {
            return Priority.CRITICAL;
        }

        int score = 0;

        // 1. Severity
        score += calculateSeverityScore(problem.getSeverity());

        // 2. Number of similar reports
        score += calculateReportCountScore(problem);

        // 3. Age of problem
        score += calculateAgeScore(problem);

        // 4. Category
        score += calculateCategoryScore(problem);

        return convertScoreToPriority(score);
    }

    private int calculateSeverityScore(Severity severity) {

        if (severity == null) {
            return 0;
        }

        return switch (severity) {
            case LOW -> 10;
            case MEDIUM -> 25;
            case HIGH -> 40;
            case CRITICAL -> 60;
        };
    }

    private int calculateReportCountScore(Problem problem) {

        List<Problem> problems = problemRepository.findAll();

        String title = normalize(problem.getTitle());

        long similarReports = problems.stream()
                .filter(existingProblem ->
                        existingProblem.getId() != null
                                && problem.getId() != null
                                && !existingProblem.getId()
                                .equals(problem.getId()))
                .filter(existingProblem ->
                        normalize(existingProblem.getTitle())
                                .equals(title))
                .count();

        if (similarReports >= 5) {
            return 20;
        }

        if (similarReports >= 3) {
            return 15;
        }

        if (similarReports >= 1) {
            return 10;
        }

        return 0;
    }

    private int calculateAgeScore(Problem problem) {

        if (problem.getCreatedAt() == null) {
            return 0;
        }

        long hours = Duration.between(
                problem.getCreatedAt(),
                LocalDateTime.now()
        ).toHours();

        if (hours >= 168) {
            return 10;
        }

        if (hours >= 72) {
            return 7;
        }

        if (hours >= 24) {
            return 5;
        }

        return 0;
    }

    private int calculateCategoryScore(Problem problem) {

        if (problem.getCategory() == null) {
            return 0;
        }

        return 10;
    }

    private Priority convertScoreToPriority(int score) {

        if (score >= 80) {
            return Priority.CRITICAL;
        }

        if (score >= 60) {
            return Priority.HIGH;
        }

        if (score >= 30) {
            return Priority.MEDIUM;
        }

        return Priority.LOW;
    }

    private String normalize(String text) {

        if (text == null) {
            return "";
        }

        return text
                .toLowerCase()
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
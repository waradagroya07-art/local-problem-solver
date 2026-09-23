package com.localproblemsolver.service;

import com.localproblemsolver.entity.Category;
import com.localproblemsolver.entity.Priority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.Severity;
import com.localproblemsolver.repository.ProblemRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PriorityCalculationServiceTest {

    private ProblemRepository problemRepository;
    private PriorityCalculationService priorityCalculationService;

    @BeforeEach
    void setUp() {

        problemRepository = mock(ProblemRepository.class);

        priorityCalculationService =
                new PriorityCalculationService(problemRepository);
    }

    // ============================================================
    // 1. CRITICAL SEVERITY
    // ============================================================

    @Test
    void testCriticalSeverity() {

        Problem problem = new Problem();

        problem.setSeverity(Severity.CRITICAL);

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.CRITICAL, result);
    }

    // ============================================================
    // 2. LOW SEVERITY
    // ============================================================

    @Test
    void testLowSeverity() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);
        problem.setCreatedAt(LocalDateTime.now());

        when(problemRepository.findAll())
                .thenReturn(List.of());

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 3. ONE SIMILAR REPORT
    // ============================================================

    @Test
    void testOneSimilarReport() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);
        problem.setCreatedAt(LocalDateTime.now());

        Problem similarProblem = new Problem();

        similarProblem.setId(2L);
        similarProblem.setTitle("Pothole");

        when(problemRepository.findAll())
                .thenReturn(List.of(similarProblem));

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 4. THREE SIMILAR REPORTS
    // ============================================================

    @Test
    void testThreeSimilarReports() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);
        problem.setCreatedAt(LocalDateTime.now());

        Problem problem2 = new Problem();
        problem2.setId(2L);
        problem2.setTitle("Pothole");

        Problem problem3 = new Problem();
        problem3.setId(3L);
        problem3.setTitle("Pothole");

        Problem problem4 = new Problem();
        problem4.setId(4L);
        problem4.setTitle("Pothole");

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        problem2,
                        problem3,
                        problem4
                ));

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 5. FIVE SIMILAR REPORTS
    // ============================================================

    @Test
    void testFiveSimilarReports() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);
        problem.setCreatedAt(LocalDateTime.now());

        Problem problem2 = createProblem(2L, "Pothole");
        Problem problem3 = createProblem(3L, "Pothole");
        Problem problem4 = createProblem(4L, "Pothole");
        Problem problem5 = createProblem(5L, "Pothole");
        Problem problem6 = createProblem(6L, "Pothole");

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        problem2,
                        problem3,
                        problem4,
                        problem5,
                        problem6
                ));

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.MEDIUM, result);
    }

    // ============================================================
    // 6. AGE = 24 HOURS
    // ============================================================

    @Test
    void testProblemAge24Hours() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);

        problem.setCreatedAt(
                LocalDateTime.now().minusHours(24)
        );

        when(problemRepository.findAll())
                .thenReturn(List.of());

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 7. AGE = 72 HOURS
    // ============================================================

    @Test
    void testProblemAge72Hours() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);

        problem.setCreatedAt(
                LocalDateTime.now().minusHours(72)
        );

        when(problemRepository.findAll())
                .thenReturn(List.of());

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 8. AGE = 168 HOURS
    // ============================================================

    @Test
    void testProblemAge168Hours() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);

        problem.setCreatedAt(
                LocalDateTime.now().minusHours(168)
        );

        when(problemRepository.findAll())
                .thenReturn(List.of());

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 9. CATEGORY EXISTS
    // ============================================================

    @Test
    void testCategoryExists() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);
        problem.setCreatedAt(LocalDateTime.now());

        problem.setCategory(new Category());

        when(problemRepository.findAll())
                .thenReturn(List.of());

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 10. NULL SEVERITY
    // ============================================================

    @Test
    void testNullSeverity() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(null);
        problem.setCreatedAt(LocalDateTime.now());

        when(problemRepository.findAll())
                .thenReturn(List.of());

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 11. MEDIUM SEVERITY
    // ============================================================

    @Test
    void testMediumSeverity() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.MEDIUM);
        problem.setCreatedAt(LocalDateTime.now());

        when(problemRepository.findAll())
                .thenReturn(List.of());

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 12. HIGH SEVERITY
    // ============================================================

    @Test
    void testHighSeverity() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.HIGH);
        problem.setCreatedAt(LocalDateTime.now());

        when(problemRepository.findAll())
                .thenReturn(List.of());

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.MEDIUM, result);
    }

    // ============================================================
    // 13. TWO SIMILAR REPORTS
    // ============================================================

    @Test
    void testTwoSimilarReports() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);
        problem.setCreatedAt(LocalDateTime.now());

        Problem problem2 = createProblem(2L, "Pothole");
        Problem problem3 = createProblem(3L, "Pothole");

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        problem2,
                        problem3
                ));

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 14. FOUR SIMILAR REPORTS
    // ============================================================

    @Test
    void testFourSimilarReports() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);
        problem.setCreatedAt(LocalDateTime.now());

        Problem problem2 = createProblem(2L, "Pothole");
        Problem problem3 = createProblem(3L, "Pothole");
        Problem problem4 = createProblem(4L, "Pothole");
        Problem problem5 = createProblem(5L, "Pothole");

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        problem2,
                        problem3,
                        problem4,
                        problem5
                ));

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 15. AGE LESS THAN 24 HOURS
    // ============================================================

    @Test
    void testProblemAgeLessThan24Hours() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);

        problem.setCreatedAt(
                LocalDateTime.now().minusHours(5)
        );

        when(problemRepository.findAll())
                .thenReturn(List.of());

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 16. NULL CREATED AT
    // ============================================================

    @Test
    void testNullCreatedAt() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);
        problem.setCreatedAt(null);

        when(problemRepository.findAll())
                .thenReturn(List.of());

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 17. DIFFERENT TITLES ARE NOT SIMILAR
    // ============================================================

    @Test
    void testDifferentTitlesAreNotCountedAsSimilar() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);
        problem.setCreatedAt(LocalDateTime.now());

        Problem differentProblem =
                createProblem(2L, "Garbage");

        when(problemRepository.findAll())
                .thenReturn(List.of(differentProblem));

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 18. SAME TITLE WITH DIFFERENT CASE
    // ============================================================

    @Test
    void testTitleNormalization() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);
        problem.setCreatedAt(LocalDateTime.now());

        Problem similarProblem =
                createProblem(2L, "POTHOLE");

        when(problemRepository.findAll())
                .thenReturn(List.of(similarProblem));

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.LOW, result);
    }

    // ============================================================
    // 19. SCORE REACHES MEDIUM THRESHOLD
    // ============================================================

    @Test
    void testMediumPriorityThreshold() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.LOW);
        problem.setCreatedAt(
                LocalDateTime.now().minusHours(24)
        );

        Problem problem2 = createProblem(2L, "Pothole");
        Problem problem3 = createProblem(3L, "Pothole");
        Problem problem4 = createProblem(4L, "Pothole");

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        problem2,
                        problem3,
                        problem4
                ));

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.MEDIUM, result);
    }

    // ============================================================
    // 20. SCORE REACHES HIGH THRESHOLD
    // ============================================================

    @Test
    void testHighPriorityThreshold() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.HIGH);
        problem.setCreatedAt(
                LocalDateTime.now().minusHours(24)
        );

        Problem problem2 = createProblem(2L, "Pothole");
        Problem problem3 = createProblem(3L, "Pothole");
        Problem problem4 = createProblem(4L, "Pothole");
        Problem problem5 = createProblem(5L, "Pothole");
        Problem problem6 = createProblem(6L, "Pothole");

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        problem2,
                        problem3,
                        problem4,
                        problem5,
                        problem6
                ));

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.HIGH, result);
    }

    // ============================================================
    // 21. SCORE REACHES CRITICAL THRESHOLD
    // ============================================================

    @Test
    void testCriticalPriorityThreshold() {

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setTitle("Pothole");
        problem.setSeverity(Severity.HIGH);
        problem.setCreatedAt(
                LocalDateTime.now().minusHours(168)
        );

        problem.setCategory(new Category());

        Problem problem2 = createProblem(2L, "Pothole");
        Problem problem3 = createProblem(3L, "Pothole");
        Problem problem4 = createProblem(4L, "Pothole");
        Problem problem5 = createProblem(5L, "Pothole");
        Problem problem6 = createProblem(6L, "Pothole");

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        problem2,
                        problem3,
                        problem4,
                        problem5,
                        problem6
                ));

        Priority result =
                priorityCalculationService.calculatePriority(problem);

        assertEquals(Priority.CRITICAL, result);
    }

    // ============================================================
    // HELPER METHOD
    // ============================================================

    private Problem createProblem(Long id, String title) {

        Problem problem = new Problem();

        problem.setId(id);
        problem.setTitle(title);

        return problem;
    }
}
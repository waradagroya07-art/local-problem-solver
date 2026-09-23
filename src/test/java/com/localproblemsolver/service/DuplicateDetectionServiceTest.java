package com.localproblemsolver.service;

import com.localproblemsolver.entity.Category;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.repository.ProblemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DuplicateDetectionServiceTest {

    private ProblemRepository problemRepository;
    private DuplicateDetectionService duplicateDetectionService;

    @BeforeEach
    void setUp() {

        problemRepository = mock(ProblemRepository.class);

        duplicateDetectionService =
                new DuplicateDetectionService(problemRepository);
    }

    // ============================================================
    // 1. NO DUPLICATES
    // ============================================================

    @Test
    void testNoDuplicatesFound() {

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        null,
                        20.0000,
                        73.0000,
                        LocalDateTime.now()
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(currentProblem));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(0, result.size());
    }

    // ============================================================
    // 2. EXACT MATCH - DUPLICATE FOUND
    // ============================================================

    @Test
    void testExactMatchingProblemIsDuplicate() {

        LocalDateTime now = LocalDateTime.now();

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        null,
                        20.0000,
                        73.0000,
                        now
                );

        Problem matchingProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        null,
                        20.0000,
                        73.0000,
                        now
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        matchingProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(1, result.size());
    }

    // ============================================================
    // 3. SAME TEXT + SAME CATEGORY = 0.70
    // ============================================================

    @Test
    void testDuplicateAtExactThreshold() {

        LocalDateTime now = LocalDateTime.now();

        Category category =
                createCategory(1L);

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        null,
                        null,
                        now
                );

        Problem matchingProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        null,
                        null,
                        now
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        matchingProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(1, result.size());
    }

    // ============================================================
    // 4. DIFFERENT CATEGORY
    // ============================================================

    @Test
    void testDifferentCategoryIsNotDuplicate() {

        LocalDateTime now = LocalDateTime.now();

        Category category1 =
                createCategory(1L);

        Category category2 =
                createCategory(2L);

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        category1,
                        null,
                        null,
                        now
                );

        Problem differentCategoryProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        category2,
                        null,
                        null,
                        now
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        differentCategoryProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(0, result.size());
    }

    // ============================================================
    // 5. DIFFERENT TEXT
    // ============================================================

    @Test
    void testDifferentTextIsNotDuplicate() {

        LocalDateTime now = LocalDateTime.now();

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        null,
                        null,
                        null,
                        now
                );

        Problem differentProblem =
                createProblem(
                        2L,
                        "Garbage",
                        "Garbage collection issue",
                        null,
                        null,
                        null,
                        now
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        differentProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(0, result.size());
    }

    // ============================================================
    // 6. SAME LOCATION
    // ============================================================

    @Test
    void testSameLocationContributesToDuplicateScore() {

        LocalDateTime now = LocalDateTime.now();

        Category category =
                createCategory(1L);

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        20.0000,
                        73.0000,
                        now
                );

        Problem matchingProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        20.0000,
                        73.0000,
                        now
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        matchingProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(1, result.size());
    }

    // ============================================================
    // 7. LOCATION MORE THAN 500 METERS
    // ============================================================

    @Test
    void testLocationBeyond500Meters() {

        LocalDateTime now = LocalDateTime.now();

        Category category =
                createCategory(1L);

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        20.0000,
                        73.0000,
                        now
                );

        Problem farProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        20.0100,
                        73.0000,
                        now
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        farProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(1, result.size());
    }

    // ============================================================
    // 8. TIME WITHIN 7 DAYS
    // ============================================================

    @Test
    void testProblemWithinSevenDays() {

        Category category =
                createCategory(1L);

        LocalDateTime now =
                LocalDateTime.now();

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        null,
                        null,
                        now
                );

        Problem recentProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        null,
                        null,
                        now.minusDays(3)
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        recentProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(1, result.size());
    }

    // ============================================================
    // 9. TIME BEYOND 7 DAYS
    // ============================================================

    @Test
    void testProblemBeyondSevenDays() {

        Category category =
                createCategory(1L);

        LocalDateTime now =
                LocalDateTime.now();

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        null,
                        null,
                        now
                );

        Problem oldProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        null,
                        null,
                        now.minusDays(10)
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        oldProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(1, result.size());
    }

    // ============================================================
    // 10. REJECTED PROBLEM IS IGNORED
    // ============================================================

    @Test
    void testRejectedProblemIsIgnored() {

        LocalDateTime now =
                LocalDateTime.now();

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        null,
                        20.0000,
                        73.0000,
                        now
                );

        Problem rejectedProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        null,
                        20.0000,
                        73.0000,
                        now
                );

        rejectedProblem.setStatus(
                ProblemStatus.REJECTED
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        rejectedProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(0, result.size());
    }

    // ============================================================
    // 11. DUPLICATE PROBLEM IS IGNORED
    // ============================================================

    @Test
    void testAlreadyDuplicateProblemIsIgnored() {

        LocalDateTime now =
                LocalDateTime.now();

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        null,
                        20.0000,
                        73.0000,
                        now
                );

        Problem duplicateProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        null,
                        20.0000,
                        73.0000,
                        now
                );

        duplicateProblem.setStatus(
                ProblemStatus.DUPLICATE
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        duplicateProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(0, result.size());
    }

    // ============================================================
    // 12. CURRENT PROBLEM IS IGNORED
    // ============================================================

    @Test
    void testCurrentProblemIsIgnored() {

        LocalDateTime now =
                LocalDateTime.now();

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        null,
                        20.0000,
                        73.0000,
                        now
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(currentProblem));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(0, result.size());
    }

    // ============================================================
    // 13. NULL CATEGORY
    // ============================================================

    @Test
    void testNullCategoryDoesNotCauseDuplicateByCategory() {

        LocalDateTime now =
                LocalDateTime.now();

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        null,
                        null,
                        null,
                        now
                );

        Problem otherProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        null,
                        null,
                        null,
                        now
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        otherProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        /*
         * Text = 1.0 × 0.50 = 0.50
         * Category = 0.0
         * Location = 0.0
         * Time = 0.0
         *
         * Final = 0.50
         *
         * Threshold = 0.70
         */
        assertEquals(0, result.size());
    }

    // ============================================================
    // 14. NULL LOCATION
    // ============================================================

    @Test
    void testNullLocationDoesNotCauseDuplicateByLocation() {

        LocalDateTime now =
                LocalDateTime.now();

        Category category =
                createCategory(1L);

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        null,
                        null,
                        now
                );

        Problem otherProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        null,
                        null,
                        now
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        otherProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        /*
         * Text = 0.50
         * Category = 0.20
         * Location = 0
         * Time = 0
         *
         * Final = 0.70
         *
         * It still reaches the duplicate threshold.
         */
        assertEquals(1, result.size());
    }

    // ============================================================
    // 15. NULL CREATED AT
    // ============================================================

    @Test
    void testNullCreatedAtDoesNotCauseException() {

        Category category =
                createCategory(1L);

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        null,
                        null,
                        null
                );

        Problem otherProblem =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        null,
                        null,
                        null
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        otherProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        /*
         * Text = 0.50
         * Category = 0.20
         * Location = 0
         * Time = 0
         *
         * Final = 0.70
         */
        assertEquals(1, result.size());
    }

    // ============================================================
    // 16. SYNONYM MATCH
    // ============================================================

    @Test
    void testPotholeSynonymMatch() {

        LocalDateTime now =
                LocalDateTime.now();

        Category category =
                createCategory(1L);

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Road damage reported",
                        category,
                        20.0000,
                        73.0000,
                        now
                );

        Problem similarProblem =
                createProblem(
                        2L,
                        "Road Hole",
                        "Road damage reported",
                        category,
                        20.0000,
                        73.0000,
                        now
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        similarProblem
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(1, result.size());
    }

    // ============================================================
    // 17. MULTIPLE DUPLICATES
    // ============================================================

    @Test
    void testMultipleDuplicatesFound() {

        LocalDateTime now =
                LocalDateTime.now();

        Category category =
                createCategory(1L);

        Problem currentProblem =
                createProblem(
                        1L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        20.0000,
                        73.0000,
                        now
                );

        Problem problem2 =
                createProblem(
                        2L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        20.0000,
                        73.0000,
                        now
                );

        Problem problem3 =
                createProblem(
                        3L,
                        "Pothole",
                        "Large pothole on road",
                        category,
                        20.0001,
                        73.0001,
                        now
                );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(currentProblem));

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        currentProblem,
                        problem2,
                        problem3
                ));

        List<?> result =
                duplicateDetectionService.findDuplicates(1L);

        assertEquals(2, result.size());
    }

    // ============================================================
    // 18. PROBLEM NOT FOUND
    // ============================================================

    @Test
    void testProblemNotFound() {

        when(problemRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                Exception.class,
                () -> duplicateDetectionService.findDuplicates(999L)
        );
    }

    // ============================================================
    // HELPER: CREATE PROBLEM
    // ============================================================

    private Problem createProblem(
            Long id,
            String title,
            String description,
            Category category,
            Double latitude,
            Double longitude,
            LocalDateTime createdAt) {

        Problem problem = new Problem();

        problem.setId(id);
        problem.setTitle(title);
        problem.setDescription(description);
        problem.setCategory(category);
        problem.setLatitude(latitude);
        problem.setLongitude(longitude);
        problem.setCreatedAt(createdAt);

        return problem;
    }

    // ============================================================
    // HELPER: CREATE CATEGORY
    // ============================================================

    private Category createCategory(Long id) {

        Category category =
                mock(Category.class);

        when(category.getId())
                .thenReturn(id);

        return category;
    }
}
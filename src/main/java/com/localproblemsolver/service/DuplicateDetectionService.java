package com.localproblemsolver.service;

import com.localproblemsolver.dto.DuplicateResponse;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.exception.ProblemNotFoundException;
import com.localproblemsolver.repository.ProblemRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DuplicateDetectionService {

    private final ProblemRepository problemRepository;

    private static final double DUPLICATE_THRESHOLD = 0.70;

    private static final double TEXT_WEIGHT = 0.50;
    private static final double CATEGORY_WEIGHT = 0.20;
    private static final double LOCATION_WEIGHT = 0.20;
    private static final double TIME_WEIGHT = 0.10;

    private static final double MAX_DISTANCE_METERS = 500.0;
    private static final long TIME_WINDOW_DAYS = 7;

    private static final Map<String, Set<String>> SYNONYMS = Map.of(
            "pothole", Set.of("pothole", "road hole", "road damage"),
            "garbage", Set.of("garbage", "waste", "trash", "rubbish"),
            "streetlight", Set.of("streetlight", "street light", "lamp"),
            "road", Set.of("road", "street")
    );

    public DuplicateDetectionService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    public List<DuplicateResponse> findDuplicates(Long problemId) {

        Problem currentProblem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Problem not found with id: " + problemId
                        )
                );

        List<Problem> existingProblems = problemRepository.findAll();

        List<DuplicateResponse> duplicates = new ArrayList<>();

        for (Problem otherProblem : existingProblems) {

            if (otherProblem.getId().equals(problemId)) {
                continue;
            }

            if (otherProblem.getStatus() == ProblemStatus.REJECTED
                    || otherProblem.getStatus() == ProblemStatus.DUPLICATE) {
                continue;
            }

            double textScore = calculateTextSimilarity(
                    currentProblem,
                    otherProblem
            );

            double categoryScore = calculateCategorySimilarity(
                    currentProblem,
                    otherProblem
            );

            double locationScore = calculateLocationSimilarity(
                    currentProblem,
                    otherProblem
            );

            double timeScore = calculateTimeSimilarity(
                    currentProblem,
                    otherProblem
            );

            double finalScore =
                    (textScore * TEXT_WEIGHT)
                            + (categoryScore * CATEGORY_WEIGHT)
                            + (locationScore * LOCATION_WEIGHT)
                            + (timeScore * TIME_WEIGHT);

            if (finalScore >= DUPLICATE_THRESHOLD) {

                String reason = buildReason(
                        textScore,
                        categoryScore,
                        locationScore,
                        timeScore
                );

                duplicates.add(
                        new DuplicateResponse(
                                otherProblem.getId(),
                                otherProblem.getTitle(),
                                otherProblem.getLocation(),
                                Math.round(finalScore * 100.0) / 100.0,
                                reason
                        )
                );
            }
        }

        return duplicates;
    }

    private double calculateTextSimilarity(
            Problem first,
            Problem second) {

        String firstText = normalize(
                first.getTitle() + " " + first.getDescription()
        );

        String secondText = normalize(
                second.getTitle() + " " + second.getDescription()
        );

        if (firstText.isEmpty() || secondText.isEmpty()) {
            return 0.0;
        }

        Set<String> firstWords = extractKeywords(firstText);
        Set<String> secondWords = extractKeywords(secondText);

        if (firstWords.isEmpty() || secondWords.isEmpty()) {
            return 0.0;
        }

        int commonWords = 0;

        for (String word : firstWords) {
            if (secondWords.contains(word)
                    || hasSynonymMatch(word, secondWords)) {
                commonWords++;
            }
        }

        int totalWords = Math.max(
                firstWords.size(),
                secondWords.size()
        );

        return (double) commonWords / totalWords;
    }

    private double calculateCategorySimilarity(
            Problem first,
            Problem second) {

        if (first.getCategory() == null
                || second.getCategory() == null) {
            return 0.0;
        }

        if (first.getCategory().getId()
                .equals(second.getCategory().getId())) {
            return 1.0;
        }

        return 0.0;
    }

    private double calculateLocationSimilarity(
            Problem first,
            Problem second) {

        if (first.getLatitude() == null
                || first.getLongitude() == null
                || second.getLatitude() == null
                || second.getLongitude() == null) {
            return 0.0;
        }

        double distance = calculateDistance(
                first.getLatitude(),
                first.getLongitude(),
                second.getLatitude(),
                second.getLongitude()
        );

        if (distance <= MAX_DISTANCE_METERS) {
            return 1.0 - (distance / MAX_DISTANCE_METERS);
        }

        return 0.0;
    }

    private double calculateTimeSimilarity(
            Problem first,
            Problem second) {

        if (first.getCreatedAt() == null
                || second.getCreatedAt() == null) {
            return 0.0;
        }

        long days = Math.abs(
                Duration.between(
                        first.getCreatedAt(),
                        second.getCreatedAt()
                ).toDays()
        );

        if (days <= TIME_WINDOW_DAYS) {
            return 1.0 - ((double) days / TIME_WINDOW_DAYS);
        }

        return 0.0;
    }

    private double calculateDistance(
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2) {

        final double EARTH_RADIUS = 6371000;

        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);

        double deltaLat =
                Math.toRadians(latitude2 - latitude1);

        double deltaLon =
                Math.toRadians(longitude2 - longitude1);

        double a =
                Math.sin(deltaLat / 2)
                        * Math.sin(deltaLat / 2)
                        + Math.cos(lat1)
                        * Math.cos(lat2)
                        * Math.sin(deltaLon / 2)
                        * Math.sin(deltaLon / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return EARTH_RADIUS * c;
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

    private Set<String> extractKeywords(String text) {

        Set<String> keywords = new java.util.HashSet<>();

        for (String word : text.split(" ")) {

            if (word.length() > 2) {
                keywords.add(word);
            }
        }

        return keywords;
    }

    private boolean hasSynonymMatch(
            String word,
            Set<String> otherWords) {

        for (Map.Entry<String, Set<String>> entry
                : SYNONYMS.entrySet()) {

            if (entry.getValue().contains(word)) {

                for (String synonym : entry.getValue()) {

                    if (otherWords.contains(synonym)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private String buildReason(
            double textScore,
            double categoryScore,
            double locationScore,
            double timeScore) {

        List<String> reasons = new ArrayList<>();

        if (textScore >= 0.70) {
            reasons.add("similar description");
        }

        if (categoryScore == 1.0) {
            reasons.add("same category");
        }

        if (locationScore > 0.0) {
            reasons.add("nearby location");
        }

        if (timeScore > 0.0) {
            reasons.add("reported within time window");
        }

        return String.join(", ", reasons);
    }
}
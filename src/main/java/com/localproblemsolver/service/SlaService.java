package com.localproblemsolver.service;

import com.localproblemsolver.dto.SlaResponse;
import com.localproblemsolver.entity.Assignment;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.entity.Priority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.Sla;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.SlaRepository;
import com.localproblemsolver.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SlaService {

    private final SlaRepository slaRepository;
    private final ProblemRepository problemRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;

    public SlaService(
            SlaRepository slaRepository,
            ProblemRepository problemRepository,
            NotificationService notificationService,
            UserRepository userRepository,
            AssignmentRepository assignmentRepository) {

        this.slaRepository = slaRepository;
        this.problemRepository = problemRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
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

    public SlaResponse getSla(
            Long problemId,
            String userEmail) {

        Sla sla = slaRepository.findByProblemId(problemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "SLA not found for problem id: " + problemId
                        )
                );

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found."
                        )
                );

        validateAccess(sla.getProblem(), user);

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

    private void validateAccess(
            Problem problem,
            User user) {

        Role role = user.getRole();

        if (role == Role.SUPER_ADMIN) {
            return;
        }

        if (role == Role.MODERATOR) {
            return;
        }

        if (role == Role.CITIZEN) {

            if (!problem.getUser().getId()
                    .equals(user.getId())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not allowed to view this SLA."
                );
            }

            return;
        }

        if (role == Role.AUTHORITY) {

            if (user.getAuthority() == null) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Authority is not assigned to this user."
                );
            }

            Assignment assignment =
                    assignmentRepository
                            .findByProblemId(problem.getId())
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.FORBIDDEN,
                                            "This problem is not assigned to you."
                                    )
                            );

            if (!assignment.getAuthority().getId()
                    .equals(user.getAuthority().getId())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not allowed to view this SLA."
                );
            }

            return;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You are not allowed to view this SLA."
        );
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

    /**
     * Automatically checks all SLAs every 60 seconds.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAllSlaBreaches() {

        List<Sla> slas = slaRepository.findAll();

        for (Sla sla : slas) {
            updateBreachStatus(sla);
        }
    }

    @Transactional
    private void updateBreachStatus(Sla sla) {

        if (!sla.isBreached()
                && LocalDateTime.now().isAfter(sla.getDeadline())) {

            sla.setBreached(true);

            slaRepository.save(sla);

            if (sla.getProblem().getUser() != null
                    && sla.getProblem().getUser().getEmail() != null) {

                notificationService.createNotification(
                        "The SLA for your problem #"
                                + sla.getProblem().getId()
                                + " has been breached.",
                        NotificationType.SLA_BREACHED,
                        sla.getProblem().getUser().getEmail()
                );
            }
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
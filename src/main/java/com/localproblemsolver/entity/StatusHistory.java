package com.localproblemsolver.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "status_history")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemStatus newStatus;

    @ManyToOne
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    public StatusHistory() {
    }

    public StatusHistory(
            Problem problem,
            ProblemStatus oldStatus,
            ProblemStatus newStatus,
            User changedBy,
            LocalDateTime changedAt) {

        this.problem = problem;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public ProblemStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(ProblemStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public ProblemStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ProblemStatus newStatus) {
        this.newStatus = newStatus;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
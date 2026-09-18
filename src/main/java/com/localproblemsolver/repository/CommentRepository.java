package com.localproblemsolver.repository;

import com.localproblemsolver.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByProblemId(Long problemId);
}
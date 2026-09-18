package com.localproblemsolver.repository;

import com.localproblemsolver.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByProblemId(Long problemId);
}
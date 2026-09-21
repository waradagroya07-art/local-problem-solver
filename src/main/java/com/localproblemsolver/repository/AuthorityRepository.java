package com.localproblemsolver.repository;

import com.localproblemsolver.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    List<Authority> findByCategoryId(Long categoryId);

    List<Authority> findAllByOrderByIdAsc();
}
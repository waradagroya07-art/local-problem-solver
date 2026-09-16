package com.localproblemsolver.service;

import com.localproblemsolver.dto.AuthorityRequest;
import com.localproblemsolver.dto.AuthorityResponse;
import com.localproblemsolver.dto.CategoryResponse;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.Category;
import com.localproblemsolver.repository.AuthorityRepository;
import com.localproblemsolver.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final CategoryRepository categoryRepository;

    public AuthorityService(
            AuthorityRepository authorityRepository,
            CategoryRepository categoryRepository) {

        this.authorityRepository = authorityRepository;
        this.categoryRepository = categoryRepository;
    }

    public AuthorityResponse createAuthority(
            AuthorityRequest request) {

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Category not found with id: "
                                        + request.getCategoryId()
                        )
                );

        Authority authority = new Authority();

        authority.setName(request.getName());
        authority.setCategory(category);
        authority.setZone(request.getZone());

        Authority savedAuthority =
                authorityRepository.save(authority);

        return convertToResponse(savedAuthority);
    }

    public List<AuthorityResponse> getAllAuthorities() {

        return authorityRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private AuthorityResponse convertToResponse(
            Authority authority) {

        CategoryResponse categoryResponse = null;

        if (authority.getCategory() != null) {

            categoryResponse = new CategoryResponse(
                    authority.getCategory().getId(),
                    authority.getCategory().getName()
            );
        }

        return new AuthorityResponse(
                authority.getId(),
                authority.getName(),
                categoryResponse,
                authority.getZone()
        );
    }
}
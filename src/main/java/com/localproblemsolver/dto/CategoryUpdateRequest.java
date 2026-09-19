package com.localproblemsolver.dto;

import jakarta.validation.constraints.NotNull;

public class CategoryUpdateRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    public CategoryUpdateRequest() {
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
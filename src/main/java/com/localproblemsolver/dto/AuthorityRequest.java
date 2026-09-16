package com.localproblemsolver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AuthorityRequest {

    @NotBlank(message = "Authority name is required")
    private String name;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Zone is required")
    private String zone;

    public AuthorityRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
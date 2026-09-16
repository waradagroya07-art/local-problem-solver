package com.localproblemsolver.dto;

public class AuthorityResponse {

    private Long id;
    private String name;
    private CategoryResponse category;
    private String zone;

    public AuthorityResponse() {
    }

    public AuthorityResponse(
            Long id,
            String name,
            CategoryResponse category,
            String zone) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.zone = zone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
package com.localproblemsolver.controller;

import com.localproblemsolver.entity.Category;
import com.localproblemsolver.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/api/categories")
    public Category createCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    @GetMapping("/api/categories")
    public List<Category> getAllCategories() {
        return categoryService.findAllCategories();
    }
    @GetMapping("/api/categories/{id}")
    public Category getCategoryById(@PathVariable Long id) {

        return categoryService.findCategoryById(id);
    }
}
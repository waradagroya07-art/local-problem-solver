package com.localproblemsolver.service;

import com.localproblemsolver.entity.Category;
import com.localproblemsolver.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import com.localproblemsolver.exception.CategoryNotFoundException;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category findCategoryById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: " + id
                        )
                );
    }
}
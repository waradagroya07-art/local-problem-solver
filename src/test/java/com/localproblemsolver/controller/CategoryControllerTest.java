package com.localproblemsolver.controller;

import com.localproblemsolver.entity.Category;
import com.localproblemsolver.service.CategoryService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    private final CategoryService service =
            mock(CategoryService.class);
    private final CategoryController controller =
            new CategoryController(service);

    @Test
    void createCategory_delegatesToService() {
        Category category = mock(Category.class);

        when(service.saveCategory(category))
                .thenReturn(category);

        assertSame(
                category,
                controller.createCategory(category)
        );

        verify(service).saveCategory(category);
    }

    @Test
    void getAllCategories_delegatesToService() {
        List<Category> expected =
                List.of(mock(Category.class));

        when(service.findAllCategories())
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getAllCategories()
        );

        verify(service).findAllCategories();
    }

    @Test
    void getCategoryById_delegatesToService() {
        Category expected = mock(Category.class);

        when(service.findCategoryById(5L))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getCategoryById(5L)
        );

        verify(service).findCategoryById(5L);
    }
}

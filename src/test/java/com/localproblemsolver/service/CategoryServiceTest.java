package com.localproblemsolver.service;

import com.localproblemsolver.entity.Category;
import com.localproblemsolver.exception.CategoryNotFoundException;
import com.localproblemsolver.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category roadCategory;
    private Category waterCategory;

    @BeforeEach
    void setUp() {

        roadCategory = new Category();
        roadCategory.setId(1L);
        roadCategory.setName("Road");
        roadCategory.setDescription(
                "Road and infrastructure related problems"
        );

        waterCategory = new Category();
        waterCategory.setId(2L);
        waterCategory.setName("Water");
        waterCategory.setDescription(
                "Water supply related problems"
        );
    }

    // =========================================================
    // SAVE CATEGORY
    // =========================================================

    @Test
    void saveCategorySuccessfully() {

        when(categoryRepository.save(roadCategory))
                .thenReturn(roadCategory);

        Category result =
                categoryService.saveCategory(roadCategory);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                "Road",
                result.getName()
        );
        assertEquals(
                "Road and infrastructure related problems",
                result.getDescription()
        );

        verify(categoryRepository)
                .save(roadCategory);
    }

    @Test
    void saveCategoryReturnsSavedCategory() {

        Category savedCategory = new Category();
        savedCategory.setId(10L);
        savedCategory.setName("Electricity");
        savedCategory.setDescription(
                "Electricity related problems"
        );

        Category inputCategory = new Category();
        inputCategory.setName("Electricity");
        inputCategory.setDescription(
                "Electricity related problems"
        );

        when(categoryRepository.save(inputCategory))
                .thenReturn(savedCategory);

        Category result =
                categoryService.saveCategory(inputCategory);

        assertNotNull(result);
        assertEquals(
                10L,
                result.getId()
        );
        assertEquals(
                "Electricity",
                result.getName()
        );

        verify(categoryRepository)
                .save(inputCategory);
    }

    // =========================================================
    // FIND ALL CATEGORIES
    // =========================================================

    @Test
    void findAllCategoriesReturnsAllCategories() {

        when(categoryRepository.findAll())
                .thenReturn(
                        List.of(
                                roadCategory,
                                waterCategory
                        )
                );

        List<Category> result =
                categoryService.findAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(
                "Road",
                result.get(0).getName()
        );

        assertEquals(
                "Water",
                result.get(1).getName()
        );

        verify(categoryRepository)
                .findAll();
    }

    @Test
    void findAllCategoriesReturnsEmptyListWhenNoCategoriesExist() {

        when(categoryRepository.findAll())
                .thenReturn(List.of());

        List<Category> result =
                categoryService.findAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryRepository)
                .findAll();
    }

    // =========================================================
    // FIND CATEGORY BY ID
    // =========================================================

    @Test
    void findCategoryByIdReturnsCategory() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(roadCategory));

        Category result =
                categoryService.findCategoryById(1L);

        assertNotNull(result);

        assertEquals(
                1L,
                result.getId()
        );

        assertEquals(
                "Road",
                result.getName()
        );

        assertEquals(
                "Road and infrastructure related problems",
                result.getDescription()
        );

        verify(categoryRepository)
                .findById(1L);
    }

    @Test
    void findCategoryByIdFailsWhenCategoryDoesNotExist() {

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        CategoryNotFoundException exception =
                assertThrows(
                        CategoryNotFoundException.class,
                        () -> categoryService
                                .findCategoryById(999L)
                );

        assertTrue(
                exception.getMessage()
                        .contains("999")
        );

        verify(categoryRepository)
                .findById(999L);
    }

    @Test
    void findCategoryByIdUsesCorrectRepositoryId() {

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(waterCategory));

        Category result =
                categoryService.findCategoryById(2L);

        assertEquals(
                2L,
                result.getId()
        );

        assertEquals(
                "Water",
                result.getName()
        );

        verify(categoryRepository)
                .findById(2L);

        verify(categoryRepository, never())
                .findById(1L);
    }

    // =========================================================
    // VERIFY SAVE IS CALLED ONCE
    // =========================================================

    @Test
    void saveCategoryCallsRepositoryOnlyOnce() {

        when(categoryRepository.save(roadCategory))
                .thenReturn(roadCategory);

        categoryService.saveCategory(roadCategory);

        verify(
                categoryRepository,
                times(1)
        ).save(roadCategory);
    }
}
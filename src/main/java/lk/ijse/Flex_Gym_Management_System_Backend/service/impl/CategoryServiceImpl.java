package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.CategoryDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Category;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.CategoryStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.CategoryRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.CategoryService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        log.info("Execute saveCategory()");
        try {
            if (categoryDTO.getCategoryName() == null || categoryDTO.getCategoryName().isBlank()) {
                throw new RuntimeException("Category Name cannot be null or empty!");
            }

            if (categoryRepository.existsByCategoryName(categoryDTO.getCategoryName())) {
                throw new RuntimeException("Category with name '" + categoryDTO.getCategoryName() + "' already exists!");
            }

            Category category = new Category();
            category.setCategoryName(categoryDTO.getCategoryName());
            category.setCategoryDescription(categoryDTO.getCategoryDescription());
            category.setCategoryStatus(CategoryStatus.ACTIVE); // Default ACTIVE

            Category savedCategory = categoryRepository.save(category);
            log.info("Category saved successfully with ID: {}", savedCategory.getCategoryId());

            return convertToDTO(savedCategory);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        log.info("Execute updateCategory()");
        try {
            if (categoryDTO.getCategoryId() == null) {
                throw new RuntimeException("Category ID cannot be null for update!");
            }

            Optional<Category> optionalCategory = categoryRepository.findById(categoryDTO.getCategoryId());
            if (optionalCategory.isEmpty()) {
                throw new RuntimeException("Category not found with ID: " + categoryDTO.getCategoryId());
            }

            Category category = optionalCategory.get();

            if (category.getCategoryStatus() == CategoryStatus.DELETED) {
                throw new RuntimeException("Cannot update a deleted category!");
            }

            category.setCategoryName(categoryDTO.getCategoryName());
            category.setCategoryDescription(categoryDTO.getCategoryDescription());
            if (categoryDTO.getCategoryStatus() != null) {
                category.setCategoryStatus(categoryDTO.getCategoryStatus());
            }

            Category updatedCategory = categoryRepository.save(category);
            log.info("Category updated successfully!");

            return convertToDTO(updatedCategory);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        log.info("Execute getCategoryById()");
        try {
            if (id == null) {
                throw new RuntimeException("Category ID cannot be null!");
            }

            Optional<Category> optionalCategory = categoryRepository.findById(id);
            if (optionalCategory.isEmpty() || optionalCategory.get().getCategoryStatus() == CategoryStatus.DELETED) {
                throw new RuntimeException("Category not found with ID: " + id);
            }

            return convertToDTO(optionalCategory.get());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<CategoryDTO> getAllActiveCategories() {
        log.info("Execute getAllActiveCategories()");
        try {
            List<Category> categoryList = categoryRepository.findAllByCategoryStatus(CategoryStatus.ACTIVE);
            List<CategoryDTO> dtoList = new ArrayList<>();

            for (Category category : categoryList) {
                dtoList.add(convertToDTO(category));
            }
            return dtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String deleteCategory(Long id) {
        log.info("Execute deleteCategory()");
        try {
            if (id == null) {
                throw new RuntimeException("Category ID cannot be null!");
            }

            Optional<Category> optionalCategory = categoryRepository.findById(id);
            if (optionalCategory.isEmpty() || optionalCategory.get().getCategoryStatus() == CategoryStatus.DELETED) {
                throw new RuntimeException("Category not found with ID: " + id);
            }

            Category category = optionalCategory.get();
            category.setCategoryStatus(CategoryStatus.DELETED);
            categoryRepository.save(category);

            log.info("Category status set to deleted successfully!");
            return "Category deleted successfully!";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setCategoryDescription(category.getCategoryDescription());
        dto.setCategoryStatus(category.getCategoryStatus());
        return dto;
    }
}
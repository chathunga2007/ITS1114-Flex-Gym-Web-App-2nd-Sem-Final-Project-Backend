package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.CategoryDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.CategoryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(value = "/saveCategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedCategory, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateCategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedCategory, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getCategory/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getCategory(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);
        return new CommonResponse(OPERATION_SUCCESS, categoryDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllCategories", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllCategories() {
        List<CategoryDTO> categoryDTOList = categoryService.getAllActiveCategories();
        return new CommonResponse(OPERATION_SUCCESS, categoryDTOList, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteCategory/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteCategory(@PathVariable Long categoryId) {
        String deleteMessage = categoryService.deleteCategory(categoryId);
        return new CommonResponse(OPERATION_SUCCESS, deleteMessage, SUCCESS_MESSAGE);
    }
}
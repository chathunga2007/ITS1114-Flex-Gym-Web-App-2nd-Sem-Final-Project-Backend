package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.ProductDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/saveProduct", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO savedProduct = productService.saveProduct(productDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedProduct, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateProduct", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(productDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedProduct, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteProduct/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteProduct(@PathVariable Long productId) {
        String deleteMessage = productService.deleteProduct(productId);
        return new CommonResponse(OPERATION_SUCCESS, deleteMessage, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getProduct/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getProduct(@PathVariable Long productId) {
        ProductDTO productDTO = productService.getProductById(productId);
        return new CommonResponse(OPERATION_SUCCESS, productDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllProducts", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllProducts() {
        List<ProductDTO> productDTOList = productService.getAllActiveProducts();
        return new CommonResponse(OPERATION_SUCCESS, productDTOList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getProductsByCategory/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> productDTOList = productService.getProductsByCategoryId(categoryId);
        return new CommonResponse(OPERATION_SUCCESS, productDTOList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getLowStockAlerts", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getLowStockAlerts(@RequestParam(defaultValue = "5") Integer minStock) {
        List<ProductDTO> productDTOList = productService.getLowStockAlerts(minStock);
        return new CommonResponse(OPERATION_SUCCESS, productDTOList, SUCCESS_MESSAGE);
    }
}
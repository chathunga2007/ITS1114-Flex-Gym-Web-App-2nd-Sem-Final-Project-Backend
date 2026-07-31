package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.ProductDTO;
import java.util.List;

public interface ProductService {
    ProductDTO saveProduct(ProductDTO productDTO);
    ProductDTO updateProduct(ProductDTO productDTO);
    ProductDTO getProductById(Long id);
    List<ProductDTO> getAllActiveProducts();
    List<ProductDTO> getProductsByCategoryId(Long categoryId);
    String deleteProduct(Long id);
    List<ProductDTO> getLowStockAlerts(Integer minStock);
}
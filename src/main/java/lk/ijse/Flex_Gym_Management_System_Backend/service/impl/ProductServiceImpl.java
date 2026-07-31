package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.ProductDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Category;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Product;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.CategoryStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.ProductStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.CategoryRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.ProductRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        log.info("Execute saveProduct() for: {}", productDTO.getProductName());
        try {
            if (productDTO.getProductName() == null || productDTO.getProductName().isBlank()) {
                throw new RuntimeException("Product Name cannot be null or empty!");
            }

            if (productDTO.getCategoryId() == null) {
                throw new RuntimeException("Category ID is required!");
            }

            if (productRepository.existsByProductName(productDTO.getProductName())) {
                throw new RuntimeException("Product with name '" + productDTO.getProductName() + "' already exists!");
            }

            Optional<Category> optionalCategory = categoryRepository.findById(productDTO.getCategoryId());

            if (optionalCategory.isEmpty() || optionalCategory.get().getCategoryStatus() == CategoryStatus.DELETED) {
                throw new RuntimeException("Category not found with ID: " + productDTO.getCategoryId());
            }

            Product product = new Product();
            product.setProductName(productDTO.getProductName());
            product.setProductDescription(productDTO.getProductDescription());
            product.setProductPrice(productDTO.getProductPrice());
            product.setStockQuantity(productDTO.getStockQuantity());
            product.setImageUrl(productDTO.getImageUrl());
            product.setCategory(optionalCategory.get());
            product.setProductStatus(ProductStatus.ACTIVE);

            Product savedProduct = productRepository.save(product);
            log.info("Product saved successfully with ID: {}", savedProduct.getProductId());

            return convertToDTO(savedProduct);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        log.info("Execute updateProduct() for ID: {}", productDTO.getProductId());
        try {
            if (productDTO.getProductId() == null) {
                throw new RuntimeException("Product ID cannot be null for update!");
            }

            Optional<Product> optionalProduct = productRepository.findById(productDTO.getProductId());
            if (optionalProduct.isEmpty()) {
                throw new RuntimeException("Product not found with ID: " + productDTO.getProductId());
            }

            Product product = optionalProduct.get();

            if (product.getProductStatus() == ProductStatus.DELETED) {
                throw new RuntimeException("Cannot update a deleted product!");
            }

            if (productDTO.getCategoryId() != null) {
                Optional<Category> optionalCategory = categoryRepository.findById(productDTO.getCategoryId());
                if (optionalCategory.isEmpty() || optionalCategory.get().getCategoryStatus() == CategoryStatus.DELETED) {
                    throw new RuntimeException("Category not found with ID: " + productDTO.getCategoryId());
                }
                product.setCategory(optionalCategory.get());
            }

            product.setProductName(productDTO.getProductName());
            product.setProductDescription(productDTO.getProductDescription());
            product.setProductPrice(productDTO.getProductPrice());
            product.setStockQuantity(productDTO.getStockQuantity());
            product.setImageUrl(productDTO.getImageUrl());
            if (productDTO.getProductStatus() != null) {
                product.setProductStatus(productDTO.getProductStatus());
            }

            Product updatedProduct = productRepository.save(product);
            log.info("Product updated successfully!");

            return convertToDTO(updatedProduct);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String deleteProduct(Long id) {
        log.info("Execute deleteProduct() for ID: {}", id);
        try {
            if (id == null) {
                throw new RuntimeException("Product ID cannot be null!");
            }

            Optional<Product> optionalProduct = productRepository.findById(id);
            if (optionalProduct.isEmpty() || optionalProduct.get().getProductStatus() == ProductStatus.DELETED) {
                throw new RuntimeException("Product not found with ID: " + id);
            }

            Product product = optionalProduct.get();
            product.setProductStatus(ProductStatus.DELETED);
            productRepository.save(product);

            log.info("Product status set to DELETED successfully!");
            return "Product deleted successfully!";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<ProductDTO> getAllActiveProducts() {
        log.info("Execute getAllActiveProducts()");
        try {
            List<Product> productList = productRepository.findAllByProductStatus(ProductStatus.ACTIVE);
            List<ProductDTO> dtoList = new ArrayList<>();

            for (Product product : productList) {
                dtoList.add(convertToDTO(product));
            }
            return dtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductDTO getProductById(Long id) {
        log.info("Execute getProductById() for ID: {}", id);
        try {
            if (id == null) {
                throw new RuntimeException("Product ID cannot be null!");
            }

            Optional<Product> optionalProduct = productRepository.findById(id);
            if (optionalProduct.isEmpty() || optionalProduct.get().getProductStatus() == ProductStatus.DELETED) {
                throw new RuntimeException("Product not found with ID: " + id);
            }

            return convertToDTO(optionalProduct.get());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        log.info("Execute getProductsByCategoryId() for Category ID: {}", categoryId);
        try {
            List<Product> productList = productRepository.findAllByCategory_CategoryIdAndProductStatus(categoryId, ProductStatus.ACTIVE);
            List<ProductDTO> dtoList = new ArrayList<>();

            for (Product product : productList) {
                dtoList.add(convertToDTO(product));
            }
            return dtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<ProductDTO> getLowStockAlerts(Integer minStock) {
        log.info("Execute getLowStockAlerts() for minStock level: {}", minStock);
        try {
            int limit = (minStock != null && minStock > 0) ? minStock : 5;

            List<Product> productList = productRepository.findAllByProductStatusAndStockQuantityLessThanEqual(ProductStatus.ACTIVE, limit);

            List<ProductDTO> dtoList = new ArrayList<>();
            for (Product product : productList) {
                dtoList.add(convertToDTO(product));
            }
            return dtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setProductDescription(product.getProductDescription());
        dto.setProductPrice(product.getProductPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setImageUrl(product.getImageUrl());
        dto.setProductStatus(product.getProductStatus());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getCategoryId());
            dto.setCategoryName(product.getCategory().getCategoryName());
        }
        return dto;
    }
}
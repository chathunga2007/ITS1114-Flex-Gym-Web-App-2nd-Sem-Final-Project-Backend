package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.ProductDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Category;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Product;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.CategoryStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.ProductStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.CategoryRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.ProductRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.ProductService;
import lombok.extern.slf4j.Slf4j;

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
        log.info("Execute saveProduct()");
        if (productDTO == null) {
            throw new CustomException(400, "Product data cannot be null!");
        }
        if (productDTO.getProductName() == null || productDTO.getProductName().isBlank()) {
            throw new CustomException(400, "Product Name cannot be null or empty!");
        }
        if (productDTO.getCategoryId() == null) {
            throw new CustomException(400, "Category ID is required!");
        }

        if (productRepository.existsByProductName(productDTO.getProductName())) {
            throw new CustomException(409, "Product with name '" + productDTO.getProductName() + "' already exists!");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(productDTO.getCategoryId());

        if (optionalCategory.isEmpty() || optionalCategory.get().getCategoryStatus() == CategoryStatus.DELETED) {
            throw new CustomException(404, "Category not found with ID: " + productDTO.getCategoryId());
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
        log.info("Product saved successfully");

        ProductDTO responseDTO = new ProductDTO();
        responseDTO.setProductId(savedProduct.getProductId());
        responseDTO.setProductName(savedProduct.getProductName());
        responseDTO.setProductDescription(savedProduct.getProductDescription());
        responseDTO.setProductPrice(savedProduct.getProductPrice());
        responseDTO.setStockQuantity(savedProduct.getStockQuantity());
        responseDTO.setImageUrl(savedProduct.getImageUrl());
        responseDTO.setProductStatus(savedProduct.getProductStatus());

        if (savedProduct.getCategory() != null) {
            responseDTO.setCategoryId(savedProduct.getCategory().getCategoryId());
            responseDTO.setCategoryName(savedProduct.getCategory().getCategoryName());
        }
        return responseDTO;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        log.info("Execute updateProduct()");
        if (productDTO == null) {
            throw new CustomException(400, "Product data cannot be null!");
        }
        if (productDTO.getProductId() == null) {
            throw new CustomException(400, "Product ID cannot be null for update!");
        }

        Optional<Product> optionalProduct = productRepository.findById(productDTO.getProductId());
        if (optionalProduct.isEmpty()) {
            throw new CustomException(404, "Product not found with ID: " + productDTO.getProductId());
        }

        Product product = optionalProduct.get();

        if (product.getProductStatus() == ProductStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted product!");
        }

        if (productDTO.getCategoryId() != null) {
            Optional<Category> optionalCategory = categoryRepository.findById(productDTO.getCategoryId());
            if (optionalCategory.isEmpty() || optionalCategory.get().getCategoryStatus() == CategoryStatus.DELETED) {
                throw new CustomException(404, "Category not found with ID: " + productDTO.getCategoryId());
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

        ProductDTO responseDTO = new ProductDTO();
        responseDTO.setProductId(updatedProduct.getProductId());
        responseDTO.setProductName(updatedProduct.getProductName());
        responseDTO.setProductDescription(updatedProduct.getProductDescription());
        responseDTO.setProductPrice(updatedProduct.getProductPrice());
        responseDTO.setStockQuantity(updatedProduct.getStockQuantity());
        responseDTO.setImageUrl(updatedProduct.getImageUrl());
        responseDTO.setProductStatus(updatedProduct.getProductStatus());

        if (updatedProduct.getCategory() != null) {
            responseDTO.setCategoryId(updatedProduct.getCategory().getCategoryId());
            responseDTO.setCategoryName(updatedProduct.getCategory().getCategoryName());
        }
        return responseDTO;
    }

    @Override
    public String deleteProduct(Long id) {
        log.info("Execute deleteProduct()");
        if (id == null) {
            throw new CustomException(400, "Product ID cannot be null!");
        }

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty() || optionalProduct.get().getProductStatus() == ProductStatus.DELETED) {
            throw new CustomException(404, "Product not found with ID: " + id);
        }

        Product product = optionalProduct.get();
        product.setProductStatus(ProductStatus.DELETED);
        productRepository.save(product);

        log.info("Product status set to DELETED successfully!");
        return "Product deleted successfully!";
    }

    @Override
    public List<ProductDTO> getAllActiveProducts() {
        log.info("Execute getAllActiveProducts()");
        List<Product> productList = productRepository.findAllByProductStatus(ProductStatus.ACTIVE);
        List<ProductDTO> dtoList = new ArrayList<>();

        for (Product product : productList) {
            ProductDTO responseDTO = new ProductDTO();
            responseDTO.setProductId(product.getProductId());
            responseDTO.setProductName(product.getProductName());
            responseDTO.setProductDescription(product.getProductDescription());
            responseDTO.setProductPrice(product.getProductPrice());
            responseDTO.setStockQuantity(product.getStockQuantity());
            responseDTO.setImageUrl(product.getImageUrl());
            responseDTO.setProductStatus(product.getProductStatus());

            if (product.getCategory() != null) {
                responseDTO.setCategoryId(product.getCategory().getCategoryId());
                responseDTO.setCategoryName(product.getCategory().getCategoryName());
            }
            dtoList.add(responseDTO);
        }
        return dtoList;
    }

    @Override
    public ProductDTO getProductById(Long id) {
        log.info("Execute getProductById()");
        if (id == null) {
            throw new CustomException(400, "Product ID cannot be null!");
        }

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty() || optionalProduct.get().getProductStatus() == ProductStatus.DELETED) {
            throw new CustomException(404, "Product not found with ID: " + id);
        }

        Product product = optionalProduct.get();
        ProductDTO responseDTO = new ProductDTO();
        responseDTO.setProductId(product.getProductId());
        responseDTO.setProductName(product.getProductName());
        responseDTO.setProductDescription(product.getProductDescription());
        responseDTO.setProductPrice(product.getProductPrice());
        responseDTO.setStockQuantity(product.getStockQuantity());
        responseDTO.setImageUrl(product.getImageUrl());
        responseDTO.setProductStatus(product.getProductStatus());

        if (product.getCategory() != null) {
            responseDTO.setCategoryId(product.getCategory().getCategoryId());
            responseDTO.setCategoryName(product.getCategory().getCategoryName());
        }
        return responseDTO;
    }

    @Override
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        log.info("Execute getProductsByCategoryId()");
        if (categoryId == null) {
            throw new CustomException(400, "Category ID cannot be null!");
        }
        List<Product> productList = productRepository.findAllByCategory_CategoryIdAndProductStatus(categoryId, ProductStatus.ACTIVE);
        List<ProductDTO> dtoList = new ArrayList<>();

        for (Product product : productList) {
            ProductDTO responseDTO = new ProductDTO();
            responseDTO.setProductId(product.getProductId());
            responseDTO.setProductName(product.getProductName());
            responseDTO.setProductDescription(product.getProductDescription());
            responseDTO.setProductPrice(product.getProductPrice());
            responseDTO.setStockQuantity(product.getStockQuantity());
            responseDTO.setImageUrl(product.getImageUrl());
            responseDTO.setProductStatus(product.getProductStatus());

            if (product.getCategory() != null) {
                responseDTO.setCategoryId(product.getCategory().getCategoryId());
                responseDTO.setCategoryName(product.getCategory().getCategoryName());
            }
            dtoList.add(responseDTO);
        }
        return dtoList;
    }

    @Override
    public List<ProductDTO> getLowStockAlerts(Integer minStock) {
        log.info("Execute getLowStockAlerts()");
        int limit = (minStock != null && minStock > 0) ? minStock : 5;

        List<Product> productList = productRepository.findAllByProductStatusAndStockQuantityLessThanEqual(ProductStatus.ACTIVE, limit);

        List<ProductDTO> dtoList = new ArrayList<>();
        for (Product product : productList) {
            ProductDTO responseDTO = new ProductDTO();
            responseDTO.setProductId(product.getProductId());
            responseDTO.setProductName(product.getProductName());
            responseDTO.setProductDescription(product.getProductDescription());
            responseDTO.setProductPrice(product.getProductPrice());
            responseDTO.setStockQuantity(product.getStockQuantity());
            responseDTO.setImageUrl(product.getImageUrl());
            responseDTO.setProductStatus(product.getProductStatus());

            if (product.getCategory() != null) {
                responseDTO.setCategoryId(product.getCategory().getCategoryId());
                responseDTO.setCategoryName(product.getCategory().getCategoryName());
            }
            dtoList.add(responseDTO);
        }
        return dtoList;
    }
}
package lk.ijse.Flex_Gym_Management_System_Backend.repository;

import lk.ijse.Flex_Gym_Management_System_Backend.entity.Product;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByProductName(String productName);
    List<Product> findAllByProductStatus(ProductStatus productStatus);
    List<Product> findAllByCategory_CategoryIdAndProductStatus(Long categoryId, ProductStatus productStatus);
    List<Product> findAllByProductStatusAndStockQuantityLessThanEqual(ProductStatus status, Integer minStock);
}
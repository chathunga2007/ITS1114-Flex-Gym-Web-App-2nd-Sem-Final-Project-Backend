package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private Integer stockQuantity;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private ProductStatus productStatus;
}
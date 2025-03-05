package org.example.bidflow.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.bidflow.domain.product.entity.Product;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long productId;
    private String productName;
    private String imageUrl;
    private String description;

//    // Product 엔티티를 ProductDto로 변환하는 메서드
//    public static ProductDto from(Product product) {
//        return new ProductDto(
//                product.getProductId(),
//                product.getProductName(),
//                product.getImageUrl(),
//                product.getDescription()
//        );
//    }
}

package org.example.bidflow.domain.product.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.product.entity.Product;

@Getter
@Builder
public class ProductResponse {

    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final String description;

    // 엔티티를 DTO로 변환
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .build();
    }

}


package org.example.bidflow.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.bidflow.domain.auction.entity.Auction;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "PRODUCT_TABLE")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "DESCRIPTION")
    private String description;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Auction auction;
}

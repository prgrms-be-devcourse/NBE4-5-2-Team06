package org.example.bidflow.domain.product.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.bidflow.domain.auction.entity.Auction;

@Entity
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String prouctName;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String description;

    @OneToOne
    private Auction action;
}

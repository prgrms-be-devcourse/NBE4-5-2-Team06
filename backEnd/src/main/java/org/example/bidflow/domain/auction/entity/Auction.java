package org.example.bidflow.domain.auction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.bidflow.domain.bid.entity.Bid;
import org.example.bidflow.domain.product.entity.Product;
import org.example.bidflow.domain.winner.entity.Winner;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Auction")
public class Auction {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long auctionID;

        @Column(nullable = false)
        private String productId;

        private Integer startPrice;

        private  Integer minBid;

        private LocalDateTime startTime;

        private LocalDateTime endTime;

        @OneToOne
        private Product porduct;

        @OneToOne
        private Winner winner;

        @OneToMany
        private Bid bid;
}

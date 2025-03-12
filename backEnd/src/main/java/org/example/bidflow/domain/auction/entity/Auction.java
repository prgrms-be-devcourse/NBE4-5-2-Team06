package org.example.bidflow.domain.auction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.bid.entity.Bid;
import org.example.bidflow.domain.product.entity.Product;
import org.example.bidflow.domain.winner.entity.Winner;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "AUCTION_TABLE")
public class Auction {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "AUCTION_ID")
        private Long auctionId;

        @OneToOne
        @JoinColumn(name = "PRODUCT_ID", nullable = false)
        private Product product;

        @Column(name = "START_PRICE")
        private Integer startPrice;

        @Column(name = "MIN_BID")
        private Integer minBid;

        @Column(name = "START_TIME")
        private LocalDateTime startTime;

        @Column(name = "END_TIME")
        private LocalDateTime endTime;

        @Enumerated(EnumType.STRING)
        @Column(name = "STATUS")
        private AuctionStatus status;

        @OneToOne(mappedBy = "auction", cascade = CascadeType.ALL)
        private Winner winner;

        @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
        @Builder.Default
        private List<Bid> bids = new ArrayList<>();

        @CreatedDate
        private LocalDateTime createdAt;

        public void setStatus(AuctionStatus status) {
                this.status = status;
        }

        // 낙찰자 설정 메서드
        public void setWinner(Winner winner) {
                this.winner = winner;
        }
}

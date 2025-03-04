package org.example.bidflow.domain.bid.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "BID_TABLE")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BID_ID")
    private Long bidId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUCTION_ID", nullable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_UUID", nullable = false)
    private User user;

    @Column(name = "AMOUNT")
    private Integer amount;

    @Column(name = "BID_TIME")
    private LocalDateTime bidTime;
}

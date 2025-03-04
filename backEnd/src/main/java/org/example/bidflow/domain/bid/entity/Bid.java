package org.example.bidflow.domain.bid.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.bidflow.domain.auction.entity.Auction;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Bid")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    @Column(nullable = false)
    private Long auctionId;

    @Column(nullable = false)
    private String userId;

    private Integer amount;

    private LocalDateTime bidTime;

    @ManyToOne
    private Auction auction;

    @ManyToOne
    private User user;
}

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

    public Bid(Auction auction, User user) {
        this.auction = auction;
        this.user = user;
    }

    // 입찰을 생성하는 static 메서드
    public static Bid createBid(Auction auction, User user, Integer amount, LocalDateTime bidTime) {
        return Bid.builder()
                .auction(auction)
                .user(user)
                .amount(amount)
                .bidTime(bidTime)
                .build();
    }

    // Bid 엔티티 수정
    public Bid updateAmount(Integer newAmount) {
        this.amount = newAmount;
        this.bidTime = LocalDateTime.now(); // 금액 변경 시 입찰 시간 갱신
        return this; // 갱신된 객체를 반환
    }
}

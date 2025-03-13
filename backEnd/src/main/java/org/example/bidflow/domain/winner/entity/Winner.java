package org.example.bidflow.domain.winner.entity;


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
@Table(name = "WINNER_TABLE")
public class Winner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WINNER_ID")
    private Long winnerId;

    @ManyToOne
    @JoinColumn(name = "USER_UUID", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "AUCTION_ID", nullable = false)
    private Auction auction;

    @Column(name = "WINNING_BID")
    private Integer winningBid;
    
    @Column(name = "WIN_TIME")
    private LocalDateTime winTime;
}

package org.example.bidflow.domain.bid.repository;

import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.bid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findTopByAuctionOrderByAmountDesc(Auction auction);
}

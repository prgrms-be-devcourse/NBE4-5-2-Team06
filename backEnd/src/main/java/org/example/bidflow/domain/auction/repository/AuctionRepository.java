package org.example.bidflow.domain.auction.repository;

import org.example.bidflow.domain.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction,Long> {

    Optional<Auction> findByAuctionId(Long auctionId);
}

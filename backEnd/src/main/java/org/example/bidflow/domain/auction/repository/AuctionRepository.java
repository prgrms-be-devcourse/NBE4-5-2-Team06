package org.example.bidflow.domain.auction.repository;

import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction,Long> {
    
  Optional<Auction> findByAuctionIdAndStatus(Long auctionId, AuctionStatus status);

  Optional<Auction> findByAuctionId(Long auctionId);
 
}

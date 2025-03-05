package org.example.bidflow.domain.auction.repository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction,Long> {
  
    // 사용자 - 전체 경매 상품 리스트 조회하는 쿼리
    @Query("SELECT a FROM Auction a JOIN FETCH a.product")
    List<Auction> findAllAuctions();

    Optional<Auction> findByAuctionIdAndStatus(Long auctionId, AuctionStatus status);

    Optional<Auction> findByAuctionId(Long auctionId);
 
}






package org.example.bidflow.domain.auction.repository;


import org.example.bidflow.domain.auction.dto.AuctionDto;
import org.example.bidflow.domain.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction,Long> {
    // 전체 경매 상품 리스트 조회하는 쿼리
    @Query("SELECT new org.example.bidflow.domain.auction.dto.AuctionDto( " +
            "a.auctionId, p.productName, p.imageUrl, " +
            "CASE " +
            "   WHEN a.status = 'FINISHED' THEN w.winningBid " +
            "   ELSE COALESCE(MAX(b.amount), a.startPrice) " +
            "END, " +
            "a.status, a.startTime, a.endTime) " +
            "FROM Auction a " +
            "JOIN a.product p " +
            "LEFT JOIN a.winner w " +
            "LEFT JOIN Bid b ON b.auction.auctionId = a.auctionId " +
            "GROUP BY a.auctionId, p.productName, p.imageUrl, a.status, a.startTime, a.endTime, w.winningBid " +
            "ORDER BY a.auctionId")
    List<AuctionDto> findAllAuctions();
}

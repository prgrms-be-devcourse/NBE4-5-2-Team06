package org.example.bidflow.domain.auction.repository;

import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction,Long> {

    // 사용자 - 전체 경매 상품 리스트 조회하는 쿼리
    @Query("SELECT a FROM Auction a JOIN FETCH a.product")
    List<Auction> findAllAuctions();

    // 관리자 - 전체 경매 상품 리스트 조회하는 쿼리
    @Query("SELECT a FROM Auction a JOIN FETCH a.product LEFT JOIN FETCH a.winner")
    List<Auction> findAllAuctionsWithProductAndWinner();

    Optional<Auction> findByAuctionId(Long auctionId);
}




package org.example.bidflow.domain.auction.service;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.auction.dto.AuctionDetailResponse;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionService {

    public final AuctionRepository auctionRepository;

    // 경매 데이터 검증 후 DTO 반환
    @Transactional(readOnly = true)
    public AuctionDetailResponse getAuctionDetail(Long auctionId) {
        Auction auction = auctionRepository.findByAuctionId(auctionId)      // 경매 ID로 경매 객체 존재 여부 확인
                .orElseThrow(() -> new ServiceException("400-1", "해당 경매 상품을 찾을 수 없습니다."));

        if (auction.getStatus() != AuctionStatus.UPCOMING) {
            throw new ServiceException("400-2", "진행 중인 경매가 아닙니다.");
        }

        return AuctionDetailResponse.from(auction); // DTO 변환 후 반환
    }


}

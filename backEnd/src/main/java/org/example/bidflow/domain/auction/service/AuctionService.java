package org.example.bidflow.domain.auction.service;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.auction.dto.AuctionRequest;
import org.example.bidflow.domain.auction.dto.AuctionResponse;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.example.bidflow.domain.product.entity.Product;
import org.example.bidflow.domain.product.repository.ProductRepository;
import org.example.bidflow.global.dto.RsData;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;

    // 경매 등록 서비스
    @Transactional
    public RsData<AuctionResponse> createAuction(AuctionRequest requestDto) {

        // 경매 종료 시간이 시작 시간보다 빠르면 예외 처리
        if (requestDto.getStartTime().isAfter(requestDto.getEndTime())) {
            throw new ServiceException("400", "경매 종료 시간이 시작 시간보다 빠를 수 없습니다.");
        }

        // 최소 등록 시간 검증 (현재 시간 기준 최소 2일 전)
        LocalDateTime now = LocalDateTime.now();
        if (requestDto.getStartTime().isBefore(now.plusDays(2))) {
            throw new ServiceException("404", "상품 등록 시간은 최소 2일 전부터 가능합니다.");
        }

        // 상품 중복 검증
        Optional<Product> existingProduct = productRepository.findByProductName(requestDto.getProductName());
        if (existingProduct.isPresent()) {
            throw new ServiceException("400", "이미 등록된 상품입니다.");
        }

        // 상품 정보 저장
        Product product = Product.builder()
                .productName(requestDto.getProductName())
                .imageUrl(requestDto.getImageUrl())
                .description(requestDto.getDescription())
                .build();
        productRepository.save(product);

        // 경매 정보 저장
        Auction auction = Auction.builder()
                .product(product)
                .startPrice(requestDto.getStartPrice())
                .minBid(requestDto.getMinBid())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .status(AuctionStatus.UPCOMING)
                .build();
        auctionRepository.save(auction);

        // 성공 응답 반환
        return new RsData<>("201", "경매가 등록되었습니다.", AuctionResponse.from(auction));
    }

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

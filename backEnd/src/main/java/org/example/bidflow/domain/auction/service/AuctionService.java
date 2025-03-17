package org.example.bidflow.domain.auction.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.data.Role;
import org.example.bidflow.domain.auction.dto.*;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.example.bidflow.domain.bid.repository.BidRepository;
import org.example.bidflow.domain.product.entity.Product;
import org.example.bidflow.domain.product.repository.ProductRepository;
import org.example.bidflow.global.annotation.HasRole;
import org.example.bidflow.global.app.RedisCommon;
import org.example.bidflow.global.dto.RsData;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final ProductRepository productRepository;
    private final RedisCommon redisCommon;

    // 사용자-모든 경매 목록을 조회하고 AuctionResponse DTO 리스트로 변환
    public List<AuctionCheckResponse> getAllAuctions()  {
        // 경매 목록 조회 <AuctionRepository에서 조회>
        List<Auction> auctions = auctionRepository.findAllAuctions();
        if (auctions.isEmpty()) { // 리스트가 비어있을경우 예외처리
            throw new ServiceException("404", "경매 목록 조회 실패");
        }

        // Auction 엔티티를 AuctionCheckResponse DTO로 변환
        return auctions.stream()
                .map(auction -> {
                    String hashKey = "auction:" + auction.getAuctionId();
                    Integer amount = redisCommon.getFromHash(hashKey, "amount", Integer.class); // amount : Redis 에서 가져온 최고가
                    log.info("amount: {}", amount);
                    return AuctionCheckResponse.from(auction, amount);
                })
                .collect(Collectors.toList());
    }

    //관리자- 모든 경매 목록을 조회 (관리자)
    @HasRole(Role.ADMIN)
    public List<AuctionAdminResponse> getAdminAllAuctions() {
        List<Auction> auctions = auctionRepository.findAllAuctionsWithProductAndWinner();

        if (auctions == null || auctions.isEmpty()) {
            throw new ServiceException("404", "경매 목록 조회 실패");
        }

        return auctions.stream()
                .map(auction -> {
                    String hashKey = "auction:" + auction.getAuctionId();
                    Integer amount = redisCommon.getFromHash(hashKey, "amount", Integer.class); // amount : Redis 에서 가져온 최고가
                    log.info("amount: {}", amount);
                    return AuctionAdminResponse.from(auction, amount);
                })
                .toList();
    }

    // 경매 등록 서비스 (관리자)
    @HasRole(Role.ADMIN)
    @Transactional
    public RsData<AuctionCreateResponse> createAuction(AuctionRequest requestDto) {

        /*// 경매 종료 시간이 시작 시간보다 빠르면 예외 처리
        if (requestDto.getStartTime().isAfter(requestDto.getEndTime())) {
            throw new ServiceException("400", "경매 종료 시간이 시작 시간보다 빠를 수 없습니다.");
        }

        // 최소 등록 시간 검증 (현재 시간 기준 최소 2일 전)
        LocalDateTime now = LocalDateTime.now();
        if (requestDto.getStartTime().isBefore(now.plusDays(2))) {
            throw new ServiceException("404", "상품 등록 시간은 최소 2일 전부터 가능합니다.");
        }*/

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

        /*
        Redis(Key-Value): auction:{auctionId} = {amount=?}
         */

        // 상품 등록이 발생했을 때, 레디스 메모리 상에서 경매 시작가, TTl을 설정
        String hashKey = "auction:" + auction.getAuctionId();
        redisCommon.putInHash(hashKey, "amount", auction.getStartPrice()); // 입찰할 때는 amount로 넣고 있음.

        LocalDateTime expireTime = auction.getEndTime().plusMinutes(2); // starTime: 12:30, endTime: 12:40 -> 12:42(cause. 여유시간(2분)) => TTL: 12:42까지 유효 => 12:42 이후에는 경매 종료 => 경매 종료시(12:40) Winner 테이블에 저장 => Scheduler 로 처리
        redisCommon.setExpireAt(hashKey, expireTime);

        // 성공 응답 반환
        return new RsData<>("201", "경매가 등록되었습니다.", AuctionCreateResponse.from(auction));
    }

    // 외부 요청에 대한 거래 종료 기능 (보류)
    @Transactional
    public void closeAuction(Long auctionId) {
        Auction auction = auctionRepository.findByAuctionId(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("진행 중인 경매를 찾을 수 없습니다."));// 최고 입찰가 찾기

        auction.setStatus(AuctionStatus.FINISHED);
    }

    // 경매 데이터 검증 후 DTO 반환
    @Transactional
    public AuctionDetailResponse getAuctionDetail(Long auctionId) {
        Auction auction = getAuctionWithValidation(auctionId); // 경매 ID로 경매 데이터 조회 및 상태 검증

        // explain: Redis 에서 최고가(amount)를 가져오는 로직 추가
        String hashKey = "auction:" + auction.getAuctionId();
        Integer amount = redisCommon.getFromHash(hashKey, "amount", Integer.class); // amount : Redis 에서 가져온 최고가
        log.info("DetailCurrentAmount: {}", amount);

        return AuctionDetailResponse.from(auction, amount); // DTO 변환 후 반환
    }

    // 경매 조회 및 상태 검증 메서드
    public Auction getAuctionWithValidation(Long auctionId) {

        // 경매 조회
        Auction auction = auctionRepository.findByAuctionId(auctionId)
                .orElseThrow(() -> new ServiceException("400-1", "경매가 존재하지 않습니다."));

        /*// TODO: 경매 상태 검증 -> 상태 변화에 따른 해당 로직 필요성 검증
        if (!auction.getStatus().equals(AuctionStatus.ONGOING)) {
            throw new ServiceException("400-2", "진행 중인 경매가 아닙니다.");
        }*/

        return auction;
    }

}
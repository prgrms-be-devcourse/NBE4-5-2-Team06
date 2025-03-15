package org.example.bidflow.domain.winner.service;


import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.winner.dto.WinnerCheckResponse;
import org.example.bidflow.domain.winner.entity.Winner;
import org.example.bidflow.domain.winner.repository.WinnerRepository;
import org.example.bidflow.global.dto.RsData;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WinnerService {
    private final WinnerRepository winnerRepository;

    // 사용자의 낙찰 내역 조회
    @Transactional(readOnly = true)
    public RsData<List<WinnerCheckResponse>> getWinnerList(String userUUID) {
        List<Winner> winners = winnerRepository.findByUser_UserUUID(userUUID);

        // 낙찰자가 존재하지 않을 경우 예외 처리
        if(winners.isEmpty()){
            throw new ServiceException("404", "낙찰자가 존재하지 않습니다.");
        }

        // 낙찰자 목록을 WinnerCheckResponse 로 변환
        List<WinnerCheckResponse> response = winners.stream()
                .map(winner -> WinnerCheckResponse.builder()
                        .auctionId(winner.getAuction().getAuctionId())
                        .productName(winner.getAuction().getProduct().getProductName())
                        .description(winner.getAuction().getProduct().getDescription())
                        .winningBid(winner.getWinningBid())
                        .winTime(winner.getWinTime())
                        .imageUrl(winner.getAuction().getProduct().getImageUrl())
                        .build())
                .toList();

        return new RsData<>("200", "낙찰 내역 조회가 완료되었습니다.", response);
    }
}

package org.example.bidflow.domain.winner.controller;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.winner.dto.WinnerCheckResponse;
import org.example.bidflow.domain.winner.service.WinnerService;
import org.example.bidflow.global.dto.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class WinnerController {
    private final WinnerService winnerService;

    // 낙찰 내역 조회 컨트롤러
    @GetMapping("/{userUUID}/winner")
    public ResponseEntity<RsData<List<WinnerCheckResponse>>> getWinnerList(@PathVariable("userUUID") String userUUID) {
        RsData<List<WinnerCheckResponse>> response = winnerService.getWinnerList(userUUID);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
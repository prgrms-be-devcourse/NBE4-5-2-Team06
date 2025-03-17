package org.example.bidflow.global.app;

import lombok.Getter;
import org.example.bidflow.domain.auction.entity.Auction;
import org.springframework.context.ApplicationEvent;

// "경매가 종료되었을 때 발생하는 이벤트"를 나타내는 클래스
@Getter
public class AuctionFinishedEvent extends ApplicationEvent {
    private final Auction auction;

    public AuctionFinishedEvent(Object source, Auction auction) {
        super(source);
        this.auction = auction;
    }
}

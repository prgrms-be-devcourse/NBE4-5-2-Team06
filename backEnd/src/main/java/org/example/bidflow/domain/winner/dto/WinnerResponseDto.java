package org.example.bidflow.domain.winner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.bidflow.domain.winner.entity.Winner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WinnerResponseDto {
    private String userUUID;
    private Integer winningBid;

    public WinnerResponseDto(Winner winner) {
        this.userUUID = winner.getUser().getUserUUID();
        this.winningBid = winner.getWinningBid();
    }
}

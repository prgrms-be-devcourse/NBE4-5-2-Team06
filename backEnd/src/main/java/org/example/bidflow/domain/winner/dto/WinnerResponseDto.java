package org.example.bidflow.domain.winner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.bidflow.domain.winner.entity.Winner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WinnerResponseDto {
    private String userUuid;
    private Integer winningBid;

    public WinnerResponseDto(Winner winner) {
        this.userUuid = winner.getUser().getUserUuid();
        this.winningBid = winner.getWinningBid();
    }
}

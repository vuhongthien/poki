package com.remake.poki.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossRankingResponseDTO {
    private Long bossScheduleId;
    private String bossName;
    private List<BossRankingPlayerDTO> topPlayers; // Top 10
    private BossRankingPlayerDTO currentPlayer; // Thông tin người chơi hiện tại
}
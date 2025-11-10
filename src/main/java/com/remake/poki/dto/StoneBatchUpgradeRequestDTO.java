package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoneBatchUpgradeRequestDTO {
    private int userId;
    private List<StoneGroupDTO> stoneGroups; // Danh sách tất cả nhóm đá cần nâng cấp

    @Override
    public String toString() {
        return "StoneBatchUpgradeRequestDTO{" +
                "userId=" + userId +
                ", totalGroups=" + (stoneGroups != null ? stoneGroups.size() : 0) +
                '}';
    }
}

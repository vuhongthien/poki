package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; /**
 * Request để khảm sao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InlayStarRequest {
    private Long userId;
    private Long petId;
    private Long slotId;
}

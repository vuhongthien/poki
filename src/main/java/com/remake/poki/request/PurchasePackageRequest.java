package com.remake.poki.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePackageRequest {
    private Long userId;
    private Long packageId;
    private String paymentMethod; // MOMO, ZALOPAY, BANKING, CARD
    private String returnUrl; // URL để redirect sau khi thanh toán
}

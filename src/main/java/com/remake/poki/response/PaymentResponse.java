package com.remake.poki.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String transactionId;
    private String paymentUrl; // URL để user redirect đến
    private String qrCode; // QR code để scan (optional)
    private Integer amount;
    private String status; // PENDING, SUCCESS, FAILED
    private String message;
}

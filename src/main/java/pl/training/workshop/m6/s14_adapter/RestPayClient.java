package pl.training.workshop.m6.s14_adapter;

import java.math.BigDecimal;

/**
 * "Biblioteka" nowej bramki (nie zmieniamy jej): kwota w złotych, odmowa jako wyjątek.
 * Deterministyczna symulacja: powyżej 500.00 wyjątek z kodem LIMIT.
 */
public class RestPayClient {
    public record ChargeRequest(BigDecimal amount, String currency, String reference) {
    }

    public record ChargeResponse(String transactionId) {
    }

    public static final class RestPayException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final String code;

        public RestPayException(String code) {
            super("payment rejected: " + code);
            this.code = code;
        }

        public String code() {
            return code;
        }
    }

    public ChargeResponse charge(ChargeRequest request) {
        if (request.amount().compareTo(new BigDecimal("500.00")) > 0) {
            throw new RestPayException("LIMIT");
        }
        return new ChargeResponse("T-" + request.reference());
    }
}

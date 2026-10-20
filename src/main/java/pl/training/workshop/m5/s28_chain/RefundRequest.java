package pl.training.workshop.m5.s28_chain;

import java.time.LocalDateTime;
import java.util.Objects;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: prośba o zwrot za bilet - kwota, początek seansu, moment prośby. */
public record RefundRequest(Money amount, LocalDateTime screeningStart, LocalDateTime requestedAt) {
    public RefundRequest {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(screeningStart, "screeningStart");
        Objects.requireNonNull(requestedAt, "requestedAt");
    }
}

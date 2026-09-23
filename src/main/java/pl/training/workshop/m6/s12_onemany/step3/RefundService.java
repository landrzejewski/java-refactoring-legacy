package pl.training.workshop.m6.s12_onemany.step3;

import java.time.LocalDateTime;

import pl.training.workshop.shared.Money;

/** Krok 3: po migracji klientów stare metody usunięte - został jeden kontrakt. */
public final class RefundService {
    private static final Money FEE = Money.of("3.00");

    public Money refund(Refundable refundable, LocalDateTime now) {
        return refundable.refundableAmount(now).minus(FEE).max(Money.ZERO);
    }
}

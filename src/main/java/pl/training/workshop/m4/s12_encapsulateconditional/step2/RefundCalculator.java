package pl.training.workshop.m4.s12_encapsulateconditional.step2;

import java.time.LocalDateTime;

import pl.training.workshop.m4.s12_encapsulateconditional.Booking;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: Encapsulate Conditional - cały warunek jako {@code isRefundable(b, now)}.
 * if pyta teraz o regułę biznesową; szczegóły (status, czas, promocja) są w jednym miejscu.
 */
public final class RefundCalculator {
    private static final Money CANCELLATION_FEE = Money.of("3.00");

    public Money refund(Booking b, LocalDateTime now) {
        Money amount = Money.ZERO;
        if (isRefundable(b, now)) {
            if (!now.plusHours(24).isAfter(b.screeningStart())) {
                amount = b.tickets();
            } else {
                amount = b.tickets().percent(50);
            }
        }
        return amount.minus(CANCELLATION_FEE).max(Money.ZERO);
    }

    private static boolean isRefundable(Booking b, LocalDateTime now) {
        return b.status().equals("PAID") && now.isBefore(b.screeningStart()) && !hasFreeTicketPromo(b);
    }

    private static boolean hasFreeTicketPromo(Booking b) {
        return b.promo() != null && b.promo().startsWith("FREE");
    }
}

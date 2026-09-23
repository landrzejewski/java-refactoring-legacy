package pl.training.workshop.m4.s12_encapsulateconditional.step3;

import java.time.LocalDateTime;

import pl.training.workshop.m4.s12_encapsulateconditional.Booking;
import pl.training.workshop.shared.Money;

/**
 * Krok 3 (rozwiązanie): Encapsulate Conditional dla gałęzi - {@code cancelledAtLeast24hBefore}.
 * Podwójne zaprzeczenie {@code !now.plusHours(24).isAfter(...)} zamknięte w nazwie z regulaminu.
 */
public final class RefundCalculator {
    private static final Money CANCELLATION_FEE = Money.of("3.00");

    public Money refund(Booking b, LocalDateTime now) {
        Money amount = Money.ZERO;
        if (isRefundable(b, now)) {
            if (cancelledAtLeast24hBefore(b, now)) {
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

    private static boolean cancelledAtLeast24hBefore(Booking b, LocalDateTime now) {
        return !now.plusHours(24).isAfter(b.screeningStart());
    }
}

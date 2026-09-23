package pl.training.workshop.m4.s12_encapsulateconditional.step1;

import java.time.LocalDateTime;

import pl.training.workshop.m4.s12_encapsulateconditional.Booking;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: Extract Method dla najmniejszego fragmentu - {@code hasFreeTicketPromo}.
 * Osłona {@code promo() != null &&} idzie RAZEM z testem prefiksu:
 * krótkie spięcie jest zachowaniem.
 */
public final class RefundCalculator {
    private static final Money CANCELLATION_FEE = Money.of("3.00");

    public Money refund(Booking b, LocalDateTime now) {
        Money amount = Money.ZERO;
        if (b.status().equals("PAID") && now.isBefore(b.screeningStart()) && !hasFreeTicketPromo(b)) {
            if (!now.plusHours(24).isAfter(b.screeningStart())) {
                amount = b.tickets();
            } else {
                amount = b.tickets().percent(50);
            }
        }
        return amount.minus(CANCELLATION_FEE).max(Money.ZERO);
    }

    private static boolean hasFreeTicketPromo(Booking b) {
        return b.promo() != null && b.promo().startsWith("FREE");
    }
}

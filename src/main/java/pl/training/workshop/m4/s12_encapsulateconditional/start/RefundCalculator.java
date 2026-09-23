package pl.training.workshop.m4.s12_encapsulateconditional.start;

import java.time.LocalDateTime;

import pl.training.workshop.m4.s12_encapsulateconditional.Booking;
import pl.training.workshop.shared.Money;

/**
 * Start: kwota zwrotu. Pierwszy if pyta o implementację (status, czas, prefiks kodu promocji),
 * a nie o regułę "czy rezerwacji przysługuje zwrot". Drugi if ukrywa próg 24 godzin.
 */
public final class RefundCalculator {
    private static final Money CANCELLATION_FEE = Money.of("3.00");

    public Money refund(Booking b, LocalDateTime now) {
        Money amount = Money.ZERO;
        if (b.status().equals("PAID") && now.isBefore(b.screeningStart())
                && !(b.promo() != null && b.promo().startsWith("FREE"))) {
            if (!now.plusHours(24).isAfter(b.screeningStart())) {
                amount = b.tickets();
            } else {
                amount = b.tickets().percent(50);
            }
        }
        return amount.minus(CANCELLATION_FEE).max(Money.ZERO);
    }
}

package pl.training.workshop.m6.s12_onemany.step2;

import java.time.LocalDateTime;
import java.util.List;

import pl.training.workshop.m6.s12_onemany.TicketData;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: Replace One/Many Distinctions with Composite - jedna metoda refund(Refundable).
 * Stare metody zostają jako cienkie delegacje, dopóki klienci nie przejdą na nowy kontrakt.
 */
public final class RefundService {
    private static final Money FEE = Money.of("3.00");

    public Money refund(Refundable refundable, LocalDateTime now) {
        return refundable.refundableAmount(now).minus(FEE).max(Money.ZERO);
    }

    /** @deprecated użyj {@link #refund(Refundable, LocalDateTime)} z {@link SingleTicket}. */
    @Deprecated
    public Money refund(TicketData ticket, LocalDateTime now) {
        return refund(new SingleTicket(ticket), now);
    }

    /** @deprecated użyj {@link #refund(Refundable, LocalDateTime)} z {@link TicketGroup}. */
    @Deprecated
    public Money refundAll(List<TicketData> tickets, LocalDateTime now) {
        return refund(TicketGroup.of(tickets), now);
    }
}

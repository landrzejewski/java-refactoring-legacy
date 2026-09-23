package pl.training.workshop.m6.s12_onemany.step1;

import java.time.LocalDateTime;
import java.util.List;

import pl.training.workshop.m6.s12_onemany.TicketData;
import pl.training.workshop.shared.Money;

/** Krok 1: klient bez zmian. */
public final class CancellationDesk {
    private final RefundService service = new RefundService();

    public Money refund(List<TicketData> tickets, LocalDateTime now) {
        if (tickets.size() == 1) {
            return service.refund(tickets.getFirst(), now);
        }
        return service.refundAll(tickets, now);
    }
}

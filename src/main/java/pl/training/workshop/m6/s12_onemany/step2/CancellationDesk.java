package pl.training.workshop.m6.s12_onemany.step2;

import java.time.LocalDateTime;
import java.util.List;

import pl.training.workshop.m6.s12_onemany.TicketData;
import pl.training.workshop.shared.Money;

/** Krok 2: klient przeniesiony na nowy kontrakt; rozróżnienie jeden/wiele jeszcze widać. */
public final class CancellationDesk {
    private final RefundService service = new RefundService();

    public Money refund(List<TicketData> tickets, LocalDateTime now) {
        Refundable refundable = tickets.size() == 1
                ? new SingleTicket(tickets.getFirst())
                : TicketGroup.of(tickets);
        return service.refund(refundable, now);
    }
}

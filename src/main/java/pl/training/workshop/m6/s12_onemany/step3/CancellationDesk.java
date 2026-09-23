package pl.training.workshop.m6.s12_onemany.step3;

import java.time.LocalDateTime;
import java.util.List;

import pl.training.workshop.m6.s12_onemany.TicketData;
import pl.training.workshop.shared.Money;

/** Krok 3: rozróżnienie zniknęło - grupa jednego biletu zachowuje się jak bilet. */
public final class CancellationDesk {
    private final RefundService service = new RefundService();

    public Money refund(List<TicketData> tickets, LocalDateTime now) {
        return service.refund(TicketGroup.of(tickets), now);
    }
}

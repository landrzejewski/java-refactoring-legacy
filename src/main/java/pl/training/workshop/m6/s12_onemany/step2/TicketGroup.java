package pl.training.workshop.m6.s12_onemany.step2;

import java.time.LocalDateTime;
import java.util.List;

import pl.training.workshop.m6.s12_onemany.TicketData;
import pl.training.workshop.shared.Money;

/** Krok 2: węzeł - suma zwrotów elementów; pusta grupa daje 0.00. */
public record TicketGroup(List<Refundable> items) implements Refundable {
    public TicketGroup {
        items = List.copyOf(items);
    }

    public static TicketGroup of(List<TicketData> tickets) {
        return new TicketGroup(tickets.stream().<Refundable>map(SingleTicket::new).toList());
    }

    @Override
    public Money refundableAmount(LocalDateTime now) {
        Money total = Money.ZERO;
        for (Refundable item : items) {
            total = total.plus(item.refundableAmount(now));
        }
        return total;
    }
}

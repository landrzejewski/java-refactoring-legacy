package pl.training.workshop.m6.s19_visitor.step2;

import java.util.List;

import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian - przykładowe zamówienia (bilety, bar, vouchery). */
public final class SampleOrders {
    public List<OrderItem> find(String code) {
        return switch (code) {
            case "evening" -> List.of(
                    new TicketItem("Diuna", "IMAX", Money.of("40.00")),
                    new SnackItem("Popcorn L", Money.of("18.00")),
                    new SnackItem("Cola", Money.of("9.00")),
                    new VoucherItem("KINO20", Money.of("20.00")));
            case "voucher" -> List.of(
                    new TicketItem("Amator", "2D", Money.of("25.00")),
                    new VoucherItem("KINO20", Money.of("20.00")));
            case "empty" -> List.of();
            default -> throw new IllegalArgumentException("unknown order: " + code);
        };
    }
}

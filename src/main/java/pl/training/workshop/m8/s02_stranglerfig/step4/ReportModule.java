package pl.training.workshop.m8.s02_stranglerfig.step4;

import java.util.Map;
import java.util.TreeMap;

import pl.training.workshop.m8.s02_stranglerfig.BookingLedger;
import pl.training.workshop.shared.Money;

/** Krok 4 (bez zmian): nowy moduł raportów - ten sam format wyjścia, sumy w Money zamiast double. */
public final class ReportModule {
    private final BookingLedger ledger;

    public ReportModule(BookingLedger ledger) {
        this.ledger = ledger;
    }

    public String report() {
        Map<String, TitleSales> byTitle = new TreeMap<>();
        for (BookingLedger.Booking booking : ledger.all()) {
            byTitle.merge(booking.title(), new TitleSales(booking.tickets(), booking.ticketsValue()),
                    TitleSales::plus);
        }
        TitleSales total = byTitle.values().stream().reduce(TitleSales.NONE, TitleSales::plus);
        Money fees = ledger.all().stream().map(BookingLedger.Booking::fees).reduce(Money.ZERO, Money::plus);
        StringBuilder text = new StringBuilder("RAPORT\n");
        byTitle.forEach((title, sales) -> text.append(title).append(": ").append(sales.tickets())
                .append(" bil., ").append(sales.value()).append('\n'));
        return text.append("Biletow: ").append(total.tickets()).append('\n')
                .append("Przychod z biletow: ").append(total.value()).append('\n')
                .append("Oplaty rezerwacyjne: ").append(fees).append('\n')
                .toString();
    }

    private record TitleSales(int tickets, Money value) {
        static final TitleSales NONE = new TitleSales(0, Money.ZERO);

        TitleSales plus(TitleSales other) {
            return new TitleSales(tickets + other.tickets, value.plus(other.value));
        }
    }
}

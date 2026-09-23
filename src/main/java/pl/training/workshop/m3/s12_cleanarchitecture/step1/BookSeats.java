package pl.training.workshop.m3.s12_cleanarchitecture.step1;

import java.math.BigDecimal;

import pl.training.workshop.m3.s12_cleanarchitecture.Outbox;
import pl.training.workshop.m3.s12_cleanarchitecture.RowStore;

/**
 * Krok 1: Extract Class - przypadek użycia jako jawna orkiestracja jednego celu:
 * wyceń, zapisz, powiadom. Wejście i wyjście to rekordy. Wciąż zna jednak
 * wiersz Object[] i temat komunikatu (szczegóły techniczne).
 */
public final class BookSeats {
    private final RowStore db;
    private final Outbox outbox;

    public BookSeats(RowStore db, Outbox outbox) {
        this.db = db;
        this.outbox = outbox;
    }

    public Booking execute(BookSeatsCommand command) {
        if (command.rows().isEmpty()) {
            throw new IllegalArgumentException("brak miejsc");
        }
        BigDecimal total = price(command);
        String id = db.insert(
                new Object[] {command.email(), command.format(), command.rows().size(), total});
        outbox.publish("reservation-created", id + ";" + command.email() + ";" + total);
        return new Booking(id, total);
    }

    private BigDecimal price(BookSeatsCommand command) {
        BigDecimal base = switch (command.format()) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
        BigDecimal total = new BigDecimal("0.00");
        for (int row : command.rows()) {
            total = total.add(base);
            if (row >= 10) {
                total = total.add(new BigDecimal("10.00"));
            }
        }
        return total;
    }
}

package pl.training.workshop.m7.s03_breakresponsibilities.step1;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import pl.training.workshop.m7.s03_breakresponsibilities.BookingRequest;
import pl.training.workshop.m7.s03_breakresponsibilities.Outbox;

/** Krok 1: Extract Class dla walidacji - BookingDesk deleguje do BookingValidator. */
public final class BookingDesk {
    private final Outbox outbox;
    private final BookingValidator validator = new BookingValidator();

    public BookingDesk(Outbox outbox) {
        this.outbox = Objects.requireNonNull(outbox, "outbox");
    }

    public String book(BookingRequest request) {
        Optional<String> error = validator.firstError(request);
        if (error.isPresent()) {
            return error.get();
        }

        // wycena
        BigDecimal base = switch (request.format()) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
        BigDecimal total = BigDecimal.ZERO;
        int vipSeats = 0;
        for (String seat : request.seats()) {
            total = total.add(base);
            if (Integer.parseInt(seat.substring(1)) >= 10) {
                total = total.add(new BigDecimal("10.00"));
                vipSeats++;
            }
        }

        // powiadomienie
        String text = "Rezerwacja " + request.seats().size() + " miejsc";
        if (vipSeats > 0) {
            text = text + " (VIP: " + vipSeats + ")";
        }
        outbox.send(request.email(), text + ", do zaplaty " + total);
        return "OK " + total;
    }
}

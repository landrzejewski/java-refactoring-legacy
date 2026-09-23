package pl.training.workshop.m7.s03_breakresponsibilities.start;

import java.math.BigDecimal;
import java.util.Objects;

import pl.training.workshop.m7.s03_breakresponsibilities.BookingRequest;
import pl.training.workshop.m7.s03_breakresponsibilities.Outbox;

/**
 * Start: jedna metoda, trzy powody zmiany - reguły walidacji (dział obsługi),
 * cennik (dział finansów) i treść powiadomienia (marketing). Komentarze dzielą ją na klastry.
 */
public final class BookingDesk {
    private final Outbox outbox;

    public BookingDesk(Outbox outbox) {
        this.outbox = Objects.requireNonNull(outbox, "outbox");
    }

    public String book(BookingRequest request) {
        // walidacja
        if (request.email() == null || !request.email().contains("@")) {
            return "ERROR: niepoprawny e-mail";
        }
        if (request.seats().isEmpty()) {
            return "ERROR: brak miejsc";
        }
        for (String seat : request.seats()) {
            if (!seat.matches("[A-L][0-9]{1,2}")) {
                return "ERROR: niepoprawne miejsce " + seat;
            }
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

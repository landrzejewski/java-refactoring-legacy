package pl.training.workshop.m3.s12_cleanarchitecture.step2;

import java.math.BigDecimal;

/**
 * Krok 2: przypadek użycia zależy tylko od własnych portów. Protokół efektów jest
 * jawny: najpierw zapis, potem powiadomienie - błąd zapisu oznacza brak powiadomienia.
 */
public final class BookSeats {
    private final ReservationStore store;
    private final BookingNotifier notifier;

    public BookSeats(ReservationStore store, BookingNotifier notifier) {
        this.store = store;
        this.notifier = notifier;
    }

    public Booking execute(BookSeatsCommand command) {
        if (command.rows().isEmpty()) {
            throw new IllegalArgumentException("brak miejsc");
        }
        NewReservation reservation = new NewReservation(
                command.email(), command.format(), command.rows().size(), price(command));
        String id = store.save(reservation);
        notifier.reservationCreated(id, reservation);
        return new Booking(id, reservation.total());
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

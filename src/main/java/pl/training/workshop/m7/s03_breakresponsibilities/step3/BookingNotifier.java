package pl.training.workshop.m7.s03_breakresponsibilities.step3;

import java.util.Objects;

import pl.training.workshop.m7.s03_breakresponsibilities.BookingRequest;
import pl.training.workshop.m7.s03_breakresponsibilities.Outbox;

/** Krok 3: Extract Class - treść i wysyłka powiadomienia mają jednego właściciela. */
final class BookingNotifier {
    private final Outbox outbox;

    BookingNotifier(Outbox outbox) {
        this.outbox = Objects.requireNonNull(outbox, "outbox");
    }

    void bookingConfirmed(BookingRequest request, Pricing pricing) {
        String text = "Rezerwacja " + request.seats().size() + " miejsc";
        if (pricing.vipSeats() > 0) {
            text = text + " (VIP: " + pricing.vipSeats() + ")";
        }
        outbox.send(request.email(), text + ", do zaplaty " + pricing.total());
    }
}

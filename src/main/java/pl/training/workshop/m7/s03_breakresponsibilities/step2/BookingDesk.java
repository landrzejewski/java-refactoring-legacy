package pl.training.workshop.m7.s03_breakresponsibilities.step2;

import java.util.Objects;
import java.util.Optional;

import pl.training.workshop.m7.s03_breakresponsibilities.BookingRequest;
import pl.training.workshop.m7.s03_breakresponsibilities.Outbox;

/** Krok 2: Extract Class dla wyceny - BookingDesk deleguje do TicketPricer. */
public final class BookingDesk {
    private final Outbox outbox;
    private final BookingValidator validator = new BookingValidator();
    private final TicketPricer pricer = new TicketPricer();

    public BookingDesk(Outbox outbox) {
        this.outbox = Objects.requireNonNull(outbox, "outbox");
    }

    public String book(BookingRequest request) {
        Optional<String> error = validator.firstError(request);
        if (error.isPresent()) {
            return error.get();
        }
        Pricing pricing = pricer.price(request);

        // powiadomienie
        String text = "Rezerwacja " + request.seats().size() + " miejsc";
        if (pricing.vipSeats() > 0) {
            text = text + " (VIP: " + pricing.vipSeats() + ")";
        }
        outbox.send(request.email(), text + ", do zaplaty " + pricing.total());
        return "OK " + pricing.total();
    }
}

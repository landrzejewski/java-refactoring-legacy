package pl.training.workshop.m7.s03_breakresponsibilities.step3;

import java.util.Objects;
import java.util.Optional;

import pl.training.workshop.m7.s03_breakresponsibilities.BookingRequest;
import pl.training.workshop.m7.s03_breakresponsibilities.Outbox;

/**
 * Krok 3 (rozwiązanie): BookingDesk tylko składa trzy kroki - waliduj, wyceń, powiadom.
 * Publiczne API (konstruktor z Outbox, book) bez zmian; szczegóły mają osobnych właścicieli.
 */
public final class BookingDesk {
    private final BookingValidator validator;
    private final TicketPricer pricer;
    private final BookingNotifier notifier;

    public BookingDesk(Outbox outbox) {
        this(new BookingValidator(), new TicketPricer(), new BookingNotifier(outbox));
    }

    BookingDesk(BookingValidator validator, TicketPricer pricer, BookingNotifier notifier) {
        this.validator = Objects.requireNonNull(validator, "validator");
        this.pricer = Objects.requireNonNull(pricer, "pricer");
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    public String book(BookingRequest request) {
        Optional<String> error = validator.firstError(request);
        if (error.isPresent()) {
            return error.get();
        }
        Pricing pricing = pricer.price(request);
        notifier.bookingConfirmed(request, pricing);
        return "OK " + pricing.total();
    }
}

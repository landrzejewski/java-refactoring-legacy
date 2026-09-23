package pl.training.workshop.m8.s01_branchbyabstraction.step2;

import pl.training.workshop.m8.s01_branchbyabstraction.BookingRequest;

/** Krok 2: klient zależy od abstrakcji TicketPricing; domyślnie działa stara implementacja. */
public final class BookingService {
    private final TicketPricing pricing;

    public BookingService() {
        this(new LegacyTicketPricing());
    }

    public BookingService(TicketPricing pricing) {
        this.pricing = pricing;
    }

    public String confirm(BookingRequest request) {
        return request.screening().title() + ": " + String.join(",", request.seats())
                + " - do zaplaty " + pricing.total(request);
    }
}

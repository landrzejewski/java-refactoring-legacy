package pl.training.workshop.m8.s01_branchbyabstraction.step1;

import java.util.Locale;

import pl.training.workshop.m8.s01_branchbyabstraction.BookingRequest;

/** Krok 1: potwierdzenie deleguje wyliczenie ceny do wydzielonej klasy LegacyTicketPricing. */
public final class BookingService {
    private final LegacyTicketPricing pricing = new LegacyTicketPricing();

    public String confirm(BookingRequest request) {
        double total = pricing.total(request);
        return request.screening().title() + ": " + String.join(",", request.seats())
                + " - do zaplaty " + String.format(Locale.ROOT, "%.2f", total);
    }
}

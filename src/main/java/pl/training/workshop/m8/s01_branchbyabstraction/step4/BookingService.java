package pl.training.workshop.m8.s01_branchbyabstraction.step4;

import pl.training.workshop.m8.s01_branchbyabstraction.BookingRequest;

/**
 * Krok 4: usunięcie starej ścieżki. Po okresie obserwacji w trybie MODERN kasujemy
 * LegacyTicketPricing i PricingMode - migracja jest zamknięta dopiero teraz.
 */
public final class BookingService {
    private final TicketPricing pricing;

    public BookingService() {
        this(new ModernTicketPricing());
    }

    public BookingService(TicketPricing pricing) {
        this.pricing = pricing;
    }

    public String confirm(BookingRequest request) {
        return request.screening().title() + ": " + String.join(",", request.seats())
                + " - do zaplaty " + pricing.total(request);
    }
}

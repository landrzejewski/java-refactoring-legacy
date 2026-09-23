package pl.training.workshop.m8.s01_branchbyabstraction.step3;

import pl.training.workshop.m8.s01_branchbyabstraction.BookingRequest;

/**
 * Krok 3: przełącznik w jednym miejscu. Domyślnie LEGACY - wdrożenie nowego kodu
 * nie zmienia zachowania, dopiero konfiguracja (MODERN) je przełącza.
 */
public final class BookingService {
    private final TicketPricing pricing;

    public BookingService() {
        this(PricingMode.LEGACY);
    }

    public BookingService(PricingMode mode) {
        this(switch (mode) {
            case LEGACY -> new LegacyTicketPricing();
            case MODERN -> new ModernTicketPricing();
        });
    }

    public BookingService(TicketPricing pricing) {
        this.pricing = pricing;
    }

    public String confirm(BookingRequest request) {
        return request.screening().title() + ": " + String.join(",", request.seats())
                + " - do zaplaty " + pricing.total(request);
    }
}

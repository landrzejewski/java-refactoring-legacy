package pl.training.workshop.m8.s01_branchbyabstraction.step4;

import pl.training.workshop.m8.s01_branchbyabstraction.BookingRequest;
import pl.training.workshop.shared.Money;

/**
 * Krok 4: abstrakcja zostaje jako szew dla testów i kolejnych zmian.
 * Stara implementacja i przełącznik zostały usunięte (Safe Delete).
 */
public interface TicketPricing {
    Money total(BookingRequest request);
}

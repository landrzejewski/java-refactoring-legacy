package pl.training.workshop.m8.s01_branchbyabstraction.step3;

import pl.training.workshop.m8.s01_branchbyabstraction.BookingRequest;
import pl.training.workshop.shared.Money;

/**
 * Krok 3 (bez zmian): abstrakcja - kontrakt pod przyszłą implementację (Money, nie double).
 * To jest "gałąź" w Branch by Abstraction: w kodzie, nie w systemie kontroli wersji.
 */
public interface TicketPricing {
    Money total(BookingRequest request);
}

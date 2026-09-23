package pl.training.workshop.m8.s01_branchbyabstraction.step2;

import pl.training.workshop.m8.s01_branchbyabstraction.BookingRequest;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: abstrakcja - kontrakt projektowany pod przyszłą implementację (Money, nie double).
 * To jest "gałąź" w Branch by Abstraction: w kodzie, nie w systemie kontroli wersji.
 */
public interface TicketPricing {
    Money total(BookingRequest request);
}

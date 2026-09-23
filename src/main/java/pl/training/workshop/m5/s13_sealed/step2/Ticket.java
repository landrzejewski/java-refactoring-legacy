package pl.training.workshop.m5.s13_sealed.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: bez zmian - zamknięta hierarchia pozwala na switch bez default.
 */
public sealed interface Ticket permits StandardTicket, StudentTicket, SeniorTicket {
    Money basePrice();
}

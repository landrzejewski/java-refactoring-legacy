package pl.training.workshop.m5.s13_sealed.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: sealed - zamknięta lista wariantów w jednym miejscu. Rekordy są final, więc spełniają
 * warunek permits bez zmian. Kod spoza listy nie może już implementować Ticket.
 */
public sealed interface Ticket permits StandardTicket, StudentTicket, SeniorTicket {
    Money basePrice();
}

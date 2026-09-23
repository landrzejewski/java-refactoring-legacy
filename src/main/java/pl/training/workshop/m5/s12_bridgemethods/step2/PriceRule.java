package pl.training.workshop.m5.s12_bridgemethods.step2;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s12_bridgemethods.Ticket;

/**
 * Krok 2 (rozwiązanie): reguła jawnie deklaruje obsługiwany typ - {@code ticketType()} zastępuje
 * zgadywanie refleksją (Replace Reflection with Explicit Contract).
 */
public interface PriceRule<T extends Ticket> {
    Class<T> ticketType();

    Money apply(T ticket);
}

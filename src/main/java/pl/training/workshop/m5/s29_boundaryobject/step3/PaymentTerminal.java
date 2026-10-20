package pl.training.workshop.m5.s29_boundaryobject.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: port domeny - terminal płatniczy widziany oczami kina: Money na wejściu, obiekt graniczny na wyjściu. */
@FunctionalInterface
public interface PaymentTerminal {
    PaymentConfirmation authorize(String bookingId, Money amount);
}

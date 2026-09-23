package pl.training.workshop.m5.s13_sealed.start;

import pl.training.workshop.shared.Money;

/** Start: otwarta hierarchia - każdy może dopisać implementację, a kalkulator o tym nie wie. */
public interface Ticket {
    Money basePrice();
}

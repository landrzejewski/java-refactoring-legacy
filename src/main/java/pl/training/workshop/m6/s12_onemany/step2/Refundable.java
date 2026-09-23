package pl.training.workshop.m6.s12_onemany.step2;

import java.time.LocalDateTime;

import pl.training.workshop.shared.Money;

/** Krok 2: wspólny kontrakt "jednego" i "wielu" - kwota do zwrotu przed potrąceniem. */
public sealed interface Refundable permits SingleTicket, TicketGroup {
    Money refundableAmount(LocalDateTime now);
}

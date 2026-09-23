package pl.training.workshop.m6.s12_onemany.step3;

import java.time.LocalDateTime;

import pl.training.workshop.shared.Money;

/** Krok 3: wspólny kontrakt "jednego" i "wielu" - kwota do zwrotu przed potrąceniem. */
public sealed interface Refundable permits SingleTicket, TicketGroup {
    Money refundableAmount(LocalDateTime now);
}

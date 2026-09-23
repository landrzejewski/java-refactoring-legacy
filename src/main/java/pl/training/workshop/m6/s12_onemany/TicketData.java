package pl.training.workshop.m6.s12_onemany;

import java.time.LocalDateTime;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: zapłacona cena biletu i start seansu. */
public record TicketData(Money price, LocalDateTime showStart) {
}

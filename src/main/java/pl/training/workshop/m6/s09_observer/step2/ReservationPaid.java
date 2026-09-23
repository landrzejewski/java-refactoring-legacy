package pl.training.workshop.m6.s09_observer.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian - zdarzenie - fakt "rezerwacja opłacona" z danymi potrzebnymi odbiorcom. */
public record ReservationPaid(String reservationId, String email, String phone, Money amount) {
}

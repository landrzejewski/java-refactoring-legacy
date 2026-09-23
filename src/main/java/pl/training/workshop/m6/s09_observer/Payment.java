package pl.training.workshop.m6.s09_observer;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: opłata za rezerwację (kwota biletów, bez opłat). */
public record Payment(String reservationId, String email, String phone, Money amount) {
}

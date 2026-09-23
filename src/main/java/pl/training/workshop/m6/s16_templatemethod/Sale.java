package pl.training.workshop.m6.s16_templatemethod;

import java.time.LocalTime;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: sprzedaż na jeden seans dnia. */
public record Sale(LocalTime time, String title, int tickets, Money amount) {
}

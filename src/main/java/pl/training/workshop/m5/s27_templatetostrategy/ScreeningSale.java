package pl.training.workshop.m5.s27_templatetostrategy;

import java.time.LocalDate;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: sprzedaż biletów na jeden seans danego dnia. */
public record ScreeningSale(LocalDate day, String title, int tickets, Money revenue) {
}

package pl.training.workshop.m2.s09_cleanarchitecture.step4.app;

import java.math.BigDecimal;

/** Krok 1: wynik przypadku użycia. */
public record Booking(String id, BigDecimal total) {
}

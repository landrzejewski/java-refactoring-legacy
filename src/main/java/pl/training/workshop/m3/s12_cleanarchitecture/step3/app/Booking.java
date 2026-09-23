package pl.training.workshop.m3.s12_cleanarchitecture.step3.app;

import java.math.BigDecimal;

/** Krok 1: wynik przypadku użycia. */
public record Booking(String id, BigDecimal total) {
}

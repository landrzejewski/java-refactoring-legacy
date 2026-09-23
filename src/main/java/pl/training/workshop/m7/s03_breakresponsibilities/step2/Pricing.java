package pl.training.workshop.m7.s03_breakresponsibilities.step2;

import java.math.BigDecimal;

/** Krok 2: wynik wyceny - suma i liczba miejsc VIP (potrzebna w powiadomieniu). */
record Pricing(BigDecimal total, int vipSeats) {
}

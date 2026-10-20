package pl.training.workshop.m2.s09_cleanarchitecture.step3.app;

import java.math.BigDecimal;

/** Krok 2: dane przekraczające granicę do portu zapisu - rekord, nie Object[]. */
public record NewReservation(String email, String format, int seats, BigDecimal total) {
}

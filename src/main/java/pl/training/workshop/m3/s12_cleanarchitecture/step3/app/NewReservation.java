package pl.training.workshop.m3.s12_cleanarchitecture.step3.app;

import java.math.BigDecimal;

/** Krok 2: dane przekraczające granicę do portu zapisu - rekord, nie Object[]. */
public record NewReservation(String email, String format, int seats, BigDecimal total) {
}

package pl.training.workshop.m3.s05_shadowlimits;

/** Stabilny kontrakt sceny: rezerwacja online biletów 2D (25.00 + 2.00 opłaty za bilet). */
public record BookingRequest(String email, String card, String title, int tickets) {
}

package pl.training.workshop.m4.s12_encapsulateconditional;

import java.time.LocalDateTime;

import pl.training.workshop.shared.Money;

/**
 * Stabilny kontrakt sceny.
 *
 * @param status  NEW, PAID, USED, EXPIRED, CANCELLED
 * @param tickets zapłacone za bilety (bez opłat rezerwacyjnych - te nie podlegają zwrotowi)
 * @param promo   kod promocji albo {@code null}; kody "FREE..." to bilety darmowe (bez zwrotu)
 */
public record Booking(String status, LocalDateTime screeningStart, Money tickets, String promo) {
}

package pl.training.workshop.m7.s09_doublenegative.step2;

import java.time.LocalDate;

/** Krok 1 (bez zmian w kroku 2): pozytywny predykat isExpired() delegujący do isNotExpired(). */
public record Voucher(String code, LocalDate validUntil) {
    public boolean isNotExpired(LocalDate today) {
        return !today.isAfter(validUntil);
    }

    public boolean isExpired(LocalDate today) {
        return !isNotExpired(today);
    }
}

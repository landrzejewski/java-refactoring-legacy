package pl.training.workshop.m7.s09_doublenegative.step1;

import java.time.LocalDate;

/** Krok 1: pozytywny predykat isExpired() delegujący do isNotExpired() - nic jeszcze nie migrujemy. */
public record Voucher(String code, LocalDate validUntil) {
    public boolean isNotExpired(LocalDate today) {
        return !today.isAfter(validUntil);
    }

    public boolean isExpired(LocalDate today) {
        return !isNotExpired(today);
    }
}

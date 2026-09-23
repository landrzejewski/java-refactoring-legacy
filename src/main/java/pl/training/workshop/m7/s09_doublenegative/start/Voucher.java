package pl.training.workshop.m7.s09_doublenegative.start;

import java.time.LocalDate;

/** Start: negatywny predykat isNotExpired - czytelnik musi go odwracać w głowie. */
public record Voucher(String code, LocalDate validUntil) {
    public boolean isNotExpired(LocalDate today) {
        return !today.isAfter(validUntil);
    }
}

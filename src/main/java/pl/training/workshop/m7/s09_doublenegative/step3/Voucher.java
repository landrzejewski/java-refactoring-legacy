package pl.training.workshop.m7.s09_doublenegative.step3;

import java.time.LocalDate;

/** Krok 3: isExpired() jest teraz jedyną definicją reguły; isNotExpired() usunięte (Safe Delete). */
public record Voucher(String code, LocalDate validUntil) {
    public boolean isExpired(LocalDate today) {
        return today.isAfter(validUntil);
    }
}

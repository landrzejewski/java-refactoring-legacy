package pl.training.workshop.m5.s31_anticorruptionlayer.step1;

import java.time.LocalDate;
import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: licencja filmu w języku kina - typy domeny (LocalDate, Money, procent jako int),
 * niezmienniki w konstruktorze i reguły przy danych: czy obejmuje dzień, ile się należy.
 */
public record FilmLicense(String filmCode, String title, boolean active, LocalDate validFrom, LocalDate validTo,
                          int sharePercent, Money minimumGuarantee) {
    public FilmLicense {
        Objects.requireNonNull(filmCode, "filmCode");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(validFrom, "validFrom");
        Objects.requireNonNull(validTo, "validTo");
        Objects.requireNonNull(minimumGuarantee, "minimumGuarantee");
        if (validTo.isBefore(validFrom)) {
            throw new IllegalArgumentException("license ends before it starts");
        }
        if (sharePercent < 0 || sharePercent > 100) {
            throw new IllegalArgumentException("share out of range: " + sharePercent);
        }
        if (minimumGuarantee.compareTo(Money.ZERO) < 0) {
            throw new IllegalArgumentException("minimum guarantee must not be negative");
        }
    }

    public boolean covers(LocalDate day) {
        return !day.isBefore(validFrom) && !day.isAfter(validTo);
    }

    public boolean allowsScreeningOn(LocalDate day) {
        return active && covers(day);
    }

    /** Udział dystrybutora, ale nie mniej niż minimalna gwarancja. */
    public Money dueFor(Money revenue) {
        return revenue.percent(sharePercent).max(minimumGuarantee);
    }
}

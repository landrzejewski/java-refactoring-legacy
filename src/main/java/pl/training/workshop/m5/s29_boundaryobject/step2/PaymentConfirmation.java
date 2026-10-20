package pl.training.workshop.m5.s29_boundaryobject.step2;

import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: bez zmian - Boundary Object - odpowiedź terminala jako niezmienna wartość w języku kina.
 * Walidacja w konstruktorze: obiekt, który istnieje, jest poprawny, więc dalszy kod nie musi
 * niczego sprawdzać ani zgadywać. Kwota jako Money, wynik jako enum, kod nigdy pusty.
 */
public record PaymentConfirmation(Outcome outcome, String code, Money authorized) {
    public enum Outcome { APPROVED, DECLINED, ERROR }

    public PaymentConfirmation {
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(authorized, "authorized");
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        if (authorized.compareTo(Money.ZERO) < 0) {
            throw new IllegalArgumentException("authorized amount must not be negative");
        }
        if (outcome != Outcome.APPROVED && authorized.isGreaterThan(Money.ZERO)) {
            throw new IllegalArgumentException("only approved payment can authorize money");
        }
    }

    public boolean approved() {
        return outcome == Outcome.APPROVED;
    }

    /** Autoryzacja częściowa: terminal zgodził się na mniej, niż prosiliśmy. */
    public boolean isPartialFor(Money requested) {
        return approved() && authorized.compareTo(requested) < 0;
    }
}

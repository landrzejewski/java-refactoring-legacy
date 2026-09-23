package pl.training.workshop.m8.s11_stagedrollout.step1;

import java.util.Set;

/**
 * Krok 1: polityka jako wartość - Introduce Parameter Object dla flagi i listy wyjątków.
 * Konfigurację da się teraz podać z zewnątrz, przetestować i opisać w przeglądzie.
 */
public record RolloutPolicy(boolean enabled, Set<String> allowList) {
    public RolloutPolicy {
        allowList = Set.copyOf(allowList);
    }

    /** Dotychczasowe ustawienia produkcyjne - te same, co stała i if w starym kodzie. */
    public static RolloutPolicy current() {
        return new RolloutPolicy(false, Set.of("anna@kino.pl", "jan@kino.pl"));
    }

    public boolean allows(String email) {
        return enabled || allowList.contains(email);
    }
}

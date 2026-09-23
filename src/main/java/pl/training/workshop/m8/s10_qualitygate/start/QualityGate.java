package pl.training.workshop.m8.s10_qualitygate.start;

import java.util.List;

import pl.training.workshop.m8.s10_qualitygate.GateInput;

/**
 * Start: "bramka jakości" to lista kontrolna w wiki i dobra wola. Metoda evaluate niczego
 * nie sprawdza, więc przepuszcza wszystko - fałszywe poczucie bezpieczeństwa.
 * <ul>
 *   <li>testy zielone</li>
 *   <li>brak ostrzeżeń kompilatora</li>
 *   <li>brak TODO i System.out w domenie</li>
 *   <li>kluczowa klasa pokryta testem</li>
 * </ul>
 */
public final class QualityGate {
    /** Lista wyników bramki; pusta lista = bramka przepuszcza zmianę. */
    public List<String> evaluate(GateInput input) {
        return List.of();
    }

    public boolean passes(GateInput input) {
        return evaluate(input).isEmpty();
    }
}

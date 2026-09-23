package pl.training.workshop.m6.s18_collectingparameter.step3;

import java.util.ArrayList;
import java.util.List;

/**
 * Krok 3: wąski parametr zbierający - można tylko dopisać ostrzeżenie. Metody pomocnicze
 * nie mogą niczego usunąć, wyczyścić ani przestawić.
 */
public final class Warnings {
    private final List<String> items = new ArrayList<>();

    public void add(String warning) {
        items.add(warning);
    }

    public String summary() {
        return items.isEmpty() ? "OK" : String.join("; ", items);
    }
}

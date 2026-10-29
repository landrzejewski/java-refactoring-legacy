package pl.training.workshop.exercises.e06_movieinfo.step3;

import java.util.HashMap;
import java.util.Map;

import pl.training.workshop.exercises.e06_movieinfo.AgeRatingResource;

/**
 * Krok 3: jedna instancja tabeli na JVM. Bezpieczna, bo tabela jest niemutowalna i każda jej kopia
 * byłaby równoważna; enum daje leniwe, bezpieczne wątkowo tworzenie bez synchronizacji.
 * To domyślna, nie jedyna implementacja {@link AgeRatings} - o cyklu życia decyduje korzeń
 * kompozycji, a test może podać własną tabelę.
 */
public enum AgeRatingTable implements AgeRatings {
    INSTANCE;

    private final Map<String, String> categories;

    AgeRatingTable() {
        Map<String, String> parsed = new HashMap<>();
        for (String line : AgeRatingResource.readLines()) {
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split(";");
            parsed.put(parts[0] + ":" + parts[1], parts[2]);
        }
        categories = Map.copyOf(parsed);
    }

    @Override
    public String categoryFor(String system, String code) {
        return categories.getOrDefault(system + ":" + code, "brak");
    }
}

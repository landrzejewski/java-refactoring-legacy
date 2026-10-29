package pl.training.workshop.exercises.e06_movieinfo.start;

import java.util.HashMap;
import java.util.Map;

import pl.training.workshop.exercises.e06_movieinfo.AgeRatingResource;

/**
 * Start: tabela kategorii wiekowych. Niemutowalna i taka sama dla całego kina, a mimo to
 * {@link #load()} czyta i parsuje plik przy każdym wywołaniu - bo nikt nie zdecydował, kto
 * i kiedy ją tworzy.
 */
public final class AgeRatingTable {
    private final Map<String, String> categories;

    private AgeRatingTable(Map<String, String> categories) {
        this.categories = categories;
    }

    public static AgeRatingTable load() {
        Map<String, String> parsed = new HashMap<>();
        for (String line : AgeRatingResource.readLines()) {
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split(";");
            parsed.put(parts[0] + ":" + parts[1], parts[2]);
        }
        return new AgeRatingTable(Map.copyOf(parsed));
    }

    /** Kategoria kina dla kodu z obcego systemu; nieznany kod to "brak". */
    public String categoryFor(String system, String code) {
        return categories.getOrDefault(system + ":" + code, "brak");
    }
}

package pl.training.workshop.exercises.e06_movieinfo.step3;

/** Krok 3: Extract Interface - adaptery zależą od kontraktu tabeli, nie od jedynej instancji. */
@FunctionalInterface
public interface AgeRatings {
    /** Kategoria kina dla kodu z obcego systemu; nieznany kod to "brak". */
    String categoryFor(String system, String code);
}

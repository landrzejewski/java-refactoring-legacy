package pl.training.workshop.exercises.e06_movieinfo.step2;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;

/** Krok 2: port katalogu bez zmian - ten sam interfejs ma adapter i owijająca go pamięć podręczna. */
@FunctionalInterface
public interface MovieCatalog {
    /** Dane filmu albo {@link IllegalArgumentException} "unknown movie: id" - niezależnie od źródła. */
    MovieDetails find(String id);
}

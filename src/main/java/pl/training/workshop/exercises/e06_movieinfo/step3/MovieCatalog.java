package pl.training.workshop.exercises.e06_movieinfo.step3;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;

/** Krok 3: port katalogu bez zmian. */
@FunctionalInterface
public interface MovieCatalog {
    /** Dane filmu albo {@link IllegalArgumentException} "unknown movie: id" - niezależnie od źródła. */
    MovieDetails find(String id);
}

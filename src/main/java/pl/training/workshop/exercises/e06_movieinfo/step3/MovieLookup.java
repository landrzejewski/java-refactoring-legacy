package pl.training.workshop.exercises.e06_movieinfo.step3;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.MovieSource;
import pl.training.workshop.exercises.e06_movieinfo.User;

/** Krok 3: wspólny kontrakt serwisu i strażnika uprawnień - bez zmian. */
public interface MovieLookup {
    MovieDetails lookup(User user, MovieSource source, String id);

    int runtime(User user, MovieSource source, String id);
}

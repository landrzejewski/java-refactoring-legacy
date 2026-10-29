package pl.training.workshop.exercises.e06_movieinfo.step2;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.MovieSource;
import pl.training.workshop.exercises.e06_movieinfo.User;

/**
 * Krok 2: Extract Interface z serwisu. Ten sam kontrakt ma prawdziwy serwis i strażnik uprawnień,
 * który go owija - klient nie wie, z którym rozmawia.
 */
public interface MovieLookup {
    MovieDetails lookup(User user, MovieSource source, String id);

    int runtime(User user, MovieSource source, String id);
}

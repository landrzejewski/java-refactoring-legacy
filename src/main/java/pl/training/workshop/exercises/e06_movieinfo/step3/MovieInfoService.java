package pl.training.workshop.exercises.e06_movieinfo.step3;

import java.util.Map;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.MovieSource;
import pl.training.workshop.exercises.e06_movieinfo.User;

/** Krok 3: prawdziwy podmiot - wybiera katalog dla źródła, nic więcej. Bez zmian. */
public final class MovieInfoService implements MovieLookup {
    private final Map<MovieSource, MovieCatalog> catalogs;

    public MovieInfoService(Map<MovieSource, MovieCatalog> catalogs) {
        this.catalogs = Map.copyOf(catalogs);
    }

    @Override
    public MovieDetails lookup(User user, MovieSource source, String id) {
        return catalogs.get(source).find(id);
    }

    @Override
    public int runtime(User user, MovieSource source, String id) {
        return lookup(user, source, id).runtimeMinutes();
    }
}

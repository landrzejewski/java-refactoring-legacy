package pl.training.workshop.exercises.e06_movieinfo.step2;

import java.util.Map;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.MovieSource;
import pl.training.workshop.exercises.e06_movieinfo.User;

/**
 * Krok 2: prawdziwy podmiot. Serwis robi już tylko swoje: wybiera katalog dla źródła. Nie zna
 * uprawnień (parametr {@code user} jest częścią wspólnego kontraktu, używa go strażnik) ani
 * pamięci podręcznej - obie odpowiedzialności siedzą w zastępcach o tych samych interfejsach.
 */
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

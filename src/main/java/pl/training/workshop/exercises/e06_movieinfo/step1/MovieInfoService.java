package pl.training.workshop.exercises.e06_movieinfo.step1;

import java.util.HashMap;
import java.util.Map;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.MovieSource;
import pl.training.workshop.exercises.e06_movieinfo.User;

/**
 * Krok 1: serwis zależy wyłącznie od portu {@link MovieCatalog} - nie importuje już klienta OMDb
 * ani starej bazy. Zostały dwa zapachy: sprawdzenie uprawnień i pamięć podręczna, powtórzone
 * w każdej metodzie publicznej.
 */
public final class MovieInfoService {
    private final Map<MovieSource, MovieCatalog> catalogs;
    private final Map<String, MovieDetails> cache = new HashMap<>();

    public MovieInfoService(Map<MovieSource, MovieCatalog> catalogs) {
        this.catalogs = Map.copyOf(catalogs);
    }

    public MovieDetails lookup(User user, MovieSource source, String id) {
        if (!user.staff()) {
            throw new IllegalStateException("brak uprawnien: " + user.name());
        }
        String key = source + ":" + id;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        MovieDetails details = catalogs.get(source).find(id);
        cache.put(key, details);
        return details;
    }

    public int runtime(User user, MovieSource source, String id) {
        if (!user.staff()) {
            throw new IllegalStateException("brak uprawnien: " + user.name());
        }
        String key = source + ":" + id;
        if (cache.containsKey(key)) {
            return cache.get(key).runtimeMinutes();
        }
        return catalogs.get(source).find(id).runtimeMinutes();
    }
}

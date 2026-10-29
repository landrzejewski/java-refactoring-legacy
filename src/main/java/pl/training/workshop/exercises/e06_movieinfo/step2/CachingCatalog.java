package pl.training.workshop.exercises.e06_movieinfo.step2;

import java.util.HashMap;
import java.util.Map;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;

/**
 * Krok 2: pamięć podręczna jako zastępca katalogu. Ma ten sam interfejs co adapter, więc serwis
 * nie wie, czy pyta sieć, czy pamięć. Nieudane wyszukanie (wyjątek) nie jest zapamiętywane -
 * tak samo jak w start.
 */
public final class CachingCatalog implements MovieCatalog {
    private final MovieCatalog target;
    private final Map<String, MovieDetails> cache = new HashMap<>();

    public CachingCatalog(MovieCatalog target) {
        this.target = target;
    }

    @Override
    public MovieDetails find(String id) {
        MovieDetails cached = cache.get(id);
        if (cached != null) {
            return cached;
        }
        MovieDetails details = target.find(id);
        cache.put(id, details);
        return details;
    }
}

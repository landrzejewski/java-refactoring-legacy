package pl.training.workshop.exercises.e06_movieinfo.step3;

import java.util.HashMap;
import java.util.Map;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;

/** Krok 3: pamięć podręczna jako zastępca katalogu - bez zmian. */
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

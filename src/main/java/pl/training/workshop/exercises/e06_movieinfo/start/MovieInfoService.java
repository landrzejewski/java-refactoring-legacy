package pl.training.workshop.exercises.e06_movieinfo.start;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRow;
import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows;
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.MovieSource;
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient;
import pl.training.workshop.exercises.e06_movieinfo.User;

/**
 * Start: serwis zna wszystko naraz - surowy format odpowiedzi OMDb ("155 min", "8.0/10"),
 * konwencje starej tabeli (ocena x10), zasadę "tylko personel", pamięć podręczną i tabelę
 * kategorii wiekowych. Sprawdzenie uprawnień i cache są powtórzone w każdej metodzie
 * publicznej, a nowe źródło danych albo nowa reguła dostępu oznacza edycję tej klasy.
 */
public final class MovieInfoService {
    private final OmdbLikeClient omdb;
    private final LegacyMovieRows legacy;
    private final Map<String, MovieDetails> cache = new HashMap<>();

    public MovieInfoService(OmdbLikeClient omdb, LegacyMovieRows legacy) {
        this.omdb = omdb;
        this.legacy = legacy;
    }

    public MovieDetails lookup(User user, MovieSource source, String id) {
        if (!user.staff()) {
            throw new IllegalStateException("brak uprawnien: " + user.name());
        }
        String key = source + ":" + id;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        MovieDetails details = fetch(source, id);
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
        return fetch(source, id).runtimeMinutes();
    }

    private MovieDetails fetch(MovieSource source, String id) {
        if (source == MovieSource.OMDB) {
            Map<String, String> raw = omdb.byId(id);
            if ("False".equals(raw.get("Response"))) {
                throw new IllegalArgumentException("unknown movie: " + id);
            }
            AgeRatingTable table = AgeRatingTable.load();
            String rating = raw.get("imdbRating");
            return new MovieDetails(
                    raw.get("Title"),
                    Integer.parseInt(raw.get("Year")),
                    Integer.parseInt(raw.get("Runtime").replace(" min", "")),
                    new BigDecimal(rating.substring(0, rating.indexOf('/'))),
                    table.categoryFor("OMDB", raw.get("Rated")));
        }
        LegacyMovieRow row = legacy.find(id);
        if (row == null) {
            throw new IllegalArgumentException("unknown movie: " + id);
        }
        AgeRatingTable table = AgeRatingTable.load();
        return new MovieDetails(
                row.title(),
                row.year(),
                row.minutes(),
                BigDecimal.valueOf(row.ratingTimesTen(), 1),
                table.categoryFor("LEGACY", row.ageCode()));
    }
}

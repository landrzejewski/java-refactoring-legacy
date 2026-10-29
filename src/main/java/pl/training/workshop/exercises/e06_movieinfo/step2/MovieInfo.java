package pl.training.workshop.exercises.e06_movieinfo.step2;

import java.util.Map;

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows;
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.MovieSource;
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient;
import pl.training.workshop.exercises.e06_movieinfo.User;

/**
 * Krok 2: korzeń kompozycji składa łańcuch zastępców. Kolejność jest zachowaniem: strażnik na
 * zewnątrz, żeby gość nie trafił nawet do pamięci podręcznej; pamięć tuż przed adapterem,
 * żeby zapamiętywać już przetłumaczone dane.
 */
public final class MovieInfo {
    private final MovieLookup lookup;

    public MovieInfo(OmdbLikeClient omdb, LegacyMovieRows legacy) {
        this.lookup = new AuthorizingLookup(new MovieInfoService(Map.of(
                MovieSource.OMDB, new CachingCatalog(new OmdbCatalogAdapter(omdb)),
                MovieSource.LEGACY, new CachingCatalog(new LegacyCatalogAdapter(legacy)))));
    }

    public static MovieInfo create() {
        return new MovieInfo(new OmdbLikeClient(), new LegacyMovieRows());
    }

    public MovieDetails lookup(User user, MovieSource source, String id) {
        return lookup.lookup(user, source, id);
    }

    public int runtime(User user, MovieSource source, String id) {
        return lookup.runtime(user, source, id);
    }
}

package pl.training.workshop.exercises.e06_movieinfo.step3;

import java.util.Map;

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows;
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.MovieSource;
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient;
import pl.training.workshop.exercises.e06_movieinfo.User;

/**
 * Krok 3: korzeń kompozycji decyduje o cyklu życia tabeli - jawnie przekazuje jedyną instancję.
 * Łańcuch zastępców jak w kroku 2: strażnik na zewnątrz, pamięć podręczna przed adapterem.
 */
public final class MovieInfo {
    private final MovieLookup lookup;

    public MovieInfo(OmdbLikeClient omdb, LegacyMovieRows legacy) {
        AgeRatings ratings = AgeRatingTable.INSTANCE;
        this.lookup = new AuthorizingLookup(new MovieInfoService(Map.of(
                MovieSource.OMDB, new CachingCatalog(new OmdbCatalogAdapter(omdb, ratings)),
                MovieSource.LEGACY, new CachingCatalog(new LegacyCatalogAdapter(legacy, ratings)))));
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

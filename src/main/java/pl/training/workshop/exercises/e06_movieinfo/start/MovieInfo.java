package pl.training.workshop.exercises.e06_movieinfo.start;

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows;
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.MovieSource;
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient;
import pl.training.workshop.exercises.e06_movieinfo.User;

/** Start: punkt wejścia sceny - panel informacji o filmie w kasie. Przez niego woła test. */
public final class MovieInfo {
    private final MovieInfoService service;

    public MovieInfo(OmdbLikeClient omdb, LegacyMovieRows legacy) {
        this.service = new MovieInfoService(omdb, legacy);
    }

    public static MovieInfo create() {
        return new MovieInfo(new OmdbLikeClient(), new LegacyMovieRows());
    }

    public MovieDetails lookup(User user, MovieSource source, String id) {
        return service.lookup(user, source, id);
    }

    public int runtime(User user, MovieSource source, String id) {
        return service.runtime(user, source, id);
    }
}

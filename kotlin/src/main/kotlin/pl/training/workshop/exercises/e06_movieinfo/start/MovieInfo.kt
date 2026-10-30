package pl.training.workshop.exercises.e06_movieinfo.start

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.MovieSource
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient
import pl.training.workshop.exercises.e06_movieinfo.User

/** Start: punkt wejścia sceny - panel informacji o filmie w kasie. Przez niego woła test. */
class MovieInfo(omdb: OmdbLikeClient, legacy: LegacyMovieRows) {
    private val service = MovieInfoService(omdb, legacy)

    fun lookup(user: User, source: MovieSource, id: String): MovieDetails = service.lookup(user, source, id)

    fun runtime(user: User, source: MovieSource, id: String): Int = service.runtime(user, source, id)

    companion object {
        fun create(): MovieInfo = MovieInfo(OmdbLikeClient(), LegacyMovieRows())
    }
}

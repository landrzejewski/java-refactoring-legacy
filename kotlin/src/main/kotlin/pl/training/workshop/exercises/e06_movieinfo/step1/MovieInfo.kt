package pl.training.workshop.exercises.e06_movieinfo.step1

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.MovieSource
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient
import pl.training.workshop.exercises.e06_movieinfo.User

/** Krok 1: korzeń kompozycji - tu, a nie w serwisie, każde źródło dostaje swój adapter. */
class MovieInfo(omdb: OmdbLikeClient, legacy: LegacyMovieRows) {
    private val service = MovieInfoService(
        mapOf(
            MovieSource.OMDB to OmdbCatalogAdapter(omdb),
            MovieSource.LEGACY to LegacyCatalogAdapter(legacy)
        )
    )

    fun lookup(user: User, source: MovieSource, id: String): MovieDetails = service.lookup(user, source, id)

    fun runtime(user: User, source: MovieSource, id: String): Int = service.runtime(user, source, id)

    companion object {
        fun create(): MovieInfo = MovieInfo(OmdbLikeClient(), LegacyMovieRows())
    }
}

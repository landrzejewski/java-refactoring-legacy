package pl.training.workshop.exercises.e06_movieinfo.step3

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.MovieSource
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient
import pl.training.workshop.exercises.e06_movieinfo.User

/**
 * Krok 3: korzeń kompozycji decyduje o cyklu życia tabeli - jawnie przekazuje jedyną instancję.
 * Łańcuch zastępców jak w kroku 2: strażnik na zewnątrz, pamięć podręczna przed adapterem.
 */
class MovieInfo(omdb: OmdbLikeClient, legacy: LegacyMovieRows) {
    private val lookup: MovieLookup

    init {
        val ratings: AgeRatings = AgeRatingTable.INSTANCE
        lookup = AuthorizingLookup(
            MovieInfoService(
                mapOf(
                    MovieSource.OMDB to CachingCatalog(OmdbCatalogAdapter(omdb, ratings)),
                    MovieSource.LEGACY to CachingCatalog(LegacyCatalogAdapter(legacy, ratings))
                )
            )
        )
    }

    fun lookup(user: User, source: MovieSource, id: String): MovieDetails = lookup.lookup(user, source, id)

    fun runtime(user: User, source: MovieSource, id: String): Int = lookup.runtime(user, source, id)

    companion object {
        fun create(): MovieInfo = MovieInfo(OmdbLikeClient(), LegacyMovieRows())
    }
}

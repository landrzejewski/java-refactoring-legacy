package pl.training.workshop.exercises.e06_movieinfo.step2

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.MovieSource
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient
import pl.training.workshop.exercises.e06_movieinfo.User

/**
 * Krok 2: korzeń kompozycji składa łańcuch zastępców. Kolejność jest zachowaniem: strażnik na
 * zewnątrz, żeby gość nie trafił nawet do pamięci podręcznej; pamięć tuż przed adapterem,
 * żeby zapamiętywać już przetłumaczone dane.
 */
class MovieInfo(omdb: OmdbLikeClient, legacy: LegacyMovieRows) {
    private val lookup: MovieLookup = AuthorizingLookup(
        MovieInfoService(
            mapOf(
                MovieSource.OMDB to CachingCatalog(OmdbCatalogAdapter(omdb)),
                MovieSource.LEGACY to CachingCatalog(LegacyCatalogAdapter(legacy))
            )
        )
    )

    fun lookup(user: User, source: MovieSource, id: String): MovieDetails = lookup.lookup(user, source, id)

    fun runtime(user: User, source: MovieSource, id: String): Int = lookup.runtime(user, source, id)

    companion object {
        fun create(): MovieInfo = MovieInfo(OmdbLikeClient(), LegacyMovieRows())
    }
}

package pl.training.workshop.exercises.e06_movieinfo.step3

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.MovieSource
import pl.training.workshop.exercises.e06_movieinfo.User

/** Krok 3: prawdziwy podmiot - wybiera katalog dla źródła, nic więcej. Bez zmian. */
class MovieInfoService(catalogs: Map<MovieSource, MovieCatalog>) : MovieLookup {
    private val catalogs = catalogs.toMap()

    override fun lookup(user: User, source: MovieSource, id: String): MovieDetails = catalogs.getValue(source).find(id)

    override fun runtime(user: User, source: MovieSource, id: String): Int = lookup(user, source, id).runtimeMinutes
}

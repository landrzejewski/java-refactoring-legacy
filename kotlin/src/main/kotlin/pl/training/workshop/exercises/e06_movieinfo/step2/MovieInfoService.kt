package pl.training.workshop.exercises.e06_movieinfo.step2

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.MovieSource
import pl.training.workshop.exercises.e06_movieinfo.User

/**
 * Krok 2: prawdziwy podmiot. Serwis robi już tylko swoje: wybiera katalog dla źródła. Nie zna
 * uprawnień (parametr `user` jest częścią wspólnego kontraktu, używa go strażnik) ani
 * pamięci podręcznej - obie odpowiedzialności siedzą w zastępcach o tych samych interfejsach.
 */
class MovieInfoService(catalogs: Map<MovieSource, MovieCatalog>) : MovieLookup {
    private val catalogs = catalogs.toMap()

    override fun lookup(user: User, source: MovieSource, id: String): MovieDetails = catalogs.getValue(source).find(id)

    override fun runtime(user: User, source: MovieSource, id: String): Int = lookup(user, source, id).runtimeMinutes
}

package pl.training.workshop.exercises.e06_movieinfo.step1

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.MovieSource
import pl.training.workshop.exercises.e06_movieinfo.User

/**
 * Krok 1: serwis zależy wyłącznie od portu [MovieCatalog] - nie importuje już klienta OMDb
 * ani starej bazy. Zostały dwa zapachy: sprawdzenie uprawnień i pamięć podręczna, powtórzone
 * w każdej metodzie publicznej.
 */
class MovieInfoService(catalogs: Map<MovieSource, MovieCatalog>) {
    private val catalogs = catalogs.toMap()
    private val cache = HashMap<String, MovieDetails>()

    fun lookup(user: User, source: MovieSource, id: String): MovieDetails {
        if (!user.staff) {
            throw IllegalStateException("brak uprawnien: ${user.name}")
        }
        val key = "$source:$id"
        if (key in cache) {
            return cache.getValue(key)
        }
        val details = catalogs.getValue(source).find(id)
        cache[key] = details
        return details
    }

    fun runtime(user: User, source: MovieSource, id: String): Int {
        if (!user.staff) {
            throw IllegalStateException("brak uprawnien: ${user.name}")
        }
        val key = "$source:$id"
        if (key in cache) {
            return cache.getValue(key).runtimeMinutes
        }
        return catalogs.getValue(source).find(id).runtimeMinutes
    }
}

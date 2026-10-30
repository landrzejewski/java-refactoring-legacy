package pl.training.workshop.exercises.e06_movieinfo.step2

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails

/**
 * Krok 2: pamięć podręczna jako zastępca katalogu. Ma ten sam interfejs co adapter, więc serwis
 * nie wie, czy pyta sieć, czy pamięć. Nieudane wyszukanie (wyjątek) nie jest zapamiętywane -
 * tak samo jak w start.
 */
class CachingCatalog(private val target: MovieCatalog) : MovieCatalog {
    private val cache = HashMap<String, MovieDetails>()

    override fun find(id: String): MovieDetails {
        val cached = cache[id]
        if (cached != null) {
            return cached
        }
        val details = target.find(id)
        cache[id] = details
        return details
    }
}

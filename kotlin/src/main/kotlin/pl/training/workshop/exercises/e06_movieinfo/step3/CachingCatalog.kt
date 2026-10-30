package pl.training.workshop.exercises.e06_movieinfo.step3

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails

/** Krok 3: pamięć podręczna jako zastępca katalogu - bez zmian. */
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

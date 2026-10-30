package pl.training.workshop.exercises.e06_movieinfo.step2

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.MovieSource
import pl.training.workshop.exercises.e06_movieinfo.User

/**
 * Krok 2: Extract Interface z serwisu. Ten sam kontrakt ma prawdziwy serwis i strażnik uprawnień,
 * który go owija - klient nie wie, z którym rozmawia.
 */
interface MovieLookup {
    fun lookup(user: User, source: MovieSource, id: String): MovieDetails

    fun runtime(user: User, source: MovieSource, id: String): Int
}

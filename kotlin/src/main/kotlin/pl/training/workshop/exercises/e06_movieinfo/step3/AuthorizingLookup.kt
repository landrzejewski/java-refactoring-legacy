package pl.training.workshop.exercises.e06_movieinfo.step3

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.MovieSource
import pl.training.workshop.exercises.e06_movieinfo.User

/** Krok 3: strażnik dostępu jako zastępca serwisu - bez zmian. */
class AuthorizingLookup(private val target: MovieLookup) : MovieLookup {
    override fun lookup(user: User, source: MovieSource, id: String): MovieDetails {
        requireStaff(user)
        return target.lookup(user, source, id)
    }

    override fun runtime(user: User, source: MovieSource, id: String): Int {
        requireStaff(user)
        return target.runtime(user, source, id)
    }

    private fun requireStaff(user: User) {
        if (!user.staff) {
            throw IllegalStateException("brak uprawnien: ${user.name}")
        }
    }
}

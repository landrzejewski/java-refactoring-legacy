package pl.training.workshop.exercises.e06_movieinfo

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

import pl.training.workshop.support.Scene

/**
 * Tabela decyzji panelu informacji o filmie: oba źródła, każdy kod wieku, nieznany kod, nieznany
 * film w każdym źródle, odmowa dla gościa (zanim cokolwiek zostanie wyszukane) i sam czas trwania.
 */
class E06EquivalenceTest {
    /** Pytanie do panelu: kto, skąd, o jaki film i czy tylko o czas trwania. */
    data class Query(val user: User, val source: MovieSource, val id: String, val runtimeOnly: Boolean)

    @TestFactory
    fun everyStepAnswersTheSame(): List<DynamicTest> = Scene.variants<Query, String>()
        .variant("start") { q ->
            ask(
                q,
                pl.training.workshop.exercises.e06_movieinfo.start.MovieInfo.create()::lookup,
                pl.training.workshop.exercises.e06_movieinfo.start.MovieInfo.create()::runtime
            )
        }
        .variant("step1") { q ->
            ask(
                q,
                pl.training.workshop.exercises.e06_movieinfo.step1.MovieInfo.create()::lookup,
                pl.training.workshop.exercises.e06_movieinfo.step1.MovieInfo.create()::runtime
            )
        }
        .variant("step2") { q ->
            ask(
                q,
                pl.training.workshop.exercises.e06_movieinfo.step2.MovieInfo.create()::lookup,
                pl.training.workshop.exercises.e06_movieinfo.step2.MovieInfo.create()::runtime
            )
        }
        .variant("step3") { q ->
            ask(
                q,
                pl.training.workshop.exercises.e06_movieinfo.step3.MovieInfo.create()::lookup,
                pl.training.workshop.exercises.e06_movieinfo.step3.MovieInfo.create()::runtime
            )
        }
        .expect("OMDb PG-13 -> 12+", lookup(STAFF, MovieSource.OMDB, "tt1160419"), "Dune (2021), 155 min, ocena 8.0/10, kategoria 12+")
        .expect("OMDb PG -> 7+", lookup(STAFF, MovieSource.OMDB, "tt4520988"), "Frozen II (2019), 103 min, ocena 6.8/10, kategoria 7+")
        .expect(
            "OMDb R -> 16+",
            lookup(STAFF, MovieSource.OMDB, "tt15398776"),
            "Oppenheimer (2023), 180 min, ocena 8.3/10, kategoria 16+"
        )
        .expect(
            "OMDb Not Rated -> brak",
            lookup(STAFF, MovieSource.OMDB, "tt0077269"),
            "Amator (1979), 117 min, ocena 7.8/10, kategoria brak"
        )
        .expect(
            "stara baza ocena 80 -> 8.0, kod 12",
            lookup(STAFF, MovieSource.LEGACY, "M-001"),
            "Diuna (2021), 155 min, ocena 8.0/10, kategoria 12+"
        )
        .expect(
            "stara baza kod 7",
            lookup(STAFF, MovieSource.LEGACY, "M-002"),
            "Kraina Lodu II (2019), 103 min, ocena 6.8/10, kategoria 7+"
        )
        .expect(
            "stara baza kod 0",
            lookup(STAFF, MovieSource.LEGACY, "M-003"),
            "Amator (1979), 117 min, ocena 7.8/10, kategoria bez ograniczeń"
        )
        .expect(
            "stara baza kod 16",
            lookup(STAFF, MovieSource.LEGACY, "M-004"),
            "Oppenheimer (2023), 180 min, ocena 8.3/10, kategoria 16+"
        )
        .expect(
            "stara baza nieznany kod wieku",
            lookup(STAFF, MovieSource.LEGACY, "M-005"),
            "Seans testowy (2026), 5 min, ocena 5.0/10, kategoria brak"
        )
        .expect("OMDb nieznany film", lookup(STAFF, MovieSource.OMDB, "tt0000000"), "ERROR: unknown movie: tt0000000")
        .expect("stara baza nieznany film", lookup(STAFF, MovieSource.LEGACY, "M-999"), "ERROR: unknown movie: M-999")
        .expect("gosc OMDb", lookup(GUEST, MovieSource.OMDB, "tt1160419"), "ERROR: brak uprawnien: gosc")
        .expect("gosc: odmowa przed wyszukaniem nieznanego filmu", lookup(GUEST, MovieSource.LEGACY, "M-999"), "ERROR: brak uprawnien: gosc")
        .expect("czas trwania OMDb", runtime(STAFF, MovieSource.OMDB, "tt1160419"), "155")
        .expect("czas trwania stara baza", runtime(STAFF, MovieSource.LEGACY, "M-002"), "103")
        .expect("czas trwania dla goscia", runtime(GUEST, MovieSource.OMDB, "tt1160419"), "ERROR: brak uprawnien: gosc")
        .tests()

    companion object {
        private val STAFF = User.staff("kasjer")
        private val GUEST = User.guest("gosc")

        private fun lookup(user: User, source: MovieSource, id: String) = Query(user, source, id, false)

        private fun runtime(user: User, source: MovieSource, id: String) = Query(user, source, id, true)

        private fun ask(
            query: Query,
            lookup: (User, MovieSource, String) -> MovieDetails,
            runtime: (User, MovieSource, String) -> Int
        ): String {
            val answer: (Query) -> String = if (query.runtimeOnly) {
                { q -> runtime(q.user, q.source, q.id).toString() }
            } else {
                { q -> lookup(q.user, q.source, q.id).describe() }
            }
            return try {
                answer(query)
            } catch (exception: IllegalArgumentException) {
                "ERROR: ${exception.message}"
            } catch (exception: IllegalStateException) {
                "ERROR: ${exception.message}"
            }
        }
    }
}

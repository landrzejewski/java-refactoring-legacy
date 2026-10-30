package pl.training.workshop.exercises.e06_movieinfo

import java.math.BigDecimal

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import pl.training.workshop.exercises.e06_movieinfo.step3.AgeRatingTable
import pl.training.workshop.exercises.e06_movieinfo.step3.AuthorizingLookup
import pl.training.workshop.exercises.e06_movieinfo.step3.CachingCatalog
import pl.training.workshop.exercises.e06_movieinfo.step3.MovieCatalog
import pl.training.workshop.exercises.e06_movieinfo.step3.MovieInfo
import pl.training.workshop.exercises.e06_movieinfo.step3.MovieInfoService
import pl.training.workshop.exercises.e06_movieinfo.step3.MovieLookup
import pl.training.workshop.exercises.e06_movieinfo.step3.OmdbCatalogAdapter

/**
 * Kontrakt rozwiązania: zastępcy oszczędzają wywołania źródeł i zatrzymują gościa, tabela
 * powstaje raz, a każdy element da się złożyć w teście bez globalnego stanu.
 */
class E06SolutionTest {
    /** Pomiar w start: każde wyszukanie czyta plik od nowa. Po "warsztat.sh jump 3" test jest pomijany. */
    @Test
    fun startReadsTheAgeTableOnEveryLookup() {
        assumeTrue(
            !pl.training.workshop.exercises.e06_movieinfo.start.AgeRatingTable::class.java.isEnum,
            "start nie czyta już pliku przy każdym wywołaniu"
        )
        val start = pl.training.workshop.exercises.e06_movieinfo.start.MovieInfo.create()
        val before = AgeRatingResource.reads()
        start.lookup(STAFF, MovieSource.OMDB, "tt1160419")
        start.lookup(STAFF, MovieSource.LEGACY, "M-001")
        start.runtime(STAFF, MovieSource.OMDB, "tt4520988")
        assertEquals(3, AgeRatingResource.reads() - before)
    }

    @Test
    fun secondLookupDoesNotCallTheSource() {
        val omdb = OmdbLikeClient()
        val legacy = LegacyMovieRows()
        val info = MovieInfo(omdb, legacy)

        info.lookup(STAFF, MovieSource.OMDB, "tt1160419")
        info.lookup(STAFF, MovieSource.OMDB, "tt1160419")
        info.runtime(STAFF, MovieSource.OMDB, "tt1160419")
        info.runtime(STAFF, MovieSource.LEGACY, "M-001")
        info.lookup(STAFF, MovieSource.LEGACY, "M-001")

        assertEquals(1, omdb.calls(), "OMDb pytane raz o ten sam film")
        assertEquals(1, legacy.calls(), "stara baza pytana raz o ten sam film")
    }

    @Test
    fun guestNeverReachesTheSources() {
        val omdb = OmdbLikeClient()
        val legacy = LegacyMovieRows()
        val info = MovieInfo(omdb, legacy)

        assertThrows<IllegalStateException> { info.lookup(GUEST, MovieSource.OMDB, "tt1160419") }
        assertThrows<IllegalStateException> { info.runtime(GUEST, MovieSource.LEGACY, "M-001") }

        assertEquals(0, omdb.calls() + legacy.calls(), "odrzucone pytanie nie kosztuje zapytania do źródła")
    }

    @Test
    fun ageTableIsLoadedAtMostOnceForAnyNumberOfServices() {
        val before = AgeRatingResource.reads()
        repeat(3) {
            val info = MovieInfo.create()
            info.lookup(STAFF, MovieSource.OMDB, "tt1160419")
            info.lookup(STAFF, MovieSource.LEGACY, "M-002")
        }
        assertTrue(AgeRatingResource.reads() - before <= 1, "plik czytany najwyżej raz na JVM")
        assertSame(AgeRatingTable.INSTANCE, AgeRatingTable.valueOf("INSTANCE"))
    }

    @Test
    fun adapterAcceptsAnotherTableWithoutGlobalState() {
        val adapter = OmdbCatalogAdapter(OmdbLikeClient()) { _, code -> "test:$code" }
        assertEquals("test:PG-13", adapter.find("tt1160419").ageCategory)
        assertEquals(
            "12+", OmdbCatalogAdapter(OmdbLikeClient()).find("tt1160419").ageCategory,
            "domyślna tabela bez zmian"
        )
    }

    @Test
    fun serviceIsAPlainSubjectComposableWithAFakeCatalog() {
        val fake = MovieCatalog { MovieDetails("Testowy", 2026, 90, BigDecimal("5.0"), "brak") }
        val service = MovieInfoService(mapOf(MovieSource.OMDB to fake))

        assertEquals(90, service.runtime(GUEST, MovieSource.OMDB, "x"), "reguła dostępu nie jest w serwisie")
        assertEquals(90, AuthorizingLookup(service).runtime(STAFF, MovieSource.OMDB, "x"))
        assertThrows<IllegalStateException> { AuthorizingLookup(service).runtime(GUEST, MovieSource.OMDB, "x") }
    }

    @Test
    fun everyProxyWrapsExactlyOneTargetOfItsOwnInterface() {
        assertEquals(1, fieldsOfType(CachingCatalog::class.java, MovieCatalog::class.java), "cache owija katalog")
        assertEquals(1, fieldsOfType(AuthorizingLookup::class.java, MovieLookup::class.java), "strażnik owija serwis")
        assertTrue(MovieCatalog::class.java.isAssignableFrom(CachingCatalog::class.java))
        assertTrue(MovieLookup::class.java.isAssignableFrom(AuthorizingLookup::class.java))
    }

    companion object {
        private val STAFF = User.staff("kasjer")
        private val GUEST = User.guest("gosc")

        private fun fieldsOfType(owner: Class<*>, type: Class<*>): Int = owner.declaredFields.count { it.type == type }
    }
}

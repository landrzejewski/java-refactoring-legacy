package pl.training.workshop.exercises.e06_movieinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.training.workshop.exercises.e06_movieinfo.step3.AgeRatingTable;
import pl.training.workshop.exercises.e06_movieinfo.step3.AuthorizingLookup;
import pl.training.workshop.exercises.e06_movieinfo.step3.CachingCatalog;
import pl.training.workshop.exercises.e06_movieinfo.step3.MovieCatalog;
import pl.training.workshop.exercises.e06_movieinfo.step3.MovieInfo;
import pl.training.workshop.exercises.e06_movieinfo.step3.MovieInfoService;
import pl.training.workshop.exercises.e06_movieinfo.step3.MovieLookup;
import pl.training.workshop.exercises.e06_movieinfo.step3.OmdbCatalogAdapter;

/**
 * Kontrakt rozwiązania: zastępcy oszczędzają wywołania źródeł i zatrzymują gościa, tabela
 * powstaje raz, a każdy element da się złożyć w teście bez globalnego stanu.
 */
final class E06SolutionTest {
    private static final User STAFF = User.staff("kasjer");
    private static final User GUEST = User.guest("gosc");

    /** Pomiar w start: każde wyszukanie czyta plik od nowa. Po "warsztat.sh jump 3" test jest pomijany. */
    @Test
    void startReadsTheAgeTableOnEveryLookup() {
        assumeTrue(!pl.training.workshop.exercises.e06_movieinfo.start.AgeRatingTable.class.isEnum(),
                "start nie czyta już pliku przy każdym wywołaniu");
        var start = pl.training.workshop.exercises.e06_movieinfo.start.MovieInfo.create();
        int before = AgeRatingResource.reads();
        start.lookup(STAFF, MovieSource.OMDB, "tt1160419");
        start.lookup(STAFF, MovieSource.LEGACY, "M-001");
        start.runtime(STAFF, MovieSource.OMDB, "tt4520988");
        assertEquals(3, AgeRatingResource.reads() - before);
    }

    @Test
    void secondLookupDoesNotCallTheSource() {
        OmdbLikeClient omdb = new OmdbLikeClient();
        LegacyMovieRows legacy = new LegacyMovieRows();
        MovieInfo info = new MovieInfo(omdb, legacy);

        info.lookup(STAFF, MovieSource.OMDB, "tt1160419");
        info.lookup(STAFF, MovieSource.OMDB, "tt1160419");
        info.runtime(STAFF, MovieSource.OMDB, "tt1160419");
        info.runtime(STAFF, MovieSource.LEGACY, "M-001");
        info.lookup(STAFF, MovieSource.LEGACY, "M-001");

        assertEquals(1, omdb.calls(), "OMDb pytane raz o ten sam film");
        assertEquals(1, legacy.calls(), "stara baza pytana raz o ten sam film");
    }

    @Test
    void guestNeverReachesTheSources() {
        OmdbLikeClient omdb = new OmdbLikeClient();
        LegacyMovieRows legacy = new LegacyMovieRows();
        MovieInfo info = new MovieInfo(omdb, legacy);

        assertThrows(IllegalStateException.class, () -> info.lookup(GUEST, MovieSource.OMDB, "tt1160419"));
        assertThrows(IllegalStateException.class, () -> info.runtime(GUEST, MovieSource.LEGACY, "M-001"));

        assertEquals(0, omdb.calls() + legacy.calls(), "odrzucone pytanie nie kosztuje zapytania do źródła");
    }

    @Test
    void ageTableIsLoadedAtMostOnceForAnyNumberOfServices() {
        int before = AgeRatingResource.reads();
        for (int i = 0; i < 3; i++) {
            MovieInfo info = MovieInfo.create();
            info.lookup(STAFF, MovieSource.OMDB, "tt1160419");
            info.lookup(STAFF, MovieSource.LEGACY, "M-002");
        }
        assertTrue(AgeRatingResource.reads() - before <= 1, "plik czytany najwyżej raz na JVM");
        assertSame(AgeRatingTable.INSTANCE, AgeRatingTable.valueOf("INSTANCE"));
    }

    @Test
    void adapterAcceptsAnotherTableWithoutGlobalState() {
        OmdbCatalogAdapter adapter = new OmdbCatalogAdapter(new OmdbLikeClient(), (system, code) -> "test:" + code);
        assertEquals("test:PG-13", adapter.find("tt1160419").ageCategory());
        assertEquals("12+", new OmdbCatalogAdapter(new OmdbLikeClient()).find("tt1160419").ageCategory(),
                "domyślna tabela bez zmian");
    }

    @Test
    void serviceIsAPlainSubjectComposableWithAFakeCatalog() {
        MovieCatalog fake = id -> new MovieDetails("Testowy", 2026, 90, new BigDecimal("5.0"), "brak");
        MovieInfoService service = new MovieInfoService(Map.of(MovieSource.OMDB, fake));

        assertEquals(90, service.runtime(GUEST, MovieSource.OMDB, "x"), "reguła dostępu nie jest w serwisie");
        assertEquals(90, new AuthorizingLookup(service).runtime(STAFF, MovieSource.OMDB, "x"));
        assertThrows(IllegalStateException.class,
                () -> new AuthorizingLookup(service).runtime(GUEST, MovieSource.OMDB, "x"));
    }

    @Test
    void everyProxyWrapsExactlyOneTargetOfItsOwnInterface() {
        assertEquals(1, fieldsOfType(CachingCatalog.class, MovieCatalog.class), "cache owija katalog");
        assertEquals(1, fieldsOfType(AuthorizingLookup.class, MovieLookup.class), "strażnik owija serwis");
        assertTrue(MovieCatalog.class.isAssignableFrom(CachingCatalog.class));
        assertTrue(MovieLookup.class.isAssignableFrom(AuthorizingLookup.class));
    }

    private static long fieldsOfType(Class<?> owner, Class<?> type) {
        return Arrays.stream(owner.getDeclaredFields()).map(Field::getType).filter(type::equals).count();
    }
}

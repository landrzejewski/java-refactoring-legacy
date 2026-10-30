<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E06MovieInfo;

use BcMath\Number;
use Closure;
use LogicException;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use ReflectionClass;
use ReflectionNamedType;
use ReflectionProperty;
use Throwable;
use Training\Workshop\Exercises\E06MovieInfo\AgeRatingResource;
use Training\Workshop\Exercises\E06MovieInfo\LegacyMovieRows;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\OmdbLikeClient;
use Training\Workshop\Exercises\E06MovieInfo\Start;
use Training\Workshop\Exercises\E06MovieInfo\Step3\AgeRatings;
use Training\Workshop\Exercises\E06MovieInfo\Step3\AgeRatingTable;
use Training\Workshop\Exercises\E06MovieInfo\Step3\AuthorizingLookup;
use Training\Workshop\Exercises\E06MovieInfo\Step3\CachingCatalog;
use Training\Workshop\Exercises\E06MovieInfo\Step3\MovieCatalog;
use Training\Workshop\Exercises\E06MovieInfo\Step3\MovieInfo;
use Training\Workshop\Exercises\E06MovieInfo\Step3\MovieInfoService;
use Training\Workshop\Exercises\E06MovieInfo\Step3\MovieLookup;
use Training\Workshop\Exercises\E06MovieInfo\Step3\OmdbCatalogAdapter;
use Training\Workshop\Exercises\E06MovieInfo\User;

/**
 * Kontrakt rozwiązania: zastępcy oszczędzają wywołania źródeł i zatrzymują gościa, tabela
 * powstaje raz, a każdy element da się złożyć w teście bez globalnego stanu.
 */
final class E06SolutionTest extends TestCase
{
    /** Pomiar w start: każde wyszukanie czyta plik od nowa. Po "warsztat.sh jump 3" test jest pomijany. */
    #[Test]
    public function startReadsTheAgeTableOnEveryLookup(): void
    {
        if ((new ReflectionClass(Start\AgeRatingTable::class))->isEnum()) {
            self::markTestSkipped('start nie czyta już pliku przy każdym wywołaniu');
        }
        $start = Start\MovieInfo::create();
        $before = AgeRatingResource::reads();
        $start->lookup(User::staff('kasjer'), MovieSource::OMDB, 'tt1160419');
        $start->lookup(User::staff('kasjer'), MovieSource::LEGACY, 'M-001');
        $start->runtime(User::staff('kasjer'), MovieSource::OMDB, 'tt4520988');
        self::assertSame(3, AgeRatingResource::reads() - $before);
    }

    #[Test]
    public function secondLookupDoesNotCallTheSource(): void
    {
        $staff = User::staff('kasjer');
        $omdb = new OmdbLikeClient();
        $legacy = new LegacyMovieRows();
        $info = new MovieInfo($omdb, $legacy);

        $info->lookup($staff, MovieSource::OMDB, 'tt1160419');
        $info->lookup($staff, MovieSource::OMDB, 'tt1160419');
        $info->runtime($staff, MovieSource::OMDB, 'tt1160419');
        $info->runtime($staff, MovieSource::LEGACY, 'M-001');
        $info->lookup($staff, MovieSource::LEGACY, 'M-001');

        self::assertSame(1, $omdb->calls(), 'OMDb pytane raz o ten sam film');
        self::assertSame(1, $legacy->calls(), 'stara baza pytana raz o ten sam film');
    }

    #[Test]
    public function guestNeverReachesTheSources(): void
    {
        $guest = User::guest('gosc');
        $omdb = new OmdbLikeClient();
        $legacy = new LegacyMovieRows();
        $info = new MovieInfo($omdb, $legacy);

        self::assertSame(LogicException::class, get_debug_type(self::thrown(fn () => $info->lookup($guest, MovieSource::OMDB, 'tt1160419'))));
        self::assertSame(LogicException::class, get_debug_type(self::thrown(fn () => $info->runtime($guest, MovieSource::LEGACY, 'M-001'))));

        self::assertSame(0, $omdb->calls() + $legacy->calls(), 'odrzucone pytanie nie kosztuje zapytania do źródła');
    }

    #[Test]
    public function ageTableIsLoadedAtMostOnceForAnyNumberOfServices(): void
    {
        $staff = User::staff('kasjer');
        $before = AgeRatingResource::reads();
        for ($i = 0; $i < 3; $i++) {
            $info = MovieInfo::create();
            $info->lookup($staff, MovieSource::OMDB, 'tt1160419');
            $info->lookup($staff, MovieSource::LEGACY, 'M-002');
        }
        self::assertLessThanOrEqual(1, AgeRatingResource::reads() - $before, 'plik czytany najwyżej raz na proces');
        // Różnica względem Javy: AgeRatingTable.valueOf("INSTANCE") -> constant(), bo enum bez wartości nie ma from().
        self::assertSame(AgeRatingTable::INSTANCE, constant(AgeRatingTable::class . '::INSTANCE'));
    }

    #[Test]
    public function adapterAcceptsAnotherTableWithoutGlobalState(): void
    {
        $adapter = new OmdbCatalogAdapter(new OmdbLikeClient(), new class implements AgeRatings {
            public function categoryFor(string $system, string $code): string
            {
                return 'test:' . $code;
            }
        });
        self::assertSame('test:PG-13', $adapter->find('tt1160419')->ageCategory);
        self::assertSame('12+', (new OmdbCatalogAdapter(new OmdbLikeClient()))->find('tt1160419')->ageCategory, 'domyślna tabela bez zmian');
    }

    #[Test]
    public function serviceIsAPlainSubjectComposableWithAFakeCatalog(): void
    {
        $staff = User::staff('kasjer');
        $guest = User::guest('gosc');
        $fake = new class implements MovieCatalog {
            public function find(string $id): MovieDetails
            {
                return new MovieDetails('Testowy', 2026, 90, new Number('5.0'), 'brak');
            }
        };
        $service = new MovieInfoService([MovieSource::OMDB->name => $fake]);

        self::assertSame(90, $service->runtime($guest, MovieSource::OMDB, 'x'), 'reguła dostępu nie jest w serwisie');
        self::assertSame(90, (new AuthorizingLookup($service))->runtime($staff, MovieSource::OMDB, 'x'));
        self::assertSame(LogicException::class, get_debug_type(self::thrown(fn () => (new AuthorizingLookup($service))->runtime($guest, MovieSource::OMDB, 'x'))));
    }

    #[Test]
    public function everyProxyWrapsExactlyOneTargetOfItsOwnInterface(): void
    {
        self::assertSame(1, self::fieldsOfType(CachingCatalog::class, MovieCatalog::class), 'cache owija katalog');
        self::assertSame(1, self::fieldsOfType(AuthorizingLookup::class, MovieLookup::class), 'strażnik owija serwis');
        self::assertTrue(is_subclass_of(CachingCatalog::class, MovieCatalog::class));
        self::assertTrue(is_subclass_of(AuthorizingLookup::class, MovieLookup::class));
    }

    /** @param class-string $owner */
    private static function fieldsOfType(string $owner, string $type): int
    {
        return count(array_filter(
            (new ReflectionClass($owner))->getProperties(),
            fn (ReflectionProperty $field) => $field->getType() instanceof ReflectionNamedType && $field->getType()->getName() === $type,
        ));
    }

    /**
     * Odpowiednik {@code assertThrows}: zwraca wyjątek rzucony przez akcję (albo null); test porównuje dokładną klasę,
     * bo {@code InvalidArgumentException} w PHP dziedziczy po {@code LogicException}.
     *
     * @param Closure(): mixed $action
     */
    private static function thrown(Closure $action): ?Throwable
    {
        try {
            $action();
        } catch (Throwable $exception) {
            return $exception;
        }
        return null;
    }
}

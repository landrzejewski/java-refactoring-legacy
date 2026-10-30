<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E06MovieInfo;

use Closure;
use InvalidArgumentException;
use LogicException;
use PHPUnit\Framework\Attributes\DataProvider;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\Start;
use Training\Workshop\Exercises\E06MovieInfo\Step1;
use Training\Workshop\Exercises\E06MovieInfo\Step2;
use Training\Workshop\Exercises\E06MovieInfo\Step3;
use Training\Workshop\Exercises\E06MovieInfo\User;
use Training\Workshop\Tests\Support\Scene;

/**
 * Tabela decyzji panelu informacji o filmie: oba źródła, każdy kod wieku, nieznany kod, nieznany
 * film w każdym źródle, odmowa dla gościa (zanim cokolwiek zostanie wyszukane) i sam czas trwania.
 *
 * Różnica względem Javy: rekord {@code Query} to tu tablica z kluczami {@code user}, {@code source},
 * {@code id}, {@code runtimeOnly}, a interfejsy {@code Lookup}/{@code Runtime} to domknięcia
 * (first-class callable {@code MovieInfo::create()->lookup(...)}).
 */
final class E06EquivalenceTest extends TestCase
{
    /** @return array<string, array{Closure, array{user: User, source: MovieSource, id: string, runtimeOnly: bool}, string}> */
    public static function everyStep(): array
    {
        $staff = User::staff('kasjer');
        $guest = User::guest('gosc');
        return Scene::variants()
            ->variant('start', fn (array $q) => self::answer(
                $q,
                Start\MovieInfo::create()->lookup(...),
                Start\MovieInfo::create()->runtime(...),
            ))
            ->variant('step1', fn (array $q) => self::answer(
                $q,
                Step1\MovieInfo::create()->lookup(...),
                Step1\MovieInfo::create()->runtime(...),
            ))
            ->variant('step2', fn (array $q) => self::answer(
                $q,
                Step2\MovieInfo::create()->lookup(...),
                Step2\MovieInfo::create()->runtime(...),
            ))
            ->variant('step3', fn (array $q) => self::answer(
                $q,
                Step3\MovieInfo::create()->lookup(...),
                Step3\MovieInfo::create()->runtime(...),
            ))
            ->expect(
                'OMDb PG-13 -> 12+',
                self::lookup($staff, MovieSource::OMDB, 'tt1160419'),
                'Dune (2021), 155 min, ocena 8.0/10, kategoria 12+',
            )
            ->expect(
                'OMDb PG -> 7+',
                self::lookup($staff, MovieSource::OMDB, 'tt4520988'),
                'Frozen II (2019), 103 min, ocena 6.8/10, kategoria 7+',
            )
            ->expect(
                'OMDb R -> 16+',
                self::lookup($staff, MovieSource::OMDB, 'tt15398776'),
                'Oppenheimer (2023), 180 min, ocena 8.3/10, kategoria 16+',
            )
            ->expect(
                'OMDb Not Rated -> brak',
                self::lookup($staff, MovieSource::OMDB, 'tt0077269'),
                'Amator (1979), 117 min, ocena 7.8/10, kategoria brak',
            )
            ->expect(
                'stara baza ocena 80 -> 8.0, kod 12',
                self::lookup($staff, MovieSource::LEGACY, 'M-001'),
                'Diuna (2021), 155 min, ocena 8.0/10, kategoria 12+',
            )
            ->expect(
                'stara baza kod 7',
                self::lookup($staff, MovieSource::LEGACY, 'M-002'),
                'Kraina Lodu II (2019), 103 min, ocena 6.8/10, kategoria 7+',
            )
            ->expect(
                'stara baza kod 0',
                self::lookup($staff, MovieSource::LEGACY, 'M-003'),
                'Amator (1979), 117 min, ocena 7.8/10, kategoria bez ograniczeń',
            )
            ->expect(
                'stara baza kod 16',
                self::lookup($staff, MovieSource::LEGACY, 'M-004'),
                'Oppenheimer (2023), 180 min, ocena 8.3/10, kategoria 16+',
            )
            ->expect(
                'stara baza nieznany kod wieku',
                self::lookup($staff, MovieSource::LEGACY, 'M-005'),
                'Seans testowy (2026), 5 min, ocena 5.0/10, kategoria brak',
            )
            ->expect('OMDb nieznany film', self::lookup($staff, MovieSource::OMDB, 'tt0000000'), 'ERROR: unknown movie: tt0000000')
            ->expect('stara baza nieznany film', self::lookup($staff, MovieSource::LEGACY, 'M-999'), 'ERROR: unknown movie: M-999')
            ->expect('gosc OMDb', self::lookup($guest, MovieSource::OMDB, 'tt1160419'), 'ERROR: brak uprawnien: gosc')
            ->expect(
                'gosc: odmowa przed wyszukaniem nieznanego filmu',
                self::lookup($guest, MovieSource::LEGACY, 'M-999'),
                'ERROR: brak uprawnien: gosc',
            )
            ->expect('czas trwania OMDb', self::runtime($staff, MovieSource::OMDB, 'tt1160419'), '155')
            ->expect('czas trwania stara baza', self::runtime($staff, MovieSource::LEGACY, 'M-002'), '103')
            ->expect('czas trwania dla goscia', self::runtime($guest, MovieSource::OMDB, 'tt1160419'), 'ERROR: brak uprawnien: gosc')
            ->cases();
    }

    #[Test]
    #[DataProvider('everyStep')]
    public function everyStepAnswersTheSame(Closure $variant, array $query, string $expected): void
    {
        self::assertSame($expected, $variant($query));
    }

    /** @return array{user: User, source: MovieSource, id: string, runtimeOnly: bool} */
    private static function lookup(User $user, MovieSource $source, string $id): array
    {
        return ['user' => $user, 'source' => $source, 'id' => $id, 'runtimeOnly' => false];
    }

    /** @return array{user: User, source: MovieSource, id: string, runtimeOnly: bool} */
    private static function runtime(User $user, MovieSource $source, string $id): array
    {
        return ['user' => $user, 'source' => $source, 'id' => $id, 'runtimeOnly' => true];
    }

    /**
     * @param array{user: User, source: MovieSource, id: string, runtimeOnly: bool} $query
     * @param Closure(User, MovieSource, string): MovieDetails $lookup
     * @param Closure(User, MovieSource, string): int $runtime
     */
    private static function answer(array $query, Closure $lookup, Closure $runtime): string
    {
        $answer = $query['runtimeOnly']
            ? fn (array $q) => (string) $runtime($q['user'], $q['source'], $q['id'])
            : fn (array $q) => $lookup($q['user'], $q['source'], $q['id'])->describe();
        try {
            return $answer($query);
        } catch (InvalidArgumentException | LogicException $exception) {
            return 'ERROR: ' . $exception->getMessage();
        }
    }
}

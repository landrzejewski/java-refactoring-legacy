<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E08Series;

use Closure;
use DateTimeImmutable;
use InvalidArgumentException;
use PHPUnit\Framework\Attributes\DataProvider;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use Training\Workshop\Exercises\E08Series\SeriesRequest;
use Training\Workshop\Exercises\E08Series\Start;
use Training\Workshop\Exercises\E08Series\Step1;
use Training\Workshop\Exercises\E08Series\Step2;
use Training\Workshop\Shared\Money;
use Training\Workshop\Tests\Support\Scene;

/**
 * Seria tygodniowa z szablonu: wartości domyślne, własna cena, dubbing, inna sala na koniec,
 * premiera przy jednym tygodniu oraz kolejność błędów walidacji.
 * Scenariusz "premiera przy wielu tygodniach" celowo nie jest tu obecny: start ma w nim błąd
 * płytkiej kopii, który rozwiązanie świadomie naprawia - dokumentuje to {@see E08SolutionTest}.
 */
final class E08EquivalenceTest extends TestCase
{
    /** @return array<string, array{Closure, SeriesRequest, string}> */
    public static function everyStep(): array
    {
        $fridayEvening = new DateTimeImmutable('2026-11-06 18:00');
        $saturdayMorning = new DateTimeImmutable('2026-11-07 10:30');
        $sundayEvening = new DateTimeImmutable('2026-11-08 20:00');
        return Scene::variants()
            ->variant('start', fn (SeriesRequest $r) => self::describeAll(fn () => (new Start\SeriesPlanner())->plan($r)))
            ->variant('step1', fn (SeriesRequest $r) => self::describeAll(fn () => (new Step1\SeriesPlanner())->plan($r)))
            ->variant('step2', fn (SeriesRequest $r) => self::describeAll(fn () => (new Step2\SeriesPlanner())->plan($r)))
            ->expect(
                'IMAX, trzy tygodnie, wszystko domyślne',
                new SeriesRequest('Diuna', 'IMAX', null, $fridayEvening, 3, null, null, null, false, null),
                "Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+\n"
                . "Diuna IMAX sala A 2026-11-13T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+\n"
                . 'Diuna IMAX sala A 2026-11-20T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+',
            )
            ->expect(
                '3D z dubbingiem, bez limitu wieku, sala B',
                new SeriesRequest('Kraina Lodu', '3D', 'B', $saturdayMorning, 2, null, true, 0, false, null),
                "Kraina Lodu 3D sala B 2026-11-07T10:30 cena 32.00 doplaty {OKULARY=3.00, VIP=10.00} dubbing 0+\n"
                . 'Kraina Lodu 3D sala B 2026-11-14T10:30 cena 32.00 doplaty {OKULARY=3.00, VIP=10.00} dubbing 0+',
            )
            ->expect(
                '2D z własną ceną',
                new SeriesRequest('Amator', '2D', 'C', $sundayEvening, 1, Money::of('20.00'), false, null, false, null),
                'Amator 2D sala C 2026-11-08T20:00 cena 20.00 doplaty {VIP=10.00} napisy 12+',
            )
            ->expect(
                'ostatni tydzień w innej sali',
                new SeriesRequest('Diuna', 'IMAX', 'A', $fridayEvening, 4, null, null, null, false, 'D'),
                "Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+\n"
                . "Diuna IMAX sala A 2026-11-13T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+\n"
                . "Diuna IMAX sala A 2026-11-20T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+\n"
                . 'Diuna IMAX sala D 2026-11-27T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+',
            )
            ->expect(
                'jeden tydzień w innej sali: pierwszy seans jest ostatnim',
                new SeriesRequest('Amator', '2D', null, $sundayEvening, 1, null, null, null, false, 'D'),
                'Amator 2D sala D 2026-11-08T20:00 cena 25.00 doplaty {VIP=10.00} napisy 12+',
            )
            ->expect(
                'premiera przy jednym tygodniu',
                new SeriesRequest('Diuna', 'IMAX', null, $fridayEvening, 1, null, null, null, true, null),
                'Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {PREMIERA=5.00, VIP=10.00} napisy 12+',
            )
            ->expect(
                'własny limit wieku',
                new SeriesRequest('Diuna', 'IMAX', null, $fridayEvening, 1, null, null, 16, false, null),
                'Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {VIP=10.00} napisy 16+',
            )
            ->expect(
                'cena zero jest dozwolona (pokaz darmowy)',
                new SeriesRequest('Amator', '2D', null, $sundayEvening, 1, Money::zero(), null, null, false, null),
                'Amator 2D sala A 2026-11-08T20:00 cena 0.00 doplaty {VIP=10.00} napisy 12+',
            )
            ->expect(
                'zero tygodni',
                new SeriesRequest('Diuna', 'IMAX', null, $fridayEvening, 0, null, null, null, false, null),
                'ERROR: weeks must be positive',
            )
            ->expect(
                'pusty tytuł',
                new SeriesRequest('  ', 'IMAX', null, $fridayEvening, 2, null, null, null, false, null),
                'ERROR: title must not be blank',
            )
            ->expect(
                'nieznany format',
                new SeriesRequest('Diuna', '4DX', null, $fridayEvening, 2, null, null, null, false, null),
                'ERROR: unknown format: 4DX',
            )
            ->expect(
                'nieznany format mimo własnej ceny',
                new SeriesRequest('Diuna', '4DX', null, $fridayEvening, 2, Money::of('50.00'), null, null, false, null),
                'ERROR: unknown format: 4DX',
            )
            ->expect(
                'ujemna cena bazowa',
                new SeriesRequest('Diuna', 'IMAX', null, $fridayEvening, 2, Money::of('-1.00'), null, null, false, null),
                'ERROR: base price must not be negative',
            )
            ->expect(
                'zero tygodni i pusty tytuł: najpierw tygodnie',
                new SeriesRequest('', 'IMAX', null, $fridayEvening, 0, null, null, null, false, null),
                'ERROR: weeks must be positive',
            )
            ->expect(
                'pusty tytuł i zły format: najpierw tytuł',
                new SeriesRequest('', '4DX', null, $fridayEvening, 1, null, null, null, false, null),
                'ERROR: title must not be blank',
            )
            ->cases();
    }

    #[Test]
    #[DataProvider('everyStep')]
    public function everyStepPlansTheSameSeries(Closure $variant, SeriesRequest $request, string $expected): void
    {
        self::assertSame($expected, $variant($request));
    }

    /** @param Closure(): list<object> $series */
    public static function describeAll(Closure $series): string
    {
        try {
            return implode("\n", array_map(fn (object $screening) => $screening->describe(), $series()));
        } catch (InvalidArgumentException $exception) {
            return 'ERROR: ' . $exception->getMessage();
        }
    }
}

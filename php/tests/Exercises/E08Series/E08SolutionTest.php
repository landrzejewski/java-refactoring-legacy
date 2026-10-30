<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E08Series;

use DateTimeImmutable;
use InvalidArgumentException;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use ReflectionMethod;
use Training\Workshop\Exercises\E08Series\SeriesRequest;
use Training\Workshop\Exercises\E08Series\Start;
use Training\Workshop\Exercises\E08Series\Step2\Screening;
use Training\Workshop\Exercises\E08Series\Step2\SeriesPlanner;
use Training\Workshop\Shared\Money;

/**
 * Pułapka startu (współdzielona mapa dopłat) i kontrakt rozwiązania:
 * niezależne kopie, wartości domyślne w jednym miejscu, pola wymagane sprawdzane przy budowie.
 */
final class E08SolutionTest extends TestCase
{
    private static function fridayEvening(): DateTimeImmutable
    {
        return new DateTimeImmutable('2026-11-06 18:00');
    }

    private static function premiereSeries(): SeriesRequest
    {
        return new SeriesRequest('Diuna', 'IMAX', null, self::fridayEvening(), 3, null, null, null, true, null);
    }

    /** Pomiar w Start: kopie współdzielą mapę dopłat. Po "warsztat.sh jump 1" (Start ma już kopie) test jest pomijany. */
    #[Test]
    public function startLeaksThePremiereSurchargeIntoEveryWeekBecauseCopiesShareTheMap(): void
    {
        if (method_exists(Start\Screening::class, 'withSurcharge')) {
            self::markTestSkipped('start ma już niezależne kopie');
        }
        $weeks = (new Start\SeriesPlanner())->plan(self::premiereSeries());
        self::assertSame(
            [true, true, true],
            array_map(fn ($week) => isset($week->surcharges()['PREMIERA']), $weeks),
            'start: dopłata premierowa trafia do wszystkich tygodni (to błąd, nie kontrakt)',
        );
    }

    #[Test]
    public function solutionKeepsThePremiereSurchargeInTheFirstWeekOnly(): void
    {
        $weeks = (new SeriesPlanner())->plan(self::premiereSeries());
        self::assertSame(
            [true, false, false],
            array_map(fn (Screening $week) => isset($week->surcharges()['PREMIERA']), $weeks),
            'rozwiązanie: świadoma naprawa, każdy tydzień ma własną mapę dopłat',
        );
    }

    #[Test]
    public function copyIsIndependentFromTheTemplate(): void
    {
        $template = Screening::builder()->title('Diuna')->format('IMAX')->start(self::fridayEvening())->build();
        $premiere = $template->withSurcharge('PREMIERA', Money::of('5.00'));
        $nextWeek = $template->withStart(self::fridayEvening()->modify('+1 weeks'));

        self::assertArrayNotHasKey('PREMIERA', $template->surcharges(), 'szablon nie widzi zmiany w kopii');
        self::assertArrayHasKey('PREMIERA', $premiere->surcharges());
        self::assertEquals($template->surcharges(), $nextWeek->surcharges());
        // Różnica względem Javy: getter zwraca tablicę PHP (wartość, nie widok), więc zamiast
        // UnsupportedOperationException przy put() sprawdzamy, że zmiana zwróconej tablicy nie dotyka seansu.
        $surcharges = $template->surcharges();
        $surcharges['VIP'] = Money::zero();
        self::assertEquals(Money::of('10.00'), $template->surcharges()['VIP'], 'mapy dopłat nie da się zmienić z zewnątrz');
    }

    #[Test]
    public function defaultsLiveInTheBuilderOnly(): void
    {
        $screening = Screening::builder()->title('Kraina Lodu')->format('3D')->start(self::fridayEvening())->build();
        self::assertSame('A', $screening->hall());
        self::assertEquals(Money::of('32.00'), $screening->basePrice());
        self::assertEquals(['VIP' => Money::of('10.00'), 'OKULARY' => Money::of('3.00')], $screening->surcharges());
        self::assertTrue($screening->subtitles());
        self::assertFalse($screening->dubbing());
        self::assertSame(12, $screening->ageLimit());
    }

    #[Test]
    public function builderRejectsMissingRequiredFieldsWithAClearMessage(): void
    {
        self::assertSame('title must not be blank', self::errorOf(
            fn () => Screening::builder()->format('2D')->start(self::fridayEvening())->build(),
        ));
        self::assertSame('unknown format: null', self::errorOf(
            fn () => Screening::builder()->title('Amator')->start(self::fridayEvening())->build(),
        ));
        self::assertSame('start is required', self::errorOf(
            fn () => Screening::builder()->title('Amator')->format('2D')->build(),
        ));
    }

    #[Test]
    public function screeningCannotBeBuiltAroundTheBuilderWithNulls(): void
    {
        self::assertTrue(
            (new ReflectionMethod(Screening::class, '__construct'))->isPrivate(),
            'jedyna droga to Screening::builder()',
        );
    }

    private static function errorOf(callable $build): string
    {
        try {
            $build();
        } catch (InvalidArgumentException $exception) {
            return $exception->getMessage();
        }
        self::fail('expected InvalidArgumentException');
    }
}

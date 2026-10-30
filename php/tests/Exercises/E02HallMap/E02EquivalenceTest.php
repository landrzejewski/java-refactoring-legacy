<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E02HallMap;

use Closure;
use InvalidArgumentException;
use PHPUnit\Framework\Attributes\DataProvider;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use Training\Workshop\Exercises\E02HallMap\HallSpec;
use Training\Workshop\Exercises\E02HallMap\RowSpec;
use Training\Workshop\Exercises\E02HallMap\SectionSpec;
use Training\Workshop\Exercises\E02HallMap\Start;
use Training\Workshop\Exercises\E02HallMap\Step1;
use Training\Workshop\Exercises\E02HallMap\Step2;
use Training\Workshop\Exercises\E02HallMap\Step3;
use Training\Workshop\Tests\Support\Scene;

/**
 * Opis sali: pojemność, dopłaty, zliczenie kategorii i lista miejsc w kolejności sektorów
 * i rzędów z pominięciem luk. Błędy danych (nieznany kod, pusty rząd, sektor, sala) też są kontraktem.
 */
final class E02EquivalenceTest extends TestCase
{
    /** @return array<string, array{Closure, HallSpec, string}> */
    public static function everyStep(): array
    {
        return Scene::variants()
            ->variant('start', fn (HallSpec $s) => self::report(fn () => (new Start\HallMaps())->describe($s)))
            ->variant('step1', fn (HallSpec $s) => self::report(fn () => (new Step1\HallMaps())->describe($s)))
            ->variant('step2', fn (HallSpec $s) => self::report(fn () => (new Step2\HallMaps())->describe($s)))
            ->variant('step3', fn (HallSpec $s) => self::report(fn () => (new Step3\HallMaps())->describe($s)))
            ->expect(
                'jeden rzad bez luk',
                self::hall('Mala', self::section('Parter', self::row('A', 'NNN'))),
                "Sala Mala: pojemnosc 3, doplaty 0.00\n"
                . "Kategorie: Normalne=3\n"
                . 'Miejsca: A1 o, A2 o, A3 o',
            )
            ->expect(
                'luka zuzywa numer',
                self::hall('Mala', self::section('Parter', self::row('A', 'NN_VV'))),
                "Sala Mala: pojemnosc 4, doplaty 20.00\n"
                . "Kategorie: Normalne=2 VIP=2\n"
                . 'Miejsca: A1 o, A2 o, A4 *, A5 *',
            )
            ->expect(
                'luka na poczatku rzedu',
                self::hall('Mala', self::section('Parter', self::row('A', '_NN'))),
                "Sala Mala: pojemnosc 2, doplaty 0.00\n"
                . "Kategorie: Normalne=2\n"
                . 'Miejsca: A2 o, A3 o',
            )
            ->expect(
                'dwa sektory, powtorzona etykieta rzedu',
                self::hall(
                    'Amator',
                    self::section('Parter', self::row('A', 'NN'), self::row('B', 'VV')),
                    self::section('Balkon', self::row('A', 'DD')),
                ),
                "Sala Amator: pojemnosc 6, doplaty 32.00\n"
                . "Kategorie: Normalne=2 VIP=2 Dwuosobowe=2\n"
                . 'Miejsca: A1 o, A2 o, B1 *, B2 *, A1 =, A2 =',
            )
            ->expect(
                'wozek i miejsca dwuosobowe',
                self::hall('Mala', self::section('Parter', self::row('A', 'WNNDD'))),
                "Sala Mala: pojemnosc 5, doplaty 12.00\n"
                . "Kategorie: Wozek=1 Normalne=2 Dwuosobowe=2\n"
                . 'Miejsca: A1 &, A2 o, A3 o, A4 =, A5 =',
            )
            ->expect(
                'same VIP',
                self::hall('Loza', self::section('Parter', self::row('A', 'VVVV'))),
                "Sala Loza: pojemnosc 4, doplaty 40.00\n"
                . "Kategorie: VIP=4\n"
                . 'Miejsca: A1 *, A2 *, A3 *, A4 *',
            )
            ->expect(
                'sala IMAX w skrocie',
                self::hall(
                    'IMAX',
                    self::section('Parter', self::row('A', 'NN_NN'), self::row('B', 'NN_NN'), self::row('C', 'VV_VV')),
                    self::section('Balkon', self::row('D', 'DD_DD'), self::row('E', 'W_NNN')),
                ),
                "Sala IMAX: pojemnosc 20, doplaty 64.00\n"
                . "Kategorie: Normalne=11 VIP=4 Dwuosobowe=4 Wozek=1\n"
                . 'Miejsca: A1 o, A2 o, A4 o, A5 o, B1 o, B2 o, B4 o, B5 o, C1 *, C2 *, C4 *, C5 *, '
                . 'D1 =, D2 =, D4 =, D5 =, E1 &, E3 o, E4 o, E5 o',
            )
            ->expect(
                'mala litera to nieznany kod',
                self::hall('Mala', self::section('Parter', self::row('A', 'NvN'))),
                'ERROR: unknown seat category: v',
            )
            ->expect(
                'nieznany kod',
                self::hall('Mala', self::section('Parter', self::row('A', 'NXN'))),
                'ERROR: unknown seat category: X',
            )
            ->expect(
                'blad w drugim sektorze po poprawnym pierwszym',
                self::hall('Mala', self::section('Parter', self::row('A', 'NN')), self::section('Balkon', self::row('A', 'NQ'))),
                'ERROR: unknown seat category: Q',
            )
            ->expect(
                'rzad z samych przejsc',
                self::hall('Mala', self::section('Parter', self::row('A', '___'))),
                'ERROR: row A has no seats',
            )
            ->expect(
                'pusty uklad rzedu',
                self::hall('Mala', self::section('Parter', self::row('A', ''))),
                'ERROR: row A has no seats',
            )
            ->expect(
                'sektor bez rzedow',
                self::hall('Mala', self::section('Parter', self::row('A', 'NN')), self::section('Balkon')),
                'ERROR: section Balkon has no rows',
            )
            ->expect('sala bez sektorow', self::hall('Pusta'), 'ERROR: hall Pusta has no sections')
            ->cases();
    }

    #[Test]
    #[DataProvider('everyStep')]
    public function everyStepDescribesTheHallTheSame(Closure $variant, HallSpec $spec, string $expected): void
    {
        self::assertSame($expected, $variant($spec));
    }

    public static function hall(string $name, SectionSpec ...$sections): HallSpec
    {
        return new HallSpec($name, $sections);
    }

    public static function section(string $name, RowSpec ...$rows): SectionSpec
    {
        return new SectionSpec($name, $rows);
    }

    public static function row(string $label, string $layout): RowSpec
    {
        return new RowSpec($label, $layout);
    }

    /** @param Closure(): string $description */
    private static function report(Closure $description): string
    {
        try {
            return $description();
        } catch (InvalidArgumentException $exception) {
            return 'ERROR: ' . $exception->getMessage();
        }
    }
}

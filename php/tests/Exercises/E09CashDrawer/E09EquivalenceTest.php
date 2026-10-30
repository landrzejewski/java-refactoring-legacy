<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E09CashDrawer;

use Closure;
use InvalidArgumentException;
use PHPUnit\Framework\Attributes\DataProvider;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use Training\Workshop\Exercises\E09CashDrawer\DrawerDay;
use Training\Workshop\Exercises\E09CashDrawer\DrawerLine;
use Training\Workshop\Exercises\E09CashDrawer\Start;
use Training\Workshop\Exercises\E09CashDrawer\Step1;
use Training\Workshop\Exercises\E09CashDrawer\Step2;
use Training\Workshop\Shared\Money;
use Training\Workshop\Tests\Support\Scene;

/**
 * Raport zamknięcia kasy identyczny na każdym kroku: każdy rodzaj wpisu, każdy tryb podziału,
 * zaokrąglenia, które muszą sumować się do rabatu, i błędy danych.
 */
final class E09EquivalenceTest extends TestCase
{
    /** @return array<string, array{Closure, DrawerDay, string}> */
    public static function everyStep(): array
    {
        $mixedDay = [
            self::cash('T-101', '25.00'),
            self::card('T-102', '32.00', '4321'),
            self::deposit('200.00', 'pogotowie kasowe'),
            self::refund('T-101', '25.00', 'choroba'),
            self::cash('T-103', '40.00'),
        ];
        return Scene::variants()
            ->variant('start', fn (DrawerDay $day) => self::report(fn () => (new Start\CashDrawerReport())->render($day)))
            ->variant('step1', fn (DrawerDay $day) => self::report(fn () => (new Step1\CashDrawerReport())->render($day)))
            ->variant('step2', fn (DrawerDay $day) => self::report(fn () => (new Step2\CashDrawerReport())->render($day)))
            ->expect(
                'dzien mieszany, grupa 10 x 25.00 proporcjonalnie',
                new DrawerDay($mixedDay, self::tickets(10, '25.00'), 'PROPORTIONAL'),
                <<<'TXT'
                Gotowka w szufladzie: 240.00
                Eksport fiskalny:
                S;T-101;25.00;8
                K;T-102;32.00;8;4321
                Z;T-101;-25.00;8
                S;T-103;40.00;8
                Audyt:
                CASH T-101 25.00
                CARD T-102 32.00 karta ****4321
                DEPOSIT 200.00 pogotowie kasowe
                REFUND T-101 -25.00 powod: choroba
                CASH T-103 40.00
                Rabat grupowy PROPORTIONAL: 25.00 = 2.50 + 2.50 + 2.50 + 2.50 + 2.50 + 2.50 + 2.50 + 2.50 + 2.50 + 2.50

                TXT,
            )
            ->expect(
                'tylko karty, bez grupy',
                new DrawerDay([self::card('T-201', '40.00', '1111'), self::card('T-202', '15.00', '2222')], [], 'PROPORTIONAL'),
                <<<'TXT'
                Gotowka w szufladzie: 0.00
                Eksport fiskalny:
                K;T-201;40.00;8;1111
                K;T-202;15.00;8;2222
                Audyt:
                CARD T-201 40.00 karta ****1111
                CARD T-202 15.00 karta ****2222
                Rabat grupowy: brak (0 biletow)

                TXT,
            )
            ->expect(
                'zwroty przewyzszaja sprzedaz gotowkowa',
                new DrawerDay(
                    [self::cash('T-301', '25.00'), self::refund('T-290', '40.00', 'odwolany seans'), self::refund('T-291', '40.00', 'odwolany seans')],
                    self::tickets(9, '25.00'),
                    'EQUAL',
                ),
                <<<'TXT'
                Gotowka w szufladzie: -55.00
                Eksport fiskalny:
                S;T-301;25.00;8
                Z;T-290;-40.00;8
                Z;T-291;-40.00;8
                Audyt:
                CASH T-301 25.00
                REFUND T-290 -40.00 powod: odwolany seans
                REFUND T-291 -40.00 powod: odwolany seans
                Rabat grupowy: brak (9 biletow)

                TXT,
            )
            ->expect(
                'pusty dzien',
                new DrawerDay([], [], 'LAST'),
                <<<'TXT'
                Gotowka w szufladzie: 0.00
                Eksport fiskalny:
                Audyt:
                Rabat grupowy: brak (0 biletow)

                TXT,
            )
            ->expect(
                'EQUAL: 30.30 na 11 biletow, 5 groszy reszty dla pierwszych',
                new DrawerDay([], self::mixed(7, '25.00', 4, '32.00'), 'EQUAL'),
                self::onlyGroup('Rabat grupowy EQUAL: 30.30 = 2.76 + 2.76 + 2.76 + 2.76 + 2.76'
                    . ' + 2.75 + 2.75 + 2.75 + 2.75 + 2.75 + 2.75'),
            )
            ->expect(
                'EQUAL: dokladnie podzielne',
                new DrawerDay([], self::tickets(10, '25.00'), 'EQUAL'),
                self::onlyGroup('Rabat grupowy EQUAL: 25.00 = 2.50 + 2.50 + 2.50 + 2.50 + 2.50'
                    . ' + 2.50 + 2.50 + 2.50 + 2.50 + 2.50'),
            )
            ->expect(
                'PROPORTIONAL: ceny z groszami, roznica zaokraglen na ostatnim',
                new DrawerDay([], self::tickets(10, '21.35'), 'PROPORTIONAL'),
                self::onlyGroup('Rabat grupowy PROPORTIONAL: 21.35 = 2.14 + 2.14 + 2.14 + 2.14 + 2.14'
                    . ' + 2.14 + 2.14 + 2.14 + 2.14 + 2.09'),
            )
            ->expect(
                'PROPORTIONAL: rozne ceny',
                new DrawerDay([], self::mixed(7, '25.00', 4, '32.00'), 'PROPORTIONAL'),
                self::onlyGroup('Rabat grupowy PROPORTIONAL: 30.30 = 2.50 + 2.50 + 2.50 + 2.50 + 2.50'
                    . ' + 2.50 + 2.50 + 3.20 + 3.20 + 3.20 + 3.20'),
            )
            ->expect(
                'LAST: caly rabat na ostatnim bilecie',
                new DrawerDay([], self::mixed(7, '25.00', 4, '32.00'), 'LAST'),
                self::onlyGroup('Rabat grupowy LAST: 30.30 = 0.00 + 0.00 + 0.00 + 0.00 + 0.00'
                    . ' + 0.00 + 0.00 + 0.00 + 0.00 + 0.00 + 30.30'),
            )
            ->expect(
                'grupa 9 biletow bez rabatu',
                new DrawerDay([], self::tickets(9, '40.00'), 'PROPORTIONAL'),
                self::onlyGroup('Rabat grupowy: brak (9 biletow)'),
            )
            ->expect(
                'nieznany tryb podzialu',
                new DrawerDay($mixedDay, self::tickets(10, '25.00'), 'RANDOM'),
                'ERROR: unknown split mode: RANDOM',
            )
            ->expect(
                'nieznany rodzaj wpisu',
                new DrawerDay([self::cash('T-1', '25.00'), new DrawerLine('VOUCHER', 'V-1', Money::of('20.00'), '')], self::tickets(10, '25.00'), 'LAST'),
                'ERROR: unknown entry kind: VOUCHER',
            )
            ->cases();
    }

    #[Test]
    #[DataProvider('everyStep')]
    public function everyStepRendersTheSameReport(Closure $variant, DrawerDay $day, string $expected): void
    {
        self::assertSame($expected, $variant($day));
    }

    /** @param Closure(): string $render */
    public static function report(Closure $render): string
    {
        try {
            return $render();
        } catch (InvalidArgumentException $exception) {
            return 'ERROR: ' . $exception->getMessage();
        }
    }

    /** @return list<Money> */
    public static function tickets(int $count, string $price): array
    {
        return array_fill(0, $count, Money::of($price));
    }

    /** @return list<Money> */
    public static function mixed(int $firstCount, string $firstPrice, int $secondCount, string $secondPrice): array
    {
        return [...self::tickets($firstCount, $firstPrice), ...self::tickets($secondCount, $secondPrice)];
    }

    private static function onlyGroup(string $groupLine): string
    {
        return "Gotowka w szufladzie: 0.00\nEksport fiskalny:\nAudyt:\n" . $groupLine . "\n";
    }

    private static function cash(string $reference, string $amount): DrawerLine
    {
        return new DrawerLine('CASH', $reference, Money::of($amount), '');
    }

    private static function card(string $reference, string $amount, string $last4): DrawerLine
    {
        return new DrawerLine('CARD', $reference, Money::of($amount), $last4);
    }

    private static function refund(string $reference, string $amount, string $reason): DrawerLine
    {
        return new DrawerLine('REFUND', $reference, Money::of($amount), $reason);
    }

    private static function deposit(string $amount, string $note): DrawerLine
    {
        return new DrawerLine('DEPOSIT', '', Money::of($amount), $note);
    }
}

<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E05GiftCard;

use Closure;
use InvalidArgumentException;
use LogicException;
use PHPUnit\Framework\Attributes\DataProvider;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use Training\Workshop\Exercises\E05GiftCard\DayOfWeek;
use Training\Workshop\Exercises\E05GiftCard\ScreeningFacts;
use Training\Workshop\Exercises\E05GiftCard\Start;
use Training\Workshop\Exercises\E05GiftCard\Step1;
use Training\Workshop\Exercises\E05GiftCard\Step2;
use Training\Workshop\Shared\Money;
use Training\Workshop\Tests\Support\Scene;

/**
 * Dwie tabele decyzji: pełna tabela przejść karty (każdy status x każda operacja, walidacja przed
 * statusem) oraz reguły voucherów (każde pojęcie, negacja, błędy składni, moment błędu).
 *
 * Różnica względem Javy: rekordy pomocnicze testu {@code CardScript} i {@code VoucherCase} to tu
 * tablice {@code array{string, list<string>}} i {@code array{string, ScreeningFacts}}.
 */
final class E05EquivalenceTest extends TestCase
{
    private const string RULE = 'format=3D AND day IN MON,TUE AND NOT premiere';

    /** @return array<string, array{Closure, array{string, list<string>}, string}> */
    public static function cardScenes(): array
    {
        return Scene::variants()
            ->variant('start', fn (array $s) => self::outcome(fn () => Start\GiftCards::simulate(Money::of($s[0]), $s[1])))
            ->variant('step1', fn (array $s) => self::outcome(fn () => Step1\GiftCards::simulate(Money::of($s[0]), $s[1])))
            ->variant('step2', fn (array $s) => self::outcome(fn () => Step2\GiftCards::simulate(Money::of($s[0]), $s[1])))
            ->expect('wydana, bez operacji', self::script('100.00'), 'ISSUED 100.00')
            ->expect('aktywacja i dwa obciążenia', self::script('100.00', 'activate', 'charge 25.00', 'charge 32.00'), 'ACTIVE 43.00')
            ->expect('wyczerpanie do zera', self::script('50.00', 'activate', 'charge 50.00'), 'EMPTY 0.00')
            ->expect('obciążenie przed aktywacją', self::script('50.00', 'charge 10.00'), 'ERROR: card not activated')
            ->expect('druga aktywacja', self::script('50.00', 'activate', 'activate'), 'ERROR: card already active')
            ->expect('brak środków', self::script('30.00', 'activate', 'charge 40.00'), 'ERROR: insufficient balance')
            ->expect('kwota ujemna', self::script('30.00', 'activate', 'charge -1.00'), 'ERROR: amount must be positive')
            ->expect(
                'kwota ujemna na wygasłej - walidacja przed statusem',
                self::script('30.00', 'expire', 'charge -1.00'),
                'ERROR: amount must be positive',
            )
            ->expect('blokada wydanej', self::script('30.00', 'block'), 'BLOCKED 30.00')
            ->expect(
                'blokada aktywnej, potem obciążenie',
                self::script('30.00', 'activate', 'block', 'charge 5.00'),
                'ERROR: card is blocked',
            )
            ->expect('aktywacja zablokowanej', self::script('30.00', 'block', 'activate'), 'ERROR: card is blocked')
            ->expect('podwójna blokada', self::script('30.00', 'block', 'block'), 'ERROR: card is blocked')
            ->expect('blokada pustej', self::script('30.00', 'activate', 'charge 30.00', 'block'), 'ERROR: card is empty')
            ->expect('aktywacja pustej', self::script('30.00', 'activate', 'charge 30.00', 'activate'), 'ERROR: card is empty')
            ->expect('wygaśnięcie zablokowanej', self::script('30.00', 'block', 'expire'), 'EXPIRED 30.00')
            ->expect('wygaśnięcie pustej', self::script('30.00', 'activate', 'charge 30.00', 'expire'), 'EXPIRED 0.00')
            ->expect('wygaśnięcie wydanej', self::script('30.00', 'expire'), 'EXPIRED 30.00')
            ->expect('aktywacja wygasłej', self::script('30.00', 'expire', 'activate'), 'ERROR: card has expired')
            ->expect('blokada wygasłej', self::script('30.00', 'expire', 'block'), 'ERROR: card has expired')
            ->expect('podwójne wygaśnięcie', self::script('30.00', 'expire', 'expire'), 'ERROR: card has expired')
            ->expect('saldo początkowe zero', self::script('0.00'), 'ERROR: initial balance must be positive')
            ->expect('nieznana operacja', self::script('30.00', 'activate', 'refund'), 'ERROR: unknown operation: refund')
            ->cases();
    }

    #[Test]
    #[DataProvider('cardScenes')]
    public function everyStepRunsTheCardTheSame(Closure $variant, array $script, string $expected): void
    {
        self::assertSame($expected, $variant($script));
    }

    /** @return array<string, array{Closure, array{string, ScreeningFacts}, string}> */
    public static function voucherScenes(): array
    {
        return Scene::variants()
            ->variant('start', fn (array $c) => self::outcome(
                fn () => self::bool(Start\Vouchers::applies($c[0], $c[1])) . ' / ' . Start\Vouchers::describe($c[0]),
            ))
            ->variant('step1', fn (array $c) => self::outcome(
                fn () => self::bool(Step1\Vouchers::applies($c[0], $c[1])) . ' / ' . Step1\Vouchers::describe($c[0]),
            ))
            ->variant('step2', fn (array $c) => self::outcome(
                fn () => self::bool(Step2\Vouchers::applies($c[0], $c[1])) . ' / ' . Step2\Vouchers::describe($c[0]),
            ))
            ->expect(
                'pełna reguła, Kraina Lodu 3D w poniedziałek',
                self::rule(self::RULE, '3D', DayOfWeek::MONDAY, false),
                'true / format 3D oraz dzień: MON, TUE oraz nie premiera',
            )
            ->expect(
                'pełna reguła, zły dzień',
                self::rule(self::RULE, '3D', DayOfWeek::FRIDAY, false),
                'false / format 3D oraz dzień: MON, TUE oraz nie premiera',
            )
            ->expect(
                'pełna reguła, premiera',
                self::rule(self::RULE, '3D', DayOfWeek::TUESDAY, true),
                'false / format 3D oraz dzień: MON, TUE oraz nie premiera',
            )
            ->expect(
                'pełna reguła, zły format',
                self::rule(self::RULE, 'IMAX', DayOfWeek::MONDAY, false),
                'false / format 3D oraz dzień: MON, TUE oraz nie premiera',
            )
            ->expect('samo premiere', self::rule('premiere', 'IMAX', DayOfWeek::FRIDAY, true), 'true / premiera')
            ->expect('negacja formatu', self::rule('NOT format=IMAX', 'IMAX', DayOfWeek::FRIDAY, false), 'false / nie format IMAX')
            ->expect('weekend', self::rule('day IN SAT,SUN', '2D', DayOfWeek::SUNDAY, false), 'true / dzień: SAT, SUN')
            ->expect(
                'dwa pojęcia, drugie fałszywe',
                self::rule('format=2D AND premiere', '2D', DayOfWeek::MONDAY, false),
                'false / format 2D oraz premiera',
            )
            ->expect(
                'odstępy wokół przecinków',
                self::rule('day IN MON , TUE', '2D', DayOfWeek::TUESDAY, false),
                'true / dzień: MON, TUE',
            )
            ->expect('nieznane pojęcie', self::rule('hour<12', '2D', DayOfWeek::MONDAY, false), 'ERROR: unknown rule term: hour<12')
            ->expect(
                'nieznane pojęcie po fałszywym - błąd mimo fałszu',
                self::rule('format=IMAX AND hour<12', '2D', DayOfWeek::MONDAY, false),
                'ERROR: unknown rule term: hour<12',
            )
            ->expect(
                'podwójne NOT nie jest obsługiwane',
                self::rule('NOT NOT premiere', '2D', DayOfWeek::MONDAY, true),
                'ERROR: unknown rule term: NOT premiere',
            )
            ->expect('nieznany dzień', self::rule('day IN MON,XYZ', '2D', DayOfWeek::MONDAY, false), 'ERROR: unknown day: XYZ')
            ->expect('pusta reguła', self::rule('   ', '2D', DayOfWeek::MONDAY, false), 'ERROR: rule must not be empty')
            ->cases();
    }

    #[Test]
    #[DataProvider('voucherScenes')]
    public function everyStepReadsVoucherRulesTheSame(Closure $variant, array $voucherCase, string $expected): void
    {
        self::assertSame($expected, $variant($voucherCase));
    }

    /** @return array{string, list<string>} */
    private static function script(string $initialBalance, string ...$operations): array
    {
        return [$initialBalance, $operations];
    }

    /** @return array{string, ScreeningFacts} */
    private static function rule(string $rule, string $format, DayOfWeek $day, bool $premiere): array
    {
        return [$rule, new ScreeningFacts($format, $day, $premiere)];
    }

    /** Wydruk {@code boolean} jak w Javie ({@code true}/{@code false}). */
    private static function bool(bool $value): string
    {
        return $value ? 'true' : 'false';
    }

    /** @param Closure(): string $action */
    public static function outcome(Closure $action): string
    {
        try {
            return $action();
        } catch (LogicException | InvalidArgumentException $exception) {
            return 'ERROR: ' . $exception->getMessage();
        }
    }
}

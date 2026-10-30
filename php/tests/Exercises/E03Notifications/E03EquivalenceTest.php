<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E03Notifications;

use Closure;
use PHPUnit\Framework\Attributes\DataProvider;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use Training\Workshop\Exercises\E03Notifications\Cancel;
use Training\Workshop\Exercises\E03Notifications\Change;
use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Move;
use Training\Workshop\Exercises\E03Notifications\Scenario;
use Training\Workshop\Exercises\E03Notifications\Screening;
use Training\Workshop\Exercises\E03Notifications\Start;
use Training\Workshop\Exercises\E03Notifications\Step1;
use Training\Workshop\Exercises\E03Notifications\Step2;
use Training\Workshop\Tests\Support\Scene;

/**
 * Dziennik efektow zmiany repertuaru: wpis repertuaru, powiadomienie kazdego klienta wybranym
 * kanalem (w kolejnosci klientow), reakcja listy oczekujacych, statystyki. Kolejnosc wpisow,
 * tresc wiadomosci, skracanie SMS i wyjatki sa kontraktem.
 */
final class E03EquivalenceTest extends TestCase
{
    private const string MOVED_SCHEDULE = 'SCHEDULE Diuna 2026-10-30 18:00 -> 2026-10-31 20:00 sala 2';
    private const string MOVED_BODY =
        'Seans Diuna z 2026-10-30 18:00 przeniesiony na 2026-10-31 20:00, sala 2. Bilety zachowuja waznosc.';
    private const string MOVED_SMS = 'Zmiana seansu: Diuna. Seans Diuna z 2026-10-30 18:00 przeniesiony n...';
    private const string CANCELLED_SCHEDULE = 'SCHEDULE Diuna 2026-10-30 18:00 odwolany: awaria projektora';
    private const string CANCELLED_BODY =
        'Seans Diuna z 2026-10-30 18:00 odwolany (awaria projektora). Zwrot w ciagu 7 dni.';
    private const string CANCELLED_SMS = 'Odwolany seans: Diuna. Seans Diuna z 2026-10-30 18:00 odwolany (awa...';
    private const string WAITLIST_KEPT = 'WAITLIST Diuna: 2 oczekujacych zachowuje kolejnosc';
    private const string WAITLIST_CLEARED = 'WAITLIST Diuna: lista wyczyszczona (2)';

    /** @return array<string, array{Closure, Scenario, string}> */
    public static function everyStep(): array
    {
        // Różnica względem Javy: stałe klasy nie mogą trzymać obiektów, więc dane testowe są zmiennymi lokalnymi.
        $diuna = self::diuna();
        $anna = new Customer('Anna', 'anna@kino.pl', '600100200', 'tok-anna');
        $bartek = new Customer('Bartek', 'bartek@kino.pl', '600300400', null);
        $celina = new Customer('Celina', 'celina@kino.pl', null, null);
        $daniel = new Customer('Daniel', null, null, null);
        $ewa = new Customer('Ewa', 'ewa@kino.pl', '600500600', 'expired-ewa');
        $filip = new Customer('Filip', null, '600700800', '  ');
        $move = new Move('2026-10-31 20:00', '2');
        $cancel = new Cancel('awaria projektora');

        return Scene::variants()
            ->variant('start', fn (Scenario $s) => (new Start\ScreeningDesk())->run($s))
            ->variant('step1', fn (Scenario $s) => (new Step1\ScreeningDesk())->run($s))
            ->variant('step2', fn (Scenario $s) => (new Step2\ScreeningDesk())->run($s))
            ->expect('przeniesienie, klient z push', self::scenario([$anna], $move), self::lines(
                self::MOVED_SCHEDULE,
                'PUSH tok-anna | Zmiana seansu: Diuna | ' . self::MOVED_BODY,
                self::WAITLIST_KEPT,
                'STATS przeniesione=1 odwolane=0',
            ))
            ->expect('przeniesienie, niewazny token push -> SMS skrocony do 70 znakow', self::scenario([$ewa], $move), self::lines(
                self::MOVED_SCHEDULE,
                'SMS 600500600 | ' . self::MOVED_SMS,
                self::WAITLIST_KEPT,
                'STATS przeniesione=1 odwolane=0',
            ))
            ->expect('przeniesienie, pusty token push liczy sie jak brak', self::scenario([$filip], $move), self::lines(
                self::MOVED_SCHEDULE,
                'SMS 600700800 | ' . self::MOVED_SMS,
                self::WAITLIST_KEPT,
                'STATS przeniesione=1 odwolane=0',
            ))
            ->expect('przeniesienie, tylko e-mail', self::scenario([$celina], $move), self::lines(
                self::MOVED_SCHEDULE,
                'EMAIL celina@kino.pl | Zmiana seansu: Diuna | ' . self::MOVED_BODY,
                self::WAITLIST_KEPT,
                'STATS przeniesione=1 odwolane=0',
            ))
            ->expect('przeniesienie, klient bez kanalu', self::scenario([$daniel], $move), self::lines(
                self::MOVED_SCHEDULE,
                'UNDELIVERED Daniel (brak kanalu)',
                self::WAITLIST_KEPT,
                'STATS przeniesione=1 odwolane=0',
            ))
            ->expect('odwolanie, trzech klientow w kolejnosci listy', self::scenario([$bartek, $anna, $daniel], $cancel), self::lines(
                self::CANCELLED_SCHEDULE,
                'SMS 600300400 | ' . self::CANCELLED_SMS,
                'PUSH tok-anna | Odwolany seans: Diuna | ' . self::CANCELLED_BODY,
                'UNDELIVERED Daniel (brak kanalu)',
                self::WAITLIST_CLEARED,
                'STATS przeniesione=0 odwolane=1',
            ))
            ->expect(
                'przeniesienie, potem odwolanie z nowego terminu',
                self::scenario([$celina], $move, new Cancel('choroba obsady')),
                self::lines(
                    self::MOVED_SCHEDULE,
                    'EMAIL celina@kino.pl | Zmiana seansu: Diuna | ' . self::MOVED_BODY,
                    self::WAITLIST_KEPT,
                    'STATS przeniesione=1 odwolane=0',
                    'SCHEDULE Diuna 2026-10-31 20:00 odwolany: choroba obsady',
                    'EMAIL celina@kino.pl | Odwolany seans: Diuna | '
                        . 'Seans Diuna z 2026-10-31 20:00 odwolany (choroba obsady). Zwrot w ciagu 7 dni.',
                    self::WAITLIST_CLEARED,
                    'STATS przeniesione=1 odwolane=1',
                ),
            )
            ->expect(
                'drugie przeniesienie liczy od nowego terminu',
                self::scenario([$anna], $move, new Move('2026-11-01 17:00', '3')),
                self::lines(
                    self::MOVED_SCHEDULE,
                    'PUSH tok-anna | Zmiana seansu: Diuna | ' . self::MOVED_BODY,
                    self::WAITLIST_KEPT,
                    'STATS przeniesione=1 odwolane=0',
                    'SCHEDULE Diuna 2026-10-31 20:00 -> 2026-11-01 17:00 sala 3',
                    'PUSH tok-anna | Zmiana seansu: Diuna | Seans Diuna z 2026-10-31 20:00 przeniesiony na '
                        . '2026-11-01 17:00, sala 3. Bilety zachowuja waznosc.',
                    self::WAITLIST_KEPT,
                    'STATS przeniesione=2 odwolane=0',
                ),
            )
            ->expect('odwolanie, potem przeniesienie', self::scenario([$anna], $cancel, $move), self::lines(
                self::CANCELLED_SCHEDULE,
                'PUSH tok-anna | Odwolany seans: Diuna | ' . self::CANCELLED_BODY,
                self::WAITLIST_CLEARED,
                'STATS przeniesione=0 odwolane=1',
                'ERROR: screening already cancelled: Diuna',
            ))
            ->expect('odwolanie dwa razy', self::scenario([], $cancel, $cancel), self::lines(
                self::CANCELLED_SCHEDULE,
                self::WAITLIST_CLEARED,
                'STATS przeniesione=0 odwolane=1',
                'ERROR: screening already cancelled: Diuna',
            ))
            ->expect('bez posiadaczy biletow i bez oczekujacych', new Scenario($diuna, [], [], [$move]), self::lines(
                self::MOVED_SCHEDULE,
                'WAITLIST Diuna: 0 oczekujacych zachowuje kolejnosc',
                'STATS przeniesione=1 odwolane=0',
            ))
            ->expect('pusty nowy termin', self::scenario([$anna], new Move(' ', '2')), 'ERROR: new start must not be blank')
            ->expect('pusta sala', self::scenario([$anna], new Move('2026-10-31 20:00', '')), 'ERROR: hall must not be blank')
            ->expect('odwolanie bez powodu', self::scenario([$anna], new Cancel(null)), 'ERROR: reason must not be blank')
            ->cases();
    }

    #[Test]
    #[DataProvider('everyStep')]
    public function everyStepProducesTheSameJournal(Closure $variant, Scenario $scenario, string $expected): void
    {
        self::assertSame($expected, $variant($scenario));
    }

    private static function diuna(): Screening
    {
        return new Screening('S1', 'Diuna', '2026-10-30 18:00', '1');
    }

    /** @param list<Customer> $ticketHolders */
    private static function scenario(array $ticketHolders, Change ...$changes): Scenario
    {
        $waiting = [
            new Customer('Bartek', 'bartek@kino.pl', '600300400', null),
            new Customer('Celina', 'celina@kino.pl', null, null),
        ];
        return new Scenario(self::diuna(), $ticketHolders, $waiting, $changes);
    }

    private static function lines(string ...$lines): string
    {
        return implode("\n", $lines);
    }
}

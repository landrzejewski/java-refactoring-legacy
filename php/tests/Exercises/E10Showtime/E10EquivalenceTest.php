<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E10Showtime;

use Closure;
use PHPUnit\Framework\Attributes\DataProvider;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use Training\Workshop\Exercises\E10Showtime\Start;
use Training\Workshop\Exercises\E10Showtime\Step1;
use Training\Workshop\Exercises\E10Showtime\Step2;
use Training\Workshop\Tests\Support\Scene;

/**
 * Dziennik urządzeń i podsystemów dla scenariuszy wspólnych wszystkim krokom: kolejność reakcji sali,
 * błędy urządzeń, procedura awaryjna. Start z automatu ({@code auto start}) jest celowo poza tym testem,
 * bo w kroku 2 świadomie zmienia zachowanie - opisuje go E10BehaviourChangeTest.
 */
final class E10EquivalenceTest extends TestCase
{
    /** @return array<string, array{Closure, list<string>, string}> */
    public static function everyStep(): array
    {
        return Scene::variants()
            ->variant('start', fn (array $s) => (new Start\ShowtimeRunner())->run($s))
            ->variant('step1', fn (array $s) => (new Step1\ShowtimeRunner())->run($s))
            ->variant('step2', fn (array $s) => (new Step2\ShowtimeRunner())->run($s))
            ->expect(
                'start z kasy: bramka, drzwi, swiatla, reklamy, raport, film',
                ['scan S1', 'scan S1', 'scan S1', 'desk start S1 Diuna', 'tick', 'tick'],
                <<<'TXT'
                scanner: S1 closed, 3 scanned
                doors: closed
                lights: 30%
                ads: start 2
                report: S1 attendance 3
                ads: 1 left
                ads: finished
                lights: 0%
                sound: 70
                projector: play Diuna
                TXT,
            )
            ->expect(
                'koniec seansu z kasy',
                ['scan S1', 'desk start S1 Diuna', 'tick', 'tick', 'desk end S1'],
                <<<'TXT'
                scanner: S1 closed, 1 scanned
                doors: closed
                lights: 30%
                ads: start 2
                report: S1 attendance 1
                ads: 1 left
                ads: finished
                lights: 0%
                sound: 70
                projector: play Diuna
                projector: stop
                sound: 0
                lights: 100%
                doors: open
                report: S1 closed
                TXT,
            )
            ->expect(
                'koniec seansu z automatu jest taki sam jak z kasy',
                ['desk start S1 Amator', 'tick', 'tick', 'auto end S1'],
                <<<'TXT'
                scanner: S1 closed, 0 scanned
                doors: closed
                lights: 30%
                ads: start 2
                report: S1 attendance 0
                ads: 1 left
                ads: finished
                lights: 0%
                sound: 70
                projector: play Amator
                projector: stop
                sound: 0
                lights: 100%
                doors: open
                report: S1 closed
                TXT,
            )
            ->expect(
                'przeszkoda w drzwiach: swiatla w gore, zamkniecie wznowione po zwolnieniu',
                ['obstacle', 'desk start S1 Diuna', 'cleared', 'tick', 'tick'],
                <<<'TXT'
                doors: obstacle
                scanner: S1 closed, 0 scanned
                doors: blocked
                lights: 100%
                report: S1 attendance 0
                doors: cleared
                doors: closed
                lights: 30%
                ads: start 2
                ads: 1 left
                ads: finished
                lights: 0%
                sound: 70
                projector: play Diuna
                TXT,
            )
            ->expect(
                'przeszkoda po zamknieciu drzwi nie wznawia niczego',
                ['desk start S1 Diuna', 'obstacle', 'cleared', 'tick'],
                <<<'TXT'
                scanner: S1 closed, 0 scanned
                doors: closed
                lights: 30%
                ads: start 2
                report: S1 attendance 0
                doors: obstacle
                doors: cleared
                ads: 1 left
                TXT,
            )
            ->expect(
                'awaryjne w trakcie reklam: swiatla, drzwi, dzwiek, reklamy, projektor',
                ['desk start S1 Diuna', 'tick', 'emergency', 'tick'],
                <<<'TXT'
                scanner: S1 closed, 0 scanned
                doors: closed
                lights: 30%
                ads: start 2
                report: S1 attendance 0
                ads: 1 left
                lights: 100%
                doors: open
                sound: 0
                ads: stopped
                projector: cut
                ERROR: no ads playing
                TXT,
            )
            ->expect(
                'awaryjne w trakcie filmu: po odcieciu lampy nie ma czego konczyc',
                ['desk start S1 Diuna', 'tick', 'tick', 'emergency', 'desk end S1'],
                <<<'TXT'
                scanner: S1 closed, 0 scanned
                doors: closed
                lights: 30%
                ads: start 2
                report: S1 attendance 0
                ads: 1 left
                ads: finished
                lights: 0%
                sound: 70
                projector: play Diuna
                lights: 100%
                doors: open
                sound: 0
                projector: cut
                ERROR: projector is not playing
                TXT,
            )
            ->expect(
                'drugi seans przy zamknietych drzwiach: bramka zamknieta, drzwi odmawiaja, raportu brak',
                ['scan S1', 'desk start S1 Diuna', 'desk start S2 Amator'],
                <<<'TXT'
                scanner: S1 closed, 1 scanned
                doors: closed
                lights: 30%
                ads: start 2
                report: S1 attendance 1
                scanner: S2 closed, 0 scanned
                ERROR: doors already closed
                TXT,
            )
            ->expect(
                'drugi seans po zakonczeniu pierwszego',
                ['desk start S1 Diuna', 'tick', 'tick', 'desk end S1', 'desk start S2 Amator'],
                <<<'TXT'
                scanner: S1 closed, 0 scanned
                doors: closed
                lights: 30%
                ads: start 2
                report: S1 attendance 0
                ads: 1 left
                ads: finished
                lights: 0%
                sound: 70
                projector: play Diuna
                projector: stop
                sound: 0
                lights: 100%
                doors: open
                report: S1 closed
                scanner: S2 closed, 0 scanned
                doors: closed
                lights: 30%
                ads: start 2
                report: S2 attendance 0
                TXT,
            )
            ->expect(
                'koniec bez seansu',
                ['desk end S1'],
                'ERROR: projector is not playing',
            )
            ->expect(
                'skan po zamknieciu bramki',
                ['desk start S1 Diuna', 'scan S1'],
                <<<'TXT'
                scanner: S1 closed, 0 scanned
                doors: closed
                lights: 30%
                ads: start 2
                report: S1 attendance 0
                ERROR: scanner S1 already closed
                TXT,
            )
            ->expect(
                'ponowny start tego samego seansu',
                ['desk start S1 Diuna', 'tick', 'tick', 'desk end S1', 'desk start S1 Diuna'],
                <<<'TXT'
                scanner: S1 closed, 0 scanned
                doors: closed
                lights: 30%
                ads: start 2
                report: S1 attendance 0
                ads: 1 left
                ads: finished
                lights: 0%
                sound: 70
                projector: play Diuna
                projector: stop
                sound: 0
                lights: 100%
                doors: open
                report: S1 closed
                ERROR: scanner S1 already closed
                TXT,
            )
            ->expect(
                'limit glosnosci jest logika naglosnienia',
                ['volume 95', 'volume 60'],
                <<<'TXT'
                sound: 85 (capped)
                sound: 60
                TXT,
            )
            ->expect(
                'zwolnienie przejscia bez przeszkody',
                ['cleared'],
                'ERROR: doors are not blocked',
            )
            ->expect(
                'reklama bez playlisty',
                ['tick'],
                'ERROR: no ads playing',
            )
            ->expect(
                'nieznane polecenie',
                ['dance'],
                'ERROR: unknown command: dance',
            )
            ->cases();
    }

    /** @param list<string> $script */
    #[Test]
    #[DataProvider('everyStep')]
    public function everyStepLogsTheSameEvents(Closure $variant, array $script, string $expected): void
    {
        self::assertSame($expected, $variant($script));
    }
}

<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E10Showtime;

use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use ReflectionClass;
use ReflectionNamedType;
use ReflectionProperty;
use Training\Workshop\Exercises\E10Showtime\Start;
use Training\Workshop\Exercises\E10Showtime\Step1;
use Training\Workshop\Exercises\E10Showtime\Step2;

/**
 * Sens ćwiczenia: kopia orkiestracji w automacie pomija raport frekwencji. Fasada w kroku 2
 * to naprawia - to świadoma zmiana zachowania, nie "czysta" refaktoryzacja, dlatego ma osobny test.
 */
final class E10BehaviourChangeTest extends TestCase
{
    private const array AUTO_START = ['scan S1', 'scan S1', 'auto start S1 Diuna', 'tick', 'tick'];

    private const string WITHOUT_REPORT = <<<'TXT'
        scanner: S1 closed, 2 scanned
        doors: closed
        lights: 30%
        ads: start 2
        ads: 1 left
        ads: finished
        lights: 0%
        sound: 70
        projector: play Diuna
        TXT;

    private const string WITH_REPORT = <<<'TXT'
        scanner: S1 closed, 2 scanned
        doors: closed
        lights: 30%
        ads: start 2
        report: S1 attendance 2
        ads: 1 left
        ads: finished
        lights: 0%
        sound: 70
        projector: play Diuna
        TXT;

    /** Po "warsztat.sh jump" do kroku 2 automat nie ma już własnej orkiestracji i test jest pomijany. */
    #[Test]
    public function startAutoSchedulerSkipsTheAttendanceReport(): void
    {
        $ownOrchestration = array_any(
            (new ReflectionClass(Start\AutoScheduler::class))->getProperties(),
            fn (ReflectionProperty $property) => self::shortTypeName($property) === 'TicketScanner',
        );
        if (!$ownOrchestration) {
            self::markTestSkipped('start automatu korzysta już z fasady');
        }
        self::assertSame(self::WITHOUT_REPORT, (new Start\ShowtimeRunner())->run(self::AUTO_START));
    }

    #[Test]
    public function step1StillSkipsTheReportBecauseOnlyTheHallChanged(): void
    {
        self::assertSame(self::WITHOUT_REPORT, (new Step1\ShowtimeRunner())->run(self::AUTO_START));
    }

    #[Test]
    public function step2FacadeReportsAttendanceForBothClients(): void
    {
        self::assertSame(self::WITH_REPORT, (new Step2\ShowtimeRunner())->run(self::AUTO_START));
        self::assertSame(
            self::WITH_REPORT,
            (new Step2\ShowtimeRunner())->run(['scan S1', 'scan S1', 'desk start S1 Diuna', 'tick', 'tick']),
        );
    }

    private static function shortTypeName(ReflectionProperty $property): string
    {
        $type = $property->getType();
        if (!$type instanceof ReflectionNamedType) {
            return '';
        }
        $name = $type->getName();
        $separator = strrpos($name, '\\');
        return $separator === false ? $name : substr($name, $separator + 1);
    }
}

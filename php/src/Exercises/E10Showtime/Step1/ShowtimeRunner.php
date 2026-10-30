<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step1;

use Closure;
use InvalidArgumentException;
use LogicException;
use Training\Workshop\Exercises\E10Showtime\ShowRequest;

/**
 * Krok 1: korzeń kompozycji i symulator dnia w sali. Skrypt to polecenia klientów
 * ({@code desk start S1 Diuna}, {@code auto end S1}, {@code scan S1}) i zdarzenia sprzętu
 * ({@code tick}, {@code obstacle}, {@code cleared}, {@code volume 95}, {@code emergency}).
 * Wynikiem jest dziennik urządzeń; błąd polecenia to wpis {@code ERROR: ...}.
 */
final readonly class ShowtimeRunner
{
    private DeviceLog $log;
    private TicketScanner $scanner;
    private HallController $hall;
    private AttendanceReport $report;
    private BoxOfficePanel $desk;
    private AutoScheduler $auto;

    public function __construct()
    {
        $this->log = new DeviceLog();
        $this->scanner = new TicketScanner($this->log);
        $this->hall = new HallController($this->log);
        $this->report = new AttendanceReport($this->log);
        $this->desk = new BoxOfficePanel($this->scanner, $this->hall, $this->report);
        $this->auto = new AutoScheduler($this->scanner, $this->hall, $this->report);
    }

    /** @param list<string> $script */
    public function run(array $script): string
    {
        foreach ($script as $line) {
            try {
                $this->execute(preg_split('/\s+/', trim($line)));
            } catch (LogicException | InvalidArgumentException $exception) {
                $this->log->add('ERROR: ' . $exception->getMessage());
            }
        }
        return $this->log->dump();
    }

    /** @param list<string> $words */
    private function execute(array $words): void
    {
        match ($words[0]) {
            'scan' => $this->scanner->scan($words[1]),
            'desk' => self::client($words, $this->desk->startShow(...), $this->desk->endShow(...)),
            'auto' => self::client($words, $this->auto->startShow(...), $this->auto->endShow(...)),
            'tick' => $this->hall->adTick(),
            'obstacle' => $this->hall->doorObstacle(),
            'cleared' => $this->hall->doorCleared(),
            'volume' => $this->hall->volume(self::parseInt($words[1])),
            'emergency' => $this->hall->emergency(),
            default => throw new InvalidArgumentException('unknown command: ' . implode(' ', $words)),
        };
    }

    /**
     * @param list<string> $words
     * @param Closure(ShowRequest): void $start
     * @param Closure(string): void $end
     */
    private static function client(array $words, Closure $start, Closure $end): void
    {
        match ($words[1]) {
            'start' => $start(new ShowRequest($words[2], $words[3])),
            'end' => $end($words[2]),
            default => throw new InvalidArgumentException('unknown command: ' . implode(' ', $words)),
        };
    }

    /** Odpowiednik {@code Integer.parseInt}: zła liczba to błąd polecenia z tym samym komunikatem. */
    private static function parseInt(string $text): int
    {
        if (preg_match('/^[+-]?\d+$/', $text) !== 1) {
            throw new InvalidArgumentException('For input string: "' . $text . '"');
        }
        return (int) $text;
    }
}

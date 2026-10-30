<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step2;

use Closure;
use InvalidArgumentException;
use LogicException;
use Training\Workshop\Exercises\E10Showtime\ShowRequest;

/**
 * Krok 2: korzeń kompozycji składa podsystemy i fasadę; klienci dostają tylko fasadę.
 * Zdarzenia sprzętu ({@code tick}, {@code obstacle}, {@code cleared}, {@code volume}, {@code emergency})
 * trafiają do sterownika sali bezpośrednio - to symulacja czujników, nie operacja klienta.
 */
final readonly class ShowtimeRunner
{
    private DeviceLog $log;
    private TicketScanner $scanner;
    private HallController $hall;
    private Showtime $showtime;
    private BoxOfficePanel $desk;
    private AutoScheduler $auto;

    public function __construct()
    {
        $this->log = new DeviceLog();
        $this->scanner = new TicketScanner($this->log);
        $this->hall = new HallController($this->log);
        $this->showtime = new Showtime($this->scanner, $this->hall, new AttendanceReport($this->log));
        $this->desk = new BoxOfficePanel($this->showtime);
        $this->auto = new AutoScheduler($this->showtime);
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

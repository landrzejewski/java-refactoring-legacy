<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Start;

use LogicException;

/** Start: bramka biletowa - liczy skany i zamyka wejście na seans. */
final class TicketScanner
{
    /** @var array<string, int> */
    private array $scanned = [];
    /** @var array<string, true> */
    private array $closed = [];

    public function __construct(private readonly DeviceLog $log)
    {
    }

    public function scan(string $screeningId): void
    {
        if (isset($this->closed[$screeningId])) {
            throw new LogicException('scanner ' . $screeningId . ' already closed');
        }
        $this->scanned[$screeningId] = ($this->scanned[$screeningId] ?? 0) + 1;
    }

    /** Zamyka wejście i zwraca liczbę wpuszczonych widzów. */
    public function close(string $screeningId): int
    {
        if (isset($this->closed[$screeningId])) {
            throw new LogicException('scanner ' . $screeningId . ' already closed');
        }
        $this->closed[$screeningId] = true;
        $count = $this->scanned[$screeningId] ?? 0;
        $this->log->add('scanner: ' . $screeningId . ' closed, ' . $count . ' scanned');
        return $count;
    }
}

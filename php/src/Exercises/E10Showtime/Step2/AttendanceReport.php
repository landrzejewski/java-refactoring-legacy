<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step2;

/**
 * Krok 2: raport frekwencji dla dystrybutora - rejestruje widzów na starcie seansu i zamknięcie seansu.
 *
 * @internal podsystem ukryty za fasadą {@see Showtime} (PHP nie ma widoczności pakietowej)
 */
final readonly class AttendanceReport
{
    public function __construct(private DeviceLog $log)
    {
    }

    public function record(string $screeningId, int $attendance): void
    {
        $this->log->add('report: ' . $screeningId . ' attendance ' . $attendance);
    }

    public function close(string $screeningId): void
    {
        $this->log->add('report: ' . $screeningId . ' closed');
    }
}

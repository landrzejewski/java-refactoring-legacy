<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step2;

use InvalidArgumentException;

/**
 * Krok 2: oświetlenie sali. Jedyne urządzenie bez referencji do innych - reaguje tylko na polecenia.
 *
 * @internal podsystem ukryty za fasadą {@see Showtime} (PHP nie ma widoczności pakietowej)
 */
final class Lights
{
    private int $level = 100;

    public function __construct(private readonly DeviceLog $log)
    {
    }

    public function set(int $level): void
    {
        if ($level < 0 || $level > 100) {
            throw new InvalidArgumentException('lights level out of range: ' . $level);
        }
        $this->level = $level;
        $this->log->add('lights: ' . $level . '%');
    }

    public function level(): int
    {
        return $this->level;
    }
}

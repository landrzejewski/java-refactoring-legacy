<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step2;

/**
 * Krok 2: nagłośnienie z limitem głośności (ochrona słuchu) - limit jest logiką urządzenia, nie sali.
 *
 * @internal podsystem ukryty za fasadą {@see Showtime} (PHP nie ma widoczności pakietowej)
 */
final class Sound
{
    public const int MAX_LEVEL = 85;

    private int $level = 0;

    public function __construct(private readonly DeviceLog $log)
    {
    }

    public function set(int $requested): void
    {
        $applied = min($requested, self::MAX_LEVEL);
        $this->level = $applied;
        $this->log->add($applied < $requested ? 'sound: ' . $applied . ' (capped)' : 'sound: ' . $applied);
    }

    public function level(): int
    {
        return $this->level;
    }
}

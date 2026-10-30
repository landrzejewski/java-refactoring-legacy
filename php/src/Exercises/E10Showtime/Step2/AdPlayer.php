<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step2;

use LogicException;

/**
 * Krok 2: odtwarzacz reklam liczy reklamy i zgłasza koniec playlisty - nie wie, co się wtedy dzieje w sali.
 *
 * @internal podsystem ukryty za fasadą {@see Showtime} (PHP nie ma widoczności pakietowej)
 */
final class AdPlayer
{
    private int $remaining = 0;

    public function __construct(private readonly DeviceLog $log, private readonly HallMediator $hall)
    {
    }

    public function start(int $ads): void
    {
        $this->remaining = $ads;
        $this->log->add('ads: start ' . $ads);
    }

    /** Sprzęt: kolejna reklama się skończyła. */
    public function tick(): void
    {
        if ($this->remaining <= 0) {
            throw new LogicException('no ads playing');
        }
        $this->remaining--;
        if ($this->remaining > 0) {
            $this->log->add('ads: ' . $this->remaining . ' left');
            return;
        }
        $this->log->add('ads: finished');
        $this->hall->adsFinished();
    }

    public function stop(): void
    {
        if ($this->remaining > 0) {
            $this->remaining = 0;
            $this->log->add('ads: stopped');
        }
    }
}

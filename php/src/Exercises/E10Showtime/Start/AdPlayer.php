<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Start;

use LogicException;

/**
 * Start: odtwarzacz reklam. Gdy skończy playlistę, sam gasi światła, ustawia dźwięk i uruchamia projektor,
 * więc musi znać trzy inne urządzenia i tytuł filmu.
 */
final class AdPlayer
{
    private ?Lights $lights = null;
    private ?Sound $sound = null;
    private ?Projector $projector = null;
    private int $remaining = 0;
    private ?string $title = null;

    public function __construct(private readonly DeviceLog $log)
    {
    }

    public function wire(Lights $lights, Sound $sound, Projector $projector): void
    {
        $this->lights = $lights;
        $this->sound = $sound;
        $this->projector = $projector;
    }

    public function start(int $ads, string $title): void
    {
        $this->remaining = $ads;
        $this->title = $title;
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
        $this->lights->set(0);
        $this->sound->set(70);
        $this->projector->play($this->title);
        $this->title = null;
    }

    public function stop(): void
    {
        if ($this->remaining > 0) {
            $this->remaining = 0;
            $this->title = null;
            $this->log->add('ads: stopped');
        }
    }
}

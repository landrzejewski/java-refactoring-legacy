<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Start;

use LogicException;

/**
 * Start: projektor. {@code stop()} kończy seans i "przy okazji" przywraca dźwięk, światła i drzwi,
 * więc zna trzy inne urządzenia. {@code cut()} powstał dla procedury awaryjnej, bo tam kaskada
 * po {@code stop()} dublowała wpisy w dzienniku - dwa sposoby wyłączenia jednego urządzenia.
 */
final class Projector
{
    private ?Sound $sound = null;
    private ?Lights $lights = null;
    private ?Doors $doors = null;
    private bool $playing = false;

    public function __construct(private readonly DeviceLog $log)
    {
    }

    public function wire(Sound $sound, Lights $lights, Doors $doors): void
    {
        $this->sound = $sound;
        $this->lights = $lights;
        $this->doors = $doors;
    }

    public function play(string $title): void
    {
        if ($this->playing) {
            throw new LogicException('projector already playing');
        }
        $this->playing = true;
        $this->log->add('projector: play ' . $title);
    }

    public function stop(): void
    {
        if (!$this->playing) {
            throw new LogicException('projector is not playing');
        }
        $this->playing = false;
        $this->log->add('projector: stop');
        $this->sound->set(0);
        $this->lights->set(100);
        $this->doors->open();
    }

    /** Awaryjne odcięcie lampy - bez kaskady. */
    public function cut(): void
    {
        $this->playing = false;
        $this->log->add('projector: cut');
    }

    public function isPlaying(): bool
    {
        return $this->playing;
    }
}

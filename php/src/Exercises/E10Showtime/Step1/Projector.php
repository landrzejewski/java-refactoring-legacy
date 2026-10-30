<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step1;

use LogicException;

/**
 * Krok 1: projektor zna tylko własny stan. {@code stop()} to zwykłe zatrzymanie z kontrolą stanu,
 * {@code cut()} to awaryjne odcięcie lampy - oba są operacjami urządzenia, żadne nie steruje salą.
 */
final class Projector
{
    private bool $playing = false;

    public function __construct(private readonly DeviceLog $log)
    {
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
    }

    /** Awaryjne odcięcie lampy - działa w każdym stanie. */
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

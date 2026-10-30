<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Start;

use LogicException;

/**
 * Start: drzwi sali. Oprócz własnej logiki (zamknięte/otwarte, przeszkoda w świetle drzwi) znają
 * oświetlenie i odtwarzacz reklam, bo po zamknięciu "wiedzą", że trzeba przyciemnić i puścić reklamy.
 * Żeby to zrobić, muszą pamiętać tytuł filmu, który nie ma nic wspólnego z drzwiami.
 */
final class Doors
{
    private ?Lights $lights = null;
    private ?AdPlayer $adPlayer = null;
    private bool $closed = false;
    private bool $blocked = false;
    private ?string $pendingTitle = null;

    public function __construct(private readonly DeviceLog $log)
    {
    }

    public function wire(Lights $lights, AdPlayer $adPlayer): void
    {
        $this->lights = $lights;
        $this->adPlayer = $adPlayer;
    }

    public function close(string $title): void
    {
        if ($this->closed) {
            throw new LogicException('doors already closed');
        }
        if ($this->blocked) {
            $this->log->add('doors: blocked');
            $this->lights->set(100);
            $this->pendingTitle = $title;
            return;
        }
        $this->closed = true;
        $this->log->add('doors: closed');
        $this->lights->set(30);
        $this->adPlayer->start(2, $title);
    }

    public function open(): void
    {
        $this->closed = false;
        $this->log->add('doors: open');
    }

    /** Czujnik: coś stoi w świetle drzwi. */
    public function obstacle(): void
    {
        $this->blocked = true;
        $this->log->add('doors: obstacle');
    }

    /** Czujnik: przejście wolne - jeśli zamknięcie czekało, drzwi same je wznawiają. */
    public function cleared(): void
    {
        if (!$this->blocked) {
            throw new LogicException('doors are not blocked');
        }
        $this->blocked = false;
        $this->log->add('doors: cleared');
        if ($this->pendingTitle !== null) {
            $title = $this->pendingTitle;
            $this->pendingTitle = null;
            $this->close($title);
        }
    }

    public function isClosed(): bool
    {
        return $this->closed;
    }
}

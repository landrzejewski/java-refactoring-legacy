<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step1;

use LogicException;

/**
 * Krok 1: drzwi znają tylko własny stan (zamknięte, przeszkoda) i sterownik sali. Nie pamiętają już
 * tytułu filmu ani tego, co ma się stać po zamknięciu - tylko zgłaszają zdarzenie.
 */
final class Doors
{
    private bool $closed = false;
    private bool $blocked = false;

    public function __construct(private readonly DeviceLog $log, private readonly HallMediator $hall)
    {
    }

    public function close(): void
    {
        if ($this->closed) {
            throw new LogicException('doors already closed');
        }
        if ($this->blocked) {
            $this->log->add('doors: blocked');
            $this->hall->doorsBlocked();
            return;
        }
        $this->closed = true;
        $this->log->add('doors: closed');
        $this->hall->doorsClosed();
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

    /** Czujnik: przejście wolne. */
    public function cleared(): void
    {
        if (!$this->blocked) {
            throw new LogicException('doors are not blocked');
        }
        $this->blocked = false;
        $this->log->add('doors: cleared');
        $this->hall->doorsCleared();
    }

    public function isClosed(): bool
    {
        return $this->closed;
    }
}

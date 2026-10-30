<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use Training\Workshop\Exercises\E03Notifications\Customer;

/** Krok 2: lista oczekujacych jako odbiorca zdarzen - ta sama logika, inna sygnatura. */
final class Waitlist implements ScreeningListener
{
    /** @param list<Customer> $waiting */
    public function __construct(private readonly Journal $journal, private array $waiting)
    {
    }

    public function screeningMoved(ScreeningMoved $event): void
    {
        $this->journal->note('WAITLIST ' . $event->after->title . ': ' . count($this->waiting) . ' oczekujacych zachowuje kolejnosc');
    }

    public function screeningCancelled(ScreeningCancelled $event): void
    {
        $this->journal->note('WAITLIST ' . $event->screening->title . ': lista wyczyszczona (' . count($this->waiting) . ')');
        $this->waiting = [];
    }
}

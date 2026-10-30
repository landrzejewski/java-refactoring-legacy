<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Start;

use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Screening;

/** Start: lista oczekujacych na bilety - po przeniesieniu zachowuje kolejnosc, po odwolaniu jest czyszczona. */
final class Waitlist
{
    /** @param list<Customer> $waiting */
    public function __construct(private readonly Journal $journal, private array $waiting)
    {
    }

    public function screeningMoved(Screening $after): void
    {
        $this->journal->note('WAITLIST ' . $after->title . ': ' . count($this->waiting) . ' oczekujacych zachowuje kolejnosc');
    }

    public function screeningCancelled(Screening $screening): void
    {
        $this->journal->note('WAITLIST ' . $screening->title . ': lista wyczyszczona (' . count($this->waiting) . ')');
        $this->waiting = [];
    }
}

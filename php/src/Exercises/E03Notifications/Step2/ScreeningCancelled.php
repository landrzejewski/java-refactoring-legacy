<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Screening;

/** Krok 2: zdarzenie "seans odwolany". */
final readonly class ScreeningCancelled
{
    /** @param list<Customer> $ticketHolders */
    public function __construct(public Screening $screening, public string $reason, public array $ticketHolders)
    {
    }
}

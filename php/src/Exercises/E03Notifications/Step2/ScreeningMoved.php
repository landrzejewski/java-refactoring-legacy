<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Screening;

/** Krok 2: zdarzenie "seans przeniesiony" - wszystko, czego odbiorcy potrzebuja, bez odwolania do schedulera. */
final readonly class ScreeningMoved
{
    /** @param list<Customer> $ticketHolders */
    public function __construct(public Screening $before, public Screening $after, public array $ticketHolders)
    {
    }
}

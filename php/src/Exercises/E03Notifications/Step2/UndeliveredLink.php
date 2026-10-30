<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use Training\Workshop\Exercises\E03Notifications\Customer;

/**
 * Krok 2: jawny koniec lancucha. Zamiast "wypadniecia" z ostatniego else wpis o niedostarczeniu
 * jest ogniwem - mozna go podmienic (np. na zgloszenie do kasy) bez edycji pozostalych.
 */
final class UndeliveredLink implements Channel
{
    public function __construct(private readonly Journal $journal)
    {
    }

    public function deliver(Customer $customer, string $subject, string $body): void
    {
        $this->journal->note('UNDELIVERED ' . $customer->name . ' (brak kanalu)');
    }
}

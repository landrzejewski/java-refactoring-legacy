<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use Training\Workshop\Exercises\E03Notifications\Customer;

/** Krok 2: kanal e-mail - tytul i tresc osobno, bez limitu dlugosci. */
final class EmailChannel implements Channel
{
    public function __construct(private readonly Journal $journal)
    {
    }

    public function deliver(Customer $customer, string $subject, string $body): void
    {
        $this->journal->email($customer->email, $subject, $body);
    }
}

<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use Training\Workshop\Exercises\E03Notifications\Customer;

/** Krok 2: kanal push. */
final class PushChannel implements Channel
{
    public function __construct(private readonly Journal $journal)
    {
    }

    public function deliver(Customer $customer, string $subject, string $body): void
    {
        $this->journal->push($customer->pushToken, $subject, $body);
    }
}

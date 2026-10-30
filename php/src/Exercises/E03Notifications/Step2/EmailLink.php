<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use Training\Workshop\Exercises\E03Notifications\Customer;

/** Krok 2: ogniwo e-mail - obsluguje klienta z adresem. */
final class EmailLink implements Channel
{
    public function __construct(private readonly EmailChannel $email, private readonly Channel $next)
    {
    }

    public function deliver(Customer $customer, string $subject, string $body): void
    {
        if ($customer->email !== null && trim($customer->email) !== '') {
            $this->email->deliver($customer, $subject, $body);
        } else {
            $this->next->deliver($customer, $subject, $body);
        }
    }
}

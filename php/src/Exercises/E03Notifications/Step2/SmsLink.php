<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use Training\Workshop\Exercises\E03Notifications\Customer;

/** Krok 2: ogniwo SMS - obsluguje klienta z numerem telefonu. */
final class SmsLink implements Channel
{
    public function __construct(private readonly SmsChannel $sms, private readonly Channel $next)
    {
    }

    public function deliver(Customer $customer, string $subject, string $body): void
    {
        if ($customer->phone !== null && trim($customer->phone) !== '') {
            $this->sms->deliver($customer, $subject, $body);
        } else {
            $this->next->deliver($customer, $subject, $body);
        }
    }
}

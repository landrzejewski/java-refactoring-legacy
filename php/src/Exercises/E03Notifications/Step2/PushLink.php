<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use Training\Workshop\Exercises\E03Notifications\Customer;

/**
 * Krok 2: pierwsze ogniwo dostarczania. Obsluguje klienta z waznym tokenem push, resztę oddaje nastepnikowi.
 * Ogniwo nie wie, co jest dalej - kolejnosc sklada korzen kompozycji.
 */
final class PushLink implements Channel
{
    public function __construct(private readonly PushChannel $push, private readonly Channel $next)
    {
    }

    public function deliver(Customer $customer, string $subject, string $body): void
    {
        $token = $customer->pushToken;
        if ($token !== null && trim($token) !== '' && !str_starts_with($token, 'expired-')) {
            $this->push->deliver($customer, $subject, $body);
        } else {
            $this->next->deliver($customer, $subject, $body);
        }
    }
}

<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use Training\Workshop\Exercises\E03Notifications\Customer;

/** Krok 2: kanal SMS - sklejenie tytulu z trescia i limit 70 znakow sa teraz w jednym miejscu. */
final class SmsChannel implements Channel
{
    private const int MAX_LENGTH = 70;

    public function __construct(private readonly Journal $journal)
    {
    }

    public function deliver(Customer $customer, string $subject, string $body): void
    {
        $text = $subject . '. ' . $body;
        if (mb_strlen($text) > self::MAX_LENGTH) {
            $text = mb_substr($text, 0, self::MAX_LENGTH - 3) . '...';
        }
        $this->journal->sms($customer->phone, $text);
    }
}

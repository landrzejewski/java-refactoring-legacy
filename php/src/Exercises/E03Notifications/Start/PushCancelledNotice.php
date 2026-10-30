<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Start;

use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Screening;

/** Start: powiadomienie push o odwolaniu seansu - szosta klasa z tej samej siatki kanal x rodzaj. */
final class PushCancelledNotice
{
    public function __construct(
        private readonly Journal $journal,
        private readonly Screening $screening,
        private readonly string $reason,
    ) {
    }

    public function send(Customer $customer): void
    {
        $subject = 'Odwolany seans: ' . $this->screening->title;
        $body = 'Seans ' . $this->screening->title . ' z ' . $this->screening->start . ' odwolany (' . $this->reason
            . '). Zwrot w ciagu 7 dni.';
        $this->journal->push($customer->pushToken, $subject, $body);
    }
}

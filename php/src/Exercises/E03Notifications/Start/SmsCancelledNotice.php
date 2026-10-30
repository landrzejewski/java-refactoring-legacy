<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Start;

use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Screening;

/** Start: SMS o odwolaniu seansu - tresc z EmailCancelledNotice, skracanie z SmsMovedNotice. */
final class SmsCancelledNotice
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
        $text = $subject . '. ' . $body;
        if (mb_strlen($text) > 70) {
            $text = mb_substr($text, 0, 67) . '...';
        }
        $this->journal->sms($customer->phone, $text);
    }
}

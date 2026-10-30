<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step1;

use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Screening;

/**
 * Krok 1: SMS o przeniesieniu seansu. Tresc skopiowana z EmailMovedNotice, skracanie do 70 znakow
 * skopiowane z SmsCancelledNotice - zmiana limitu SMS wymaga poprawki w dwoch klasach.
 */
final class SmsMovedNotice
{
    public function __construct(
        private readonly Journal $journal,
        private readonly Screening $before,
        private readonly Screening $after,
    ) {
    }

    public function send(Customer $customer): void
    {
        $subject = 'Zmiana seansu: ' . $this->after->title;
        $body = 'Seans ' . $this->after->title . ' z ' . $this->before->start . ' przeniesiony na ' . $this->after->start
            . ', sala ' . $this->after->hall . '. Bilety zachowuja waznosc.';
        $text = $subject . '. ' . $body;
        if (mb_strlen($text) > 70) {
            $text = mb_substr($text, 0, 67) . '...';
        }
        $this->journal->sms($customer->phone, $text);
    }
}

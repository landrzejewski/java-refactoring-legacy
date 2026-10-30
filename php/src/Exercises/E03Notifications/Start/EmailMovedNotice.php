<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Start;

use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Screening;

/** Start: e-mail o przeniesieniu seansu. Tresc jest taka sama jak w SmsMovedNotice i PushMovedNotice. */
final class EmailMovedNotice
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
        $this->journal->email($customer->email, $subject, $body);
    }
}

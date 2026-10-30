<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step1;

use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Screening;

/** Krok 1: e-mail o odwolaniu seansu. Nowy rodzaj wiadomosci (np. zmiana sali) to kolejne trzy klasy. */
final class EmailCancelledNotice
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
        $this->journal->email($customer->email, $subject, $body);
    }
}
